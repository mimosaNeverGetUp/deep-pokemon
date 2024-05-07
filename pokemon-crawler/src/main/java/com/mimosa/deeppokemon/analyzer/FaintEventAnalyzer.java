/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.*;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.DamageEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FaintEventAnalyzer implements BattleEventAnalyzer {
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
            DamageEventStat damageEventStat = getPreviousDamageEventStat(battleEvent);
            if (damageEventStat != null) {
                EventTarget damageOf = damageEventStat.damageOf();
                PlayerStat killPlayerStat = battleStat.playerStatList().get(damageOf.playerNumber() - 1);
                PokemonBattleStat pokemonBattleStat =
                        killPlayerStat.getPokemonBattleStat(damageOf.targetName());
                pokemonBattleStat.setKillCount(pokemonBattleStat.getKillCount() + 1);

                BattleHighLight battleHighLight = new BattleHighLight(battleStatus.getTurn(),
                        BattleHighLight.HighLightType.KILL, String.format("%s kill opponent %s by %s",
                        damageOf.targetName(), eventTarget.targetName(), damageEventStat.damageFrom()));
                killPlayerStat.addHighLight(battleHighLight);
            }
        }
    }

    private DamageEventStat getPreviousDamageEventStat(BattleEvent battleEvent) {
        if (battleEvent.getPreviousEvent() != null && battleEvent.getPreviousEvent().getChildrenEvents() != null) {
            return battleEvent.getPreviousEvent().getChildrenEvents()
                    .stream()
                    .filter(e -> e.getBattleEventStat() instanceof DamageEventStat)
                    .map(e -> (DamageEventStat) e.getBattleEventStat())
                    .findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}