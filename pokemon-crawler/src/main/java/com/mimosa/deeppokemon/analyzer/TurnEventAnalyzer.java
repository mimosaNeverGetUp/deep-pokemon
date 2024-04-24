/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TurnEventAnalyzer implements BattleEventAnalyzer{
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
        battleStatus.getPlayerStatusList().forEach(playerStatus ->
                playerStatus.setTurnStartPokemonName(playerStatus.getActivePokemonName()));
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}