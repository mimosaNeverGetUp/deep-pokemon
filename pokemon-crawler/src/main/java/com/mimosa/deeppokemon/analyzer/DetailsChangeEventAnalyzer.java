/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.analyzer.utils.EventConstants;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DetailsChangeEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(DetailsChangeEventAnalyzer.class);
    private static final String DETAILS_CHANGE = "detailschange";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(DETAILS_CHANGE);

    private static final int TARGET_INDEX = 0;
    private static final int CHANGE_NAME_INDEX = 1;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }

        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX),
                battleContext);
        String detailChangeName = battleEvent.getContents().get(CHANGE_NAME_INDEX).split(EventConstants.NAME_SPLIT)[0];
        if (eventTarget != null) {
            battleContext.getPlayerStatusList().get(eventTarget.playerNumber()-1).
                    setDetailChangeName(eventTarget.nickName(), detailChangeName);
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}