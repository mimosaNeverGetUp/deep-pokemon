/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.*;
import com.mimosa.deeppokemon.entity.tour.TourBattle;
import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import com.mimosa.deeppokemon.entity.tour.TourTeam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SmogonTourReplayBattleCrawler implements BattleCrawler {
    private static final Logger log = LoggerFactory.getLogger(SmogonTourReplayBattleCrawler.class);
    protected static final int SERIES_BATTLE_SIZE = 3;

    private final ReplayBattleCrawler replayBattleCrawler;

    public SmogonTourReplayBattleCrawler(ReplayBattleCrawler replayBattleCrawler) {
        this.replayBattleCrawler = replayBattleCrawler;
    }

    @Override
    public List<Battle> craw(ReplaySource replaySource) {
        List<TourBattle> battles = new ArrayList<>();
        for (Replay replay : replaySource.replayList()) {
            if (!(replay instanceof SmogonTourReplay)) {
                throw new IllegalArgumentException("Replay is not a SmogonTourReplay");
            }
            SmogonTourReplay tourReplay = (SmogonTourReplay) replay;
            log.info("start to crawl tour {}, stage {}, id {}", tourReplay.getTourName(), tourReplay.getStage(),
                    tourReplay.getId());
            Battle battle;
            try {
                battle = replayBattleCrawler.craw(new ReplaySource(replaySource.replayType(),
                        Collections.singletonList(replay))).get(0);
            } catch (Exception e) {
                if (replaySource.replayList().size() > 1) {
                    log.warn("craw battle {} fail,try craw other series battle", replay.getId());
                    // tie or another fake replay, continue craw other series battle
                    continue;
                }
                throw e;
            }

            TourBattle tourBattle = new TourBattle();
            tourBattle.setBattleID(battle.getBattleID());
            tourBattle.setFormat(battle.getFormat());
            tourBattle.setBattleTeams(battle.getBattleTeams());
            tourBattle.setLog(battle.getLog());
            tourBattle.setPlayers(battle.getPlayers());
            tourBattle.setAvageRating(battle.getAvageRating());
            tourBattle.setDate(battle.getDate());
            tourBattle.setWinner(battle.getWinner());
            tourBattle.setTurnCount(battle.getTurnCount());
            tourBattle.setType(battle.getType());


            tourBattle.setTourId(tourReplay.getTourName());
            tourBattle.setSmogonPlayer(tourReplay.getTourPlayers());
            tourBattle.setStage(tourReplay.getStage());
            TourPlayer winPlayer = tourReplay.getWinPlayer();
            TourPlayer losePlayer = null;
            if (winPlayer != null) {
                losePlayer = tourReplay.getTourPlayers().stream()
                        .filter(tourPlayer -> !StringUtils.equalsIgnoreCase(winPlayer.getName(), tourPlayer.getName()))
                        .findFirst()
                        .orElse(null);
                tourBattle.setWinSmogonPlayerName(winPlayer.getName());
            } else {
                log.warn("tour {} {} battle {} can not get win smogon player", tourReplay.getTourName(),
                        tourReplay.getStage(), battle.getBattleID());
            }


            List<BattleTeam> tourTeams = new ArrayList<>();
            for (BattleTeam battleTeam : battle.getBattleTeams()) {
                TourTeam tourTeam = new TourTeam();
                tourTeam.setId(battleTeam.getId());
                tourTeam.setBattleId(battleTeam.getBattleId());
                tourTeam.setTeamId(battleTeam.getTeamId());
                tourTeam.setBattleDate(battleTeam.getBattleDate());
                tourTeam.setRating(battleTeam.getRating());
                tourTeam.setBattleType(battleTeam.getBattleType());
                tourTeam.setPlayerName(battleTeam.getPlayerName());
                tourTeam.setTier(battleTeam.getTier());
                tourTeam.setTagSet(battleTeam.getTagSet());
                tourTeam.setPokemons(battleTeam.getPokemons());
                tourTeam.setTourId(tourReplay.getTourName());
                tourTeam.setStage(tourReplay.getStage());
                if (StringUtils.equalsIgnoreCase(tourTeam.getPlayerName(), battle.getWinner())) {
                    tourTeam.setPlayer(winPlayer);
                } else {
                    tourTeam.setPlayer(losePlayer);
                }
                tourTeams.add(tourTeam);
            }
            tourBattle.setBattleTeams(tourTeams);
            battles.add(tourBattle);
        }

        if (battles.size() >= SERIES_BATTLE_SIZE) {
            setSeriesBattlesWin(battles);
        }
        return new ArrayList<>(battles);
    }

    private void setSeriesBattlesWin(List<TourBattle> battles) {
        String winSeriesSmogonPlayerName = battles.get(0).getWinSmogonPlayerName();
        String winSeriesPlayerName = getSeriesPlayerName(battles);
        TourPlayer winTourPlayer = battles.get(0).getSmogonPlayer().stream()
                .filter(player -> StringUtils.equals(winSeriesSmogonPlayerName, player.getName()))
                .findFirst()
                .orElse(null);
        TourPlayer lostTourPlayer = battles.get(0).getSmogonPlayer().stream()
                .filter(player -> !StringUtils.equals(winSeriesSmogonPlayerName, player.getName()))
                .findFirst()
                .orElse(null);
        if (winTourPlayer == null || lostTourPlayer == null) {
            log.error("can not get win smogon player or lost tour player in series battle {},tour {},stage {}",
                    battles.get(0).getBattleID(), battles.get(0).getTourId(), battles.get(0).getStage());
            return;
        }

        for (TourBattle battle : battles) {
            if (StringUtils.equals(winSeriesPlayerName, battle.getWinner())) {
                battle.setWinSmogonPlayerName(winSeriesSmogonPlayerName);
            } else {
                battle.setWinSmogonPlayerName(lostTourPlayer.getName());
            }

            for (BattleTeam battleTeam : battle.getBattleTeams()) {
                TourTeam tourTeam = (TourTeam) battleTeam;
                tourTeam.setPlayer(tourTeam.getPlayerName().equals(winSeriesPlayerName) ? winTourPlayer : lostTourPlayer);
            }
        }
    }

    private String getSeriesPlayerName(List<TourBattle> battles) {
        Map<String, Integer> winCountMap = new HashMap<>();
        int maxWinCount = 0;
        String winPlayer = null;
        for (TourBattle battle : battles) {
            Integer winCount = winCountMap.merge(battle.getWinner(), 1, Integer::sum);
            if (winCount > maxWinCount) {
                maxWinCount = winCount;
                winPlayer = battle.getWinner();
            }
        }
        return winPlayer;
    }
}