/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class StartEventAnalyzer implements BattleEventAnalyzer{
    private static final Logger log = LoggerFactory.getLogger(StartEventAnalyzer.class);
    private static final String START = "start";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(START);
    private static final int TARGET_INDEX = 0;
    private static final int BUFF_INDEX = 1;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < 2) {
            log.debug("can not analyze battle event {}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX), battleStatus);
        if(eventTarget == null){
            log.debug("can not analyze battle event without event target {}", battleEvent);
            return;
        }

        String buff = battleEvent.getContents().get(BUFF_INDEX);
        switch (buff) {
            case "Salt Cure" :
                setSaltBuffOf(battleEvent, battleStatus, eventTarget, buff);
                break;
        }
    }

    private static void setSaltBuffOf(BattleEvent battleEvent, BattleStatus battleStatus, EventTarget eventTarget, String buff) {
        EventTarget saltOf = null;
        if (battleEvent.getParentEvent() != null &&
                battleEvent.getParentEvent().getBattleEventStat() instanceof MoveEventStat moveEventStat) {
            saltOf = moveEventStat.eventTarget();
        }
        BattleEventUtil.getPokemonStatus(battleStatus, eventTarget).setBuffOf(buff, saltOf);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}