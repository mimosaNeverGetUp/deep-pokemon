/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler;


import com.mimosa.deeppokemon.entity.BattleMatch;
import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmogonTourWinPlayerExtractor {
    protected static final Logger log = LoggerFactory.getLogger(SmogonTourWinPlayerExtractor.class);

    protected static final String MEMBER = "member";
    protected static final int TIMEOUT_MS = 60000;
    private static final String FORUMS_THREAD_CLASS = "div.structItem--thread";
    protected static final String THREAD_MESSAGE_CLASS = "div.message-cell--main";
    protected static final String THREAD_COMMENT_CLASS = "div.bbWrapper";
    private static final Pattern DID_NOT_PLAYER_PATTERN =
            Pattern.compile("Did Not Play: (.*)" + Pattern.quote("*"));
    private static final Pattern TB_URL_PATTERN =
            Pattern.compile("(" + Pattern.quote("-tb-") + "|" + Pattern.quote("-tiebreaker-post-") + ")" + "(\\d+)" + Pattern.quote("."));
    private static final Pattern ROUND_2_URL_PATTERN =
            Pattern.compile(Pattern.quote("round-2-") + "(\\d+)" + Pattern.quote("."));

    private String tourForumsUrl;
    private String tourName;
    private List<String> threadSuffixStages;
    private final Map<String, Map<BattleMatch, String>> battleWinMap;
    private boolean init = false;


    public SmogonTourWinPlayerExtractor(String tourForumsUrl, String tourName, List<String> threadSuffixStages) {
        this.tourForumsUrl = tourForumsUrl;
        this.tourName = tourName;
        this.threadSuffixStages = threadSuffixStages;
        this.battleWinMap = new HashMap<>();
    }

    public TourPlayer getWinSmogonPlayer(String stage, List<TourPlayer> tourPlayers) {
        try {
            init();
        } catch (Exception e) {
            log.error("init error", e);
            return null;
        }
        if (tourPlayers == null || tourPlayers.size() < 2) {
            throw new IllegalArgumentException("valid tour player list");
        }
        BattleMatch battleMatch = new BattleMatch(tourPlayers.get(0).getName(),
                tourPlayers.get(1).getName());
        if (!battleWinMap.containsKey(stage)) {
            log.info("can not find battle match for {}", stage);
            return null;
        }

        String winSmogonName = battleWinMap.get(stage).get(battleMatch);
        return tourPlayers.stream()
                .filter(tourPlayer -> StringUtils.equalsIgnoreCase(winSmogonName, tourPlayer.getName()))
                .findFirst()
                .orElse(null);
    }

    private synchronized void init() throws IOException {
        if (!init) {
            Document doc = Jsoup.connect(tourForumsUrl).timeout(TIMEOUT_MS).get();
            Elements threads = doc.select(FORUMS_THREAD_CLASS);
            if (threads.isEmpty()) {
                log.error("can't find threads in {}", tourForumsUrl);
            }

            for (Element thread : threads) {
                extract(thread);
            }
            init = true;
        }
    }

    private void extract(Element thread) throws IOException {
        Elements aElements = thread.select("a");
        for (Element aElement : aElements) {
            String title = aElement.ownText();
            if (!title.contains(tourName)) {
                log.debug("title {} is not match", title);
                continue;
            }
            for (String threadSuffixStage : threadSuffixStages) {
                if (title.contains(threadSuffixStage)) {
                    String absoluteUrl = aElement.attr("abs:href");
                    String relativeUrl = aElement.attr("href");
                    extractMatchThread(absoluteUrl, threadSuffixStage);

                    extractTBOrRound2(threadSuffixStage, relativeUrl, absoluteUrl);
                }
            }
        }
    }

    private void extractTBOrRound2(String threadSuffixStage, String relativeUrl, String absoluteUrl) throws IOException {
        Matcher matcher = TB_URL_PATTERN.matcher(relativeUrl);
        if (matcher.find()) {
            int tbFloor = Integer.parseInt(matcher.group(2));
            extractMoreBattleMatchUpInMatchThread(absoluteUrl, String.format("%s TB", threadSuffixStage), tbFloor);
        }

        matcher = ROUND_2_URL_PATTERN.matcher(relativeUrl);
        if (matcher.find()) {
            int tbFloor = Integer.parseInt(matcher.group(1));
            extractMoreBattleMatchUpInMatchThread(absoluteUrl, String.format("%s Round 2", threadSuffixStage), tbFloor);
        }
    }

    private void extractMoreBattleMatchUpInMatchThread(String threadUrl, String stage, int extrageBatchMatchFloor) throws IOException {
        int page = (int) Math.ceil(extrageBatchMatchFloor / 25F);
        String tbPageUrl = String.format("%spage-%d", threadUrl, page);
        Document doc = Jsoup.connect(tbPageUrl).timeout(TIMEOUT_MS).get();
        Elements messages = doc.select(THREAD_MESSAGE_CLASS);
        if (messages.isEmpty()) {
            log.error("can not find message in {}", tbPageUrl);
        }

        String exceptFloor = "#" + extrageBatchMatchFloor;
        for (Element message : messages) {
            if (message.text().contains(exceptFloor)) {
                Element main = message.select(THREAD_COMMENT_CLASS).first();
                Map<BattleMatch, String> battleWinner = getBattleWinner(main);
                if (battleWinner.isEmpty()) {
                    log.error("can no find pair in url {}", tbPageUrl);
                    throw new ServerErrorException("can no find battle pair in match thread" + tbPageUrl, null);
                }
                battleWinMap.put(stage, battleWinner);
            }
        }
    }

    private void extractMatchThread(String url, String stage) throws IOException {
        Document doc = Jsoup.connect(url).timeout(TIMEOUT_MS).get();
        Element main = doc.select(THREAD_COMMENT_CLASS).first();
        Map<BattleMatch, String> battleWinner = getBattleWinner(main);
        if (battleWinner.isEmpty()) {
            // sometime battle match is put in #2...
            battleWinner = getBattleWinner(doc.select(THREAD_COMMENT_CLASS).get(1));
            if (battleWinner.isEmpty()) {
                log.error("can no find pair in url {}", url);
                throw new ServerErrorException("can no find battle pair in match thread" + url, null);
            }
        }
        battleWinMap.put(stage, battleWinner);
    }

    private Map<BattleMatch, String> getBattleWinner(Element matchDoc) {
        Map<BattleMatch, String> battleWinner = new HashMap<>();

        List<String> didNotPlayers = getDidNotPlay(matchDoc);
        Elements hrefs = matchDoc.select("a");
        String firstPlayerName = null;
        String winnerName = null;
        for (Element href : hrefs) {
            if (!isSmogonPlayerHref(href, didNotPlayers)) {
                continue;
            }

            String playerName = href.text().trim().toLowerCase();
            if (!href.select("b").isEmpty()) {
                // winner is bold text
                winnerName = playerName;
            }
            if (firstPlayerName == null) {
                firstPlayerName = playerName;
            } else {
                BattleMatch battleMatch = new BattleMatch(firstPlayerName, playerName);
                battleWinner.putIfAbsent(battleMatch, winnerName);
                firstPlayerName = null;
                winnerName = null;
            }
        }
        return battleWinner;
    }

    private boolean isSmogonPlayerHref(Element href, List<String> didNotPlayers) {
        String url = href.attr("abs:href");
        if (!url.contains(MEMBER)) {
            return false;
        }

        if (!href.select("s").isEmpty()) {
            // player is replace by another
            return false;
        }


        if (href.parent() != null && href.parent().is("span")) {
            // logo, host, and help members
            return false;
        }

        String playerName = href.text();
        return !didNotPlayers.contains(playerName);
    }

    private List<String> getDidNotPlay(Element matchDoc) {
        List<String> didNotPlay = new ArrayList<>();
        String contents = matchDoc.wholeText();
        Matcher matcher = DID_NOT_PLAYER_PATTERN.matcher(contents);
        while (matcher.find()) {
            didNotPlay.add(matcher.group(1).trim());
        }
        return didNotPlay;
    }
}