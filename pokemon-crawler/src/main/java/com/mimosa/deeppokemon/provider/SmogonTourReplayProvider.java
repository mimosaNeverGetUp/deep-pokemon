/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.crawler.SmogonTourWinPlayerExtractor;
import com.mimosa.deeppokemon.entity.BattleMatch;
import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.entity.SmogonTourReplay;
import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmogonTourReplayProvider implements ReplayProvider {
    private static final Logger log = LoggerFactory.getLogger(SmogonTourReplayProvider.class);
    private static final String REPLAY_POKEMONSHOWDOWN_COM = "replay.pokemonshowdown.com";
    protected static final String TOUR = "tour";

    private static final Pattern UU_MATCH_PATTERN =
            Pattern.compile(Pattern.quote("UU:") + "(.*)");

    private static final Pattern UU_GROUP_MATCH_PATTERN =
            Pattern.compile(Pattern.quote("UU #") + "(\\d+)");
    private static final Pattern TEAM_PLAYER_LEFT_PATTERN =
            Pattern.compile(Pattern.quote("[") + "(.+)" + Pattern.quote("]") + "(.+)");
    private static final Pattern TEAM_PLAYER_RIGHT_PATTERN =
            Pattern.compile("(.+)" + Pattern.quote("[") + "(.+)" + Pattern.quote("]"));
    protected static final String VS = "vs.";
    protected static final String SUFFIX_TB = " TB";
    protected static final String THREAD_REPLAY_STAGE_CLASS = "div.bbWrapper";
    protected static final String GEN_9_OU = "gen9ou";
    protected static final String PLAYER_ID_FORMAT = "%s_%s_%s";

    private boolean initialized = false;
    private final String tourName;
    private final String replayThreadUrl;
    private final String format;
    private final List<String> stageTitles;
    private final SmogonTourWinPlayerExtractor winPlayerExtractor;

    private final Deque<ReplaySource> replaySources = new LinkedList<>();
    private final Map<String, String> playerTeamCache = new HashMap<>();

    public SmogonTourReplayProvider(String tourName, String replayThreadUrl, String format, List<String> stageTitles
            , SmogonTourWinPlayerExtractor winPlayerExtractor) {
        this.tourName = tourName;
        this.replayThreadUrl = replayThreadUrl;
        this.format = format;
        this.stageTitles = stageTitles;
        this.winPlayerExtractor = winPlayerExtractor;
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
        Set<String> existBattleIds = new HashSet<>();
        try {
            Document doc = Jsoup.connect(replayThreadUrl).timeout(60000).get();
            Elements stages = doc.select(THREAD_REPLAY_STAGE_CLASS);
            int stageIndex = 0;
            String stageTitle = "";

            for (Element stage : stages) {
                Elements aElements = stage.select("a");
                List<Replay> replays = extractReplays(aElements, existBattleIds);
                if (replays.isEmpty()) {
                    continue;
                }

                if (format.equals(GEN_9_OU)) {
                    // some uu battle replay is ou format
                    replays = filterUUReplay(replays, stage);
                }

                if (isTB(stage)) {
                    stageTitle = String.format("%s TB", stageTitle);
                } else {
                    stageTitle = stageTitles.get(stageIndex);
                    ++stageIndex;
                }

                for (Replay replay : replays) {
                    SmogonTourReplay tourReplay = (SmogonTourReplay) replay;
                    tourReplay.setStage(stageTitle);
                    if (winPlayerExtractor != null) {
                        tourReplay.setWinPlayer(winPlayerExtractor.getWinSmogonPlayer(stageTitle,
                                tourReplay.getTourPlayers()));
                    }
                    replaySources.add(new ReplaySource(List.of(TOUR, tourName), Collections.singletonList(tourReplay)));
                }
            }
        } catch (IOException e) {
            log.error("extract replay thread fail", e);
        }
    }

    private List<Replay> filterUUReplay(List<Replay> replays, Element stage) {
        Set<BattleMatch> uuBattleMatch = extractUUBattleMatch(stage);
        List<Replay> filteredReplays = new ArrayList<>();
        for (Replay replay : replays) {
            List<TourPlayer> tourPlayers = ((SmogonTourReplay) replay).getTourPlayers();
            BattleMatch battleMatch = new BattleMatch(tourPlayers.get(0).getName(), tourPlayers.get(1).getName());
            if (uuBattleMatch.contains(battleMatch)) {
                continue;
            }
            filteredReplays.add(replay);
        }

        return filteredReplays;
    }

    private Set<BattleMatch> extractUUBattleMatch(Element replayDoc) {
        AtomicBoolean isUuGroupMatch = new AtomicBoolean(false);
        Set<BattleMatch> uuBattleMatch = new HashSet<>();
        String contents = replayDoc.wholeText();
        contents.lines().forEach(line -> {
            if (line.isBlank()) {
                return;
            }

            Matcher matcher = UU_MATCH_PATTERN.matcher(line);
            if (matcher.find()) {
                String matchText = matcher.group(1).trim();
                List<TourPlayer> playerList = getPlayersByReplayText(matchText);
                uuBattleMatch.add(new BattleMatch(playerList.get(0).getName(), playerList.get(1).getName()));
                return;
            }

            matcher = UU_GROUP_MATCH_PATTERN.matcher(line);
            if (matcher.find()) {
                isUuGroupMatch.set(true);
                return;
            }

            if (isUuGroupMatch.get()) {
                List<TourPlayer> playerList = getPlayersByReplayText(line);
                if (!playerList.isEmpty()) {
                    uuBattleMatch.add(new BattleMatch(playerList.get(0).getName(), playerList.get(1).getName()));
                } else {
                    isUuGroupMatch.set(false);
                }
            }
        });
        return uuBattleMatch;
    }

    private List<Replay> extractReplays(Elements aElements, Set<String> existBattleIds) {
        List<Replay> replays = new ArrayList<>();
        for (Element aElement : aElements) {
            String replay = aElement.attr("abs:href");
            if (replay.contains(REPLAY_POKEMONSHOWDOWN_COM) && replay.contains(format)) {
                String id = extractBattleId(replay);
                if (existBattleIds.add(id)) {
                    String replayText = aElement.ownText();
                    List<TourPlayer> tourPlayers = getPlayersByReplayText(replayText);
                    SmogonTourReplay smogonTourReplay = new SmogonTourReplay(id);
                    smogonTourReplay.setTourName(tourName);
                    smogonTourReplay.setTourPlayers(tourPlayers);
                    replays.add(smogonTourReplay);
                }
            }
        }
        return replays;
    }

    private List<TourPlayer> getPlayersByReplayText(String replayText) {
        List<TourPlayer> tourPlayers = new ArrayList<>();
        String[] players = replayText.split(VS);
        if (players.length <= 1) {
            log.error("getPlayersByReplayText fail, replayText:{}", replayText);
            return Collections.emptyList();
        }
        tourPlayers.add(extractLeftTourPlayer(players[0]));
        tourPlayers.add(extractRightTourPlayer(players[1]));

        return tourPlayers;
    }

    private TourPlayer extractLeftTourPlayer(String playerText) {
        Matcher leftTeamPlayerMatcher = TEAM_PLAYER_LEFT_PATTERN.matcher(playerText);
        Matcher rightTeamPlayerMatcher = TEAM_PLAYER_RIGHT_PATTERN.matcher(playerText);

        if (leftTeamPlayerMatcher.find()) {
            return extractLeftPlayer(leftTeamPlayerMatcher);
        } else if (rightTeamPlayerMatcher.find()) {
            // maybe text format is incorrect
            return extractRightPlayer(rightTeamPlayerMatcher);
        } else {
            String playerName = playerText.trim().toLowerCase();
            String tourPlayerId = String.format(PLAYER_ID_FORMAT, tourName, format, playerName);
            if (playerTeamCache.containsKey(playerName)) {
                return new TourPlayer(playerName, tourPlayerId, playerTeamCache.get(playerName), null);
            } else {
                log.warn("player {} has no team?", playerText);
                return new TourPlayer(playerName, tourPlayerId, null, null);
            }
        }
    }

    private TourPlayer extractRightTourPlayer(String playerText) {
        Matcher leftTeamPlayerMatcher = TEAM_PLAYER_LEFT_PATTERN.matcher(playerText);
        Matcher rightTeamPlayerMatcher = TEAM_PLAYER_RIGHT_PATTERN.matcher(playerText);

        if (rightTeamPlayerMatcher.find()) {
            return extractRightPlayer(rightTeamPlayerMatcher);
        } else if (leftTeamPlayerMatcher.find()) {
            // maybe text format is incorrect
            return extractLeftPlayer(leftTeamPlayerMatcher);
        } else {
            String playerName = playerText.trim().toLowerCase();
            String tourPlayerId = String.format(PLAYER_ID_FORMAT, tourName, format, playerName);
            if (playerTeamCache.containsKey(playerName)) {
                return new TourPlayer(playerName, tourPlayerId, playerTeamCache.get(playerName), null);
            } else {
                log.warn("player {} has no team?", playerText);
                return new TourPlayer(playerName, tourPlayerId, null, null);
            }
        }
    }

    private TourPlayer extractRightPlayer(Matcher rightTeamPlayerMatcher) {
        String playerName = rightTeamPlayerMatcher.group(1).trim().toLowerCase();
        String tourPlayerId = String.format(PLAYER_ID_FORMAT, tourName, format, playerName);
        String team = rightTeamPlayerMatcher.group(2).trim();
        playerTeamCache.put(playerName, team);
        return new TourPlayer(playerName, tourPlayerId, team, null);
    }

    private TourPlayer extractLeftPlayer(Matcher leftTeamPlayerMatcher) {
        String playerName = leftTeamPlayerMatcher.group(2).trim().toLowerCase();
        String team = leftTeamPlayerMatcher.group(1).trim();
        String tourPlayerId = String.format(PLAYER_ID_FORMAT, tourName, format, playerName);
        playerTeamCache.put(playerName, team);
        return new TourPlayer(playerName, tourPlayerId, team, null);
    }

    private boolean isTB(Element replayContent) {
        for (Element element : replayContent.getAllElements()) {
            String text = element.ownText();
            if (text.endsWith(SUFFIX_TB)) {
                return true;
            }
        }
        return false;
    }

    private String extractBattleId(String attr) {
        String[] split = attr.split("/");
        String s = split[split.length - 1];
        return s.split("\\?")[0];
    }
}