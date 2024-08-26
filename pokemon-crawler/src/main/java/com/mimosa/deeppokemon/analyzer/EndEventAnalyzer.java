/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.EndEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EndEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(EndEventAnalyzer.class);
    private static final String END = "end";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(END);
    private static final int TARGET_INDEX = 0;
    private static final int END_EVENT_INDEX = 1;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().size() < 2) {
            log.debug("can not analyze battle event {}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX), battleContext);
        if (eventTarget == null) {
            log.debug("can not analyze battle event without event target {}", battleEvent);
            return;
        }

        String endEvent = battleEvent.getContents().get(END_EVENT_INDEX);
        if (endEvent.contains("move:")) {
            endEvent = endEvent.replace("move:", "").trim();
        }

        switch (endEvent) {
            case "Future Sight", "Doom Desire" ->
                    setMoveEventTargetOf(battleEvent, battleContext, eventTarget, endEvent);
            default -> log.debug("buff {} start and nothing set?", endEvent);
        }
    }

    private void setMoveEventTargetOf(BattleEvent battleEvent, BattleContext battleContext, EventTarget eventTarget,
                                      String endEvent) {
        int opponentPlayerNumber = 3 - eventTarget.playerNumber();
        EventTarget moveTargetOf =
                battleContext.getPlayerStatusList().get(opponentPlayerNumber - 1).getMoveTarget(endEvent);
        if (moveTargetOf == null) {
            log.warn("can not find move target {}", endEvent);
            return;
        }
        battleEvent.setBattleEventStat(new EndEventStat(moveTargetOf, endEvent));
    }


    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}