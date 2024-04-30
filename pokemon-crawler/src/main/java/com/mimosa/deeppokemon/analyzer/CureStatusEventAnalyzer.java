/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CureStatusEventAnalyzer implements BattleEventAnalyzer{
    private static final Logger log = LoggerFactory.getLogger(CureStatusEventAnalyzer.class);
    private static final String CURE_STATUS = "curestatus";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(CURE_STATUS);

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().isEmpty()) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(0), battleStatus);
        if (eventTarget != null) {
            battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1).getPokemonStatus(eventTarget.targetName())
                    .setStatus(null);
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}