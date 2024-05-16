/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.stat.BattleHighLight;import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SideEndEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(SideEndEventAnalyzer.class);
    private static final String SIDE_END = "sideend";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(SIDE_END);
    private static final int TARGET_INDEX = 0;
    private static final int SIDE_INDEX = 1;
    private static final int FROM_INDEX = 2;
    private static final int OF_INDEX = 3;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < FROM_INDEX) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }

        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX));
        if (eventTarget != null) {
            String sideName = battleEvent.getContents().get(SIDE_INDEX);
            EventTarget endOf;
            String endFrom;
            if (battleEvent.getContents().size() - 1 >= OF_INDEX) {
                endOf = BattleEventUtil.getEventTarget(battleEvent.getContents().get(OF_INDEX));
                endFrom = BattleEventUtil.getEventFrom(battleEvent.getContents().get(FROM_INDEX));
            } else {
                endOf = null;
                endFrom = null;
            }
            battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1).getSideList()
                    .removeIf(side -> side.name().equals(sideName));

            String description = String.format("side %s end from %s of %s", sideName, endFrom, endOf);
            BattleHighLight battleHighLight = new BattleHighLight(battleStatus.getTurn(),
                    BattleHighLight.HighLightType.END_SIDE, description);
            battleStat.playerStatList().get(eventTarget.playerNumber() - 1).addHighLight(battleHighLight);
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}