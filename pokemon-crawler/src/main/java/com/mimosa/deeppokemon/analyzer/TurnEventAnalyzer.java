/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.TurnPlayerStat;
import com.mimosa.deeppokemon.analyzer.entity.TurnPokemonStat;
import com.mimosa.deeppokemon.analyzer.entity.TurnStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TurnEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(TurnEventAnalyzer.class);
    private static final String TURN = "turn";
    private static final String START = "start";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(START, TURN);

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (START.equals(battleEvent.getType())) {
            return;
        }

        if (battleEvent.getContents().isEmpty()) {
            logger.warn("can not match battle turn by content {}", battleEvent);
        }
        battleStatus.setTurn(Integer.parseInt(battleEvent.getContents().get(0)));
        battleStatus.getPlayerStatusList().forEach(playerStatus -> {
            playerStatus.setTurnStartPokemonName(battleStatus.getTurn(), playerStatus.getActivePokemonName());
            playerStatus.getPokemonStatus(playerStatus.getActivePokemonName())
                    .setLastMoveTurn(battleStatus.getTurn());
        });

        setLastTurnStat(battleStat, battleStatus);
    }

    private void setLastTurnStat(BattleStat battleStat, BattleStatus battleStatus) {
        TurnStat turnStat = new TurnStat(battleStatus.getTurn() - 1);
        for (PlayerStatus playerStatus : battleStatus.getPlayerStatusList()) {
            TurnPlayerStat turnPlayerStat = new TurnPlayerStat();
            int totalHealth = 0;
            for (PokemonStatus pokemonStatus : playerStatus.getPokemonStatusMap().values()) {
                TurnPokemonStat turnPokemonStat = new TurnPokemonStat(pokemonStatus.getPokemonName());
                turnPokemonStat.setHealth(pokemonStatus.getHealth());
                turnPlayerStat.addTurnPokemonStat(turnPokemonStat);
                totalHealth += pokemonStatus.getHealth();
            }
            turnPlayerStat.setTotalHealth(totalHealth);
            turnStat.getTurnPlayerStatList().add(turnPlayerStat);
        }
        battleStat.turnStats().add(turnStat);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}