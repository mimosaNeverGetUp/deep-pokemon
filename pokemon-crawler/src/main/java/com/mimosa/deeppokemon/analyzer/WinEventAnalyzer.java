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
public class WinEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(WinEventAnalyzer.class);
    private static final String WIN = "win";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(WIN);

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        setLastTurnStat(battleStat, battleStatus);
    }

    private void setLastTurnStat(BattleStat battleStat, BattleStatus battleStatus) {
        TurnStat turnStat = new TurnStat(battleStatus.getTurn());
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