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
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ItemEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(ItemEventAnalyzer.class);
    protected static final String ITEM = "item";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(ITEM);
    protected static final int TARGET_INDEX = 0;
    protected static final int ITEM_INDEX = 1;
    protected static final int FROM_INDEX = 2;
    protected static final int OF_INDEX = 3;
    protected static final String MAGICIAN = "ability: Magician";
    protected static final String PICKPOCKET = "ability: Pickpocket";

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }

        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX), battleContext);
        if (eventTarget != null) {
            String item = battleEvent.getContents().get(ITEM_INDEX);
            if (isFromMagician(battleEvent) && battleEvent.getContents().size() > OF_INDEX) {
                EventTarget opponentTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(OF_INDEX), battleContext);
                battleContext.setPokemonItem(opponentTarget.playerNumber(), opponentTarget.targetName(), item);
            } else {
                battleContext.setPokemonItem(eventTarget.playerNumber(), eventTarget.targetName(), item);
            }
        }
    }

    private boolean isFromMagician(BattleEvent battleEvent) {
        if (battleEvent.getContents().size() <= FROM_INDEX) {
            return false;
        }
        String from = battleEvent.getContents().get(FROM_INDEX);
        return from != null && (from.contains(MAGICIAN) || from.contains(PICKPOCKET));
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}