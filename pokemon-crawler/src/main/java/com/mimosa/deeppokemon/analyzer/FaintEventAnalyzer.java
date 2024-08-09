/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.DamageEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.entity.stat.BattleHighLight;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
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
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().isEmpty()) {
            log.error("can't analyze battle event with empty content");
            return;
        }

        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX), battleContext);
        if (eventTarget != null) {
            DamageEventStat damageEventStat = getPreviousDamageEventStat(battleEvent);
            if (damageEventStat != null) {
                EventTarget damageOf = damageEventStat.damageOf();
                if (damageOf == null) {
                    log.error("can't analyze damage with empty damageOf: {}, turn: {}", damageEventStat,
                            battleContext.getTurn());
                    throw new RuntimeException("can't analyze damage with empty damageOf: " + damageEventStat);
                }

                setKillCountAndHighLight(battleStat, battleContext, damageOf, eventTarget, damageEventStat);
            }
        }
    }

    private void setKillCountAndHighLight(BattleStat battleStat, BattleContext battleContext, EventTarget damageOf, EventTarget eventTarget, DamageEventStat damageEventStat) {
        if (damageOf.playerNumber() != eventTarget.playerNumber()) {
            PlayerStat killPlayerStat = battleStat.playerStatList().get(damageOf.playerNumber() - 1);
            PokemonBattleStat pokemonBattleStat =
                    killPlayerStat.getPokemonBattleStat(damageOf.targetName());
            pokemonBattleStat.setKillCount(pokemonBattleStat.getKillCount() + 1);

            BattleHighLight battleHighLight = new BattleHighLight(battleContext.getTurn(),
                    BattleHighLight.HighLightType.KILL, String.format("%s kill opponent %s by %s",
                    damageOf.targetName(), eventTarget.targetName(), damageEventStat.damageFrom()));
            killPlayerStat.addHighLight(battleHighLight);
        } else {
            int opponentNumber = 3 - eventTarget.playerNumber();
            PlayerStat opponentStat = battleStat.playerStatList().get(opponentNumber - 1);

            BattleHighLight battleHighLight = new BattleHighLight(battleContext.getTurn(),
                    BattleHighLight.HighLightType.KILL, String.format("opponent %s faint by %s",
                    eventTarget.targetName(), damageEventStat.damageFrom()));
            opponentStat.addHighLight(battleHighLight);
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