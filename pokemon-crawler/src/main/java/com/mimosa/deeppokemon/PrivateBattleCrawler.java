/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon;

import com.mimosa.deeppokemon.crawler.BattleCrawler;
import com.mimosa.deeppokemon.crawler.ReplayBattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.entity.privcy.PrivateBattle;
import com.mimosa.deeppokemon.entity.privcy.PrivateTeam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrivateBattleCrawler implements BattleCrawler {
    private static final Logger log = LoggerFactory.getLogger(PrivateBattleCrawler.class);

    protected final ReplayBattleCrawler replayBattleCrawler;
    protected final String sourceName;
    protected final String describe;

    public PrivateBattleCrawler(ReplayBattleCrawler replayBattleCrawler, String sourceName, String describe) {
        this.replayBattleCrawler = replayBattleCrawler;
        this.sourceName = sourceName;
        this.describe = describe;
    }

    @Override
    public List<Battle> craw(ReplaySource replaySource) {
        List<PrivateBattle> battles = new ArrayList<>();
        for (Replay replay : replaySource.replayList()) {
            Battle battle;
            log.info("start craw private battle {}", replay.getId());
            try {
                battle = replayBattleCrawler.craw(new ReplaySource(replaySource.replayType(),
                        Collections.singletonList(replay))).get(0);
            } catch (Exception e) {
                log.warn("craw private battle {} fail,try craw other battle", replay.getId(), e);
                continue;
            }
            PrivateBattle privateBattle = buildPrivateBattle(battle);
            battles.add(privateBattle);
        }

        return new ArrayList<>(battles);
    }

    private PrivateBattle buildPrivateBattle(Battle battle) {
        PrivateBattle privateBattle = new PrivateBattle();
        privateBattle.setBattleID(battle.getBattleID());
        privateBattle.setFormat(battle.getFormat());
        privateBattle.setLog(battle.getLog());
        privateBattle.setPlayers(battle.getPlayers());
        privateBattle.setAvageRating(battle.getAvageRating());
        privateBattle.setDate(battle.getDate());
        privateBattle.setWinner(battle.getWinner());
        privateBattle.setTurnCount(battle.getTurnCount());
        privateBattle.setType(battle.getType());
        privateBattle.setDescribe(this.describe);
        privateBattle.setSourceName(this.sourceName);

        List<BattleTeam> battleTeams = new ArrayList<>();
        for(BattleTeam battleTeam : battle.getBattleTeams()){
            PrivateTeam privateTeam = new PrivateTeam();
            privateTeam.setId(battleTeam.getId());
            privateTeam.setBattleId(battleTeam.getBattleId());
            privateTeam.setTeamId(battleTeam.getTeamId());
            privateTeam.setBattleDate(battleTeam.getBattleDate());
            privateTeam.setRating(battleTeam.getRating());
            privateTeam.setBattleType(battleTeam.getBattleType());
            privateTeam.setPlayerName(battleTeam.getPlayerName());
            privateTeam.setTier(battleTeam.getTier());
            privateTeam.setTagSet(battleTeam.getTagSet());
            privateTeam.setPokemons(battleTeam.getPokemons());
            privateTeam.setSourceName(this.sourceName);
            privateTeam.setDescribe(this.describe);
            battleTeams.add(privateTeam);
        }
        privateBattle.setBattleTeams(battleTeams);
        return privateBattle;
    }
}