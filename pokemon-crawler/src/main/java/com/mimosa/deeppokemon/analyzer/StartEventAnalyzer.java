/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class StartEventAnalyzer implements BattleEventAnalyzer{
    private static final Logger log = LoggerFactory.getLogger(StartEventAnalyzer.class);
    private static final String START = "start";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(START);

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < 2) {
              // todo add analyze debuff of
//            log.warn("can not analyze battle event {}", battleEvent);
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}