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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SideStartEventAnalyzer implements BattleEventAnalyzer{
    private static final Logger log = LoggerFactory.getLogger(SideStartEventAnalyzer.class);
    private static final String SIDESTART = "sidestart";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(SIDESTART);
    public static final int TARGET_INDEX = 0;
    public static final int MOVE_INDEX = 1;
    public static final String MOVE_SPLIT = ":";

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event, content size is less than 2:{}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX));
        if (eventTarget != null) {
            String sideName = battleEvent.getContents().get(MOVE_INDEX).split(MOVE_SPLIT)[1].strip();
            EventTarget fromTarget = null;
            if (battleEvent.getParentEvent() != null && battleEvent.getParentEvent().getBattleEventStat()
                    instanceof MoveEventStat moveEventStat) {
                fromTarget = moveEventStat.eventTarget();
            }
            battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1)
                    .addSide(new Side(sideName, fromTarget));
            BattleHighLight highLight = new BattleHighLight(battleStatus.getTurn(),
                    BattleHighLight.HighLightType.SIDE, String.format("side %s start", sideName));
            battleStat.playerStatList().get(eventTarget.playerNumber()-1).addHighLight(highLight);
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}