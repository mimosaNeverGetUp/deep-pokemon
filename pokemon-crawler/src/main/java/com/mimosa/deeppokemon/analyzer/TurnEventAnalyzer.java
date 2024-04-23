/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.BattleStatus;
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
        if (START.equals(battleEvent.type())) {
            return;
        }

        if (battleEvent.contents().isEmpty()) {
            logger.warn("can not match battle turn by content {}", battleEvent);
        }
        battleStatus.setTurn(Integer.parseInt(battleEvent.contents().get(0)));
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.type());
    }
}