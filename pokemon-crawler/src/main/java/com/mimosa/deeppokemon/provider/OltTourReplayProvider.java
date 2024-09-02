/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.crawler.SmogonTourWinPlayerExtractor;
import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.entity.SmogonTourReplay;
import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OltTourReplayProvider implements ReplayProvider {
    private static final Logger log = LoggerFactory.getLogger(OltTourReplayProvider.class);

    protected static final String REPLAY_POKEMONSHOWDOWN_COM = "replay.pokemonshowdown.com";
    protected static final String THREAD_REPLAY_STAGE_CLASS = "div.bbWrapper";
    protected static final String MEMBER = "member";
    protected static final String PLAYER_ID_FORMAT = "%s_%s_%s";
    protected static final String TOUR = "tour";
    private static final Pattern STAGE_PATTERN =
            Pattern.compile(Pattern.quote("-=") + "(.+)" + Pattern.quote("=-"));
    protected static final String OU = "ou";

    private boolean initialized = false;
    private final String tourName;
    private final String replayThreadUrl;
    private final String format;
    Set<String> stageTitles;
    private final SmogonTourWinPlayerExtractor smogonTourWinPlayerExtractor;
    private final Deque<ReplaySource> replaySources = new LinkedList<>();

    public OltTourReplayProvider(String tourName, String replayThreadUrl, String format, Set<String> stageTitles,
                                 SmogonTourWinPlayerExtractor winPlayerExtractor) {
        this.tourName = tourName;
        this.replayThreadUrl = replayThreadUrl;
        this.format = format;
        this.smogonTourWinPlayerExtractor = winPlayerExtractor;
        this.stageTitles = stageTitles;
    }

    @Override
    public ReplaySource next() {
        return replaySources.isEmpty() ? null : replaySources.pollFirst();
    }

    @Override
    public boolean hasNext() {
        init();
        return !replaySources.isEmpty();
    }

    private synchronized void init() {
        if (!initialized) {
            extractFromReplayThread();
            initialized = true;
        }
    }

    private void extractFromReplayThread() {
        try {
            Document doc = Jsoup.connect(replayThreadUrl).timeout(60000).get();
            Element replayComment = doc.select(THREAD_REPLAY_STAGE_CLASS).first();
            if (replayComment == null) {
                log.error("Replay thread comment is null");
                return;
            }
            replaySources.addAll(extractReplaySources(replayComment));
        } catch (IOException e) {
            throw new ServerErrorException("request smogon fail",e);
        }
    }

    private String extractStage(Element element) {
        String content = element.text().trim();
        if (stageTitles.contains(content)) {
            return content;
        }

        Matcher matcher = STAGE_PATTERN.matcher(element.ownText());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private List<ReplaySource> extractReplaySources(Element replayComment) {
        List<ReplaySource> allReplaySources = new ArrayList<>();
        String currentStage = null;
        List<String> playerNames = new ArrayList<>();
        List<Replay> seriesReplays = new ArrayList<>();
        for (var ele : replayComment.getAllElements()) {
            if (ele.is("b")) {
                String stage = extractStage(ele);
                if (stage != null) {
                    // another round match, build last series replay source
                    addSeriesToReplaySources(allReplaySources, currentStage, seriesReplays, playerNames);
                    currentStage = stage;
                }
            }

            if (ele.is("a") && currentStage == null) {
                // logo, host, and help members href or no player href
                continue;
            }
            String href = ele.attr("abs:href");
            if (href.contains(MEMBER)) {
                if (playerNames.size() >= 2) {
                    // another player match, build last series replay source
                    addSeriesToReplaySources(allReplaySources, currentStage, seriesReplays, playerNames);
                }
                playerNames.add(ele.text().trim().toLowerCase());
            } else if (href.contains(REPLAY_POKEMONSHOWDOWN_COM) && href.contains(OU)) {
                String battleId = extractBattleId(href);
                SmogonTourReplay replay = new SmogonTourReplay(battleId);
                replay.setTourName(tourName);
                replay.setStage(currentStage);
                List<TourPlayer> tourPlayers = buildTourPlayers(playerNames);
                replay.setTourPlayers(tourPlayers);
                replay.setWinPlayer(smogonTourWinPlayerExtractor.getWinSmogonPlayer(currentStage, tourPlayers));
                seriesReplays.add(replay);
            }
        }

        addSeriesToReplaySources(allReplaySources, currentStage, seriesReplays, playerNames);
        return allReplaySources;
    }

    private void addSeriesToReplaySources(List<ReplaySource> replaySources, String currentStage, List<Replay> seriesReplays, List<String> playerNames) {
        if (seriesReplays.isEmpty()) {
            return;
        }

        replaySources.add(new ReplaySource(List.of(TOUR, tourName, currentStage),
                new ArrayList<>(seriesReplays)));
        playerNames.clear();
        seriesReplays.clear();
    }

    private List<TourPlayer> buildTourPlayers(List<String> playerNames) {
        List<TourPlayer> tourPlayers = new ArrayList<>();
        for (String playerName : playerNames) {
            String tourPlayerId = String.format(PLAYER_ID_FORMAT, tourName, format, playerName);
            TourPlayer tourPlayer = new TourPlayer(playerName, tourPlayerId, null, null);
            tourPlayers.add(tourPlayer);
        }
        return tourPlayers;
    }

    private String extractBattleId(String href) {
        String[] split = href.split("/");
        String s = split[split.length - 1];
        return s.split("\\?")[0];
    }
}