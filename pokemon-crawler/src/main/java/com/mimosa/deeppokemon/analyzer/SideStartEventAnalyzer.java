/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleHighLight;
import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Side;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.analyzer.utils.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SideStartEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(SideStartEventAnalyzer.class);
    private static final String SIDESTART = "sidestart";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(SIDESTART);
    private static final int TARGET_INDEX = 0;
    private static final int MOVE_INDEX = 1;
    private static final String ACTIVATE = "activate";

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event, content size is less than 2:{}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX));
        if (eventTarget != null) {
            String sideName = getSideName(battleEvent);
            EventTarget ofTarget = null;
            if (battleEvent.getParentEvent() != null && battleEvent.getParentEvent().getBattleEventStat()
                    instanceof MoveEventStat moveEventStat) {
                BattleEvent previousEvent = BattleEventUtil.getPreviousChildrenEvent(battleEvent);
                if (previousEvent != null && ACTIVATE.equals(previousEvent.getType())) {
                    // ofTarget is ability target, no move target, such as toxic debris
                    ofTarget = BattleEventUtil.getEventTarget(previousEvent.getContents().get(TARGET_INDEX), battleStatus);
                } else {
                    ofTarget = moveEventStat.eventTarget();
                }
            }
            battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1)
                    .addSide(new Side(sideName, ofTarget));
            BattleHighLight highLight = new BattleHighLight(battleStatus.getTurn(),
                    BattleHighLight.HighLightType.SIDE, String.format("side %s start", sideName));
            battleStat.playerStatList().get(eventTarget.playerNumber() - 1).addHighLight(highLight);
        }
    }

    private String getSideName(BattleEvent battleEvent) {
        String sideName = battleEvent.getContents().get(MOVE_INDEX);
        if (sideName.contains(EventConstants.MOVE_SPLIT)) {
            sideName = sideName.split(EventConstants.MOVE_SPLIT)[1].strip();
        }
        return sideName;
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}