/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FieldEndEventAnalyzer implements BattleEventAnalyzer{
    private static final Logger log = LoggerFactory.getLogger(FieldEndEventAnalyzer.class);
    private static final String FIELD_END = "fieldend";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(FIELD_END);

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().isEmpty()) {
            log.warn("can not analyze battle event: {}", battleEvent);
            return;
        }

        battleContext.setField(null);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}