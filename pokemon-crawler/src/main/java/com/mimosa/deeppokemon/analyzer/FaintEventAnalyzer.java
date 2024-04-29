/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.DamageEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FaintEventAnalyzer implements BattleEventAnalyzer{
    private final static Logger log = LoggerFactory.getLogger(FaintEventAnalyzer.class);
    private static final String FAINT = "faint";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(FAINT);
    private static final int TARGET_INDEX = 0;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().isEmpty()) {
            log.error("can't analyze battle event with empty content");
            return;
        }

        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX), battleStatus);
        if (eventTarget != null) {
            if (battleEvent.getPreviousEvent() != null && battleEvent.getPreviousEvent().getBattleEventStat()
                    instanceof DamageEventStat damageEventStat) {
                EventTarget damageFrom = damageEventStat.damageFrom();
                PokemonBattleStat pokemonBattleStat =
                        battleStat.playerStatList().get(damageFrom.plyayerNumber() - 1).getPokemonBattleStat(damageFrom.targetName());
               pokemonBattleStat.setKillCount(pokemonBattleStat.getKillCount() + 1);
            }
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}