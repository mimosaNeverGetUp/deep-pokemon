/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Field;
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
public class FieldStartEventAnalyzer implements BattleEventAnalyzer{
    private static final Logger log = LoggerFactory.getLogger(FieldStartEventAnalyzer.class);
    private static final String FIELD_START = "fieldstart";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(FIELD_START);
    private static final int FIELD_INDEX = 0;
    private static final int OF_INDEX = 2;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().isEmpty()) {
            log.warn("can not analyze battle event: {}", battleEvent);
            return;
        }

        String field;
        EventTarget ofTarget = null;
        if (battleEvent.getContents().get(FIELD_INDEX).contains(EventConstants.MOVE_SPLIT)) {
            field = battleEvent.getContents().get(FIELD_INDEX).split(EventConstants.MOVE_SPLIT)[1].strip();
        } else {
            field = battleEvent.getContents().get(FIELD_INDEX);
        }
        if (battleEvent.getContents().size() > OF_INDEX) {
            ofTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(OF_INDEX), battleStatus);
        } else if (battleEvent.getParentEvent() != null &&
                battleEvent.getParentEvent().getBattleEventStat() instanceof MoveEventStat moveEventStat) {
            ofTarget = moveEventStat.eventTarget();
        }
        battleStatus.setField(new Field(field, ofTarget));
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}