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
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SmogonTourReplayBattleCrawler implements BattleCrawler {
    private static final Logger log = LoggerFactory.getLogger(SmogonTourReplayBattleCrawler.class);
    public static final String REPLAY_URL_PATTERN = "https://replay.pokemonshowdown.com/%s.json";

    private final BattleReplayExtractor battleReplayExtractor;
    private final SmogonTourWinPlayerExtractor smogonTourWinPlayerExtractor;

    public SmogonTourReplayBattleCrawler(BattleReplayExtractor battleReplayExtractor, SmogonTourWinPlayerExtractor smogonTourWinPlayerExtractor) {
        this.battleReplayExtractor = battleReplayExtractor;
        this.smogonTourWinPlayerExtractor = smogonTourWinPlayerExtractor;
    }

    @Override
    public TourBattle craw(Replay replay) {
        if (!(replay instanceof SmogonTourReplay)) {
            throw new IllegalArgumentException("Replay is not a SmogonTourReplay");
        }
        SmogonTourReplay tourReplay = (SmogonTourReplay) replay;
        ClassicHttpRequest httpGet = initGet(replay.getId());
        BattleReplayData battleReplay = HttpUtil.request(httpGet, BattleReplayData.class);
        Battle battle = battleReplayExtractor.extract(battleReplay);


        TourBattle tourBattle = new TourBattle();
        tourBattle.setBattleID(battle.getBattleID());
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
        TourPlayer winPlayer = smogonTourWinPlayerExtractor.getWinSmogonPlayer(tourReplay.getStage(),
                tourReplay.getTourPlayers());
        TourPlayer losePlayer = null;
        if (winPlayer != null) {
            losePlayer = tourReplay.getTourPlayers().stream()
                    .filter(tourPlayer -> StringUtils.equalsIgnoreCase(winPlayer.getName(), tourPlayer.getName()))
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
        return tourBattle;
    }

    private ClassicHttpRequest initGet(String battleId) {
        return ClassicRequestBuilder.get(String.format(REPLAY_URL_PATTERN, battleId)).build();
    }
}