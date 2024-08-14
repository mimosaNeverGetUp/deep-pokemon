/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.utils;

import com.mimosa.deeppokemon.analyzer.BattleEventAnalyzer;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TeraEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(TeraEventAnalyzer.class);

    protected static final String TERASTALLIZE = "terastallize";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(TERASTALLIZE);

    private static final int TARGET_INDEX = 0;
    private static final int TERA_INDEX = 1;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }

        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX),
                battleContext);
        String teraType = battleEvent.getContents().get(TERA_INDEX);
        if (eventTarget != null) {
            battleContext.setPokemonTeraType(eventTarget.playerNumber(), eventTarget.targetName(), teraType);
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}