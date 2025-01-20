/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.DamageEventStat;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.entity.stat.BattleDamageStat;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class MoveEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(MoveEventAnalyzer.class);
    private static final String MOVE = "move";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(MOVE);
    protected static final String ITEM = "item";
    protected static final String TRICK = "Trick";
    protected static final String FAIL = "fail";

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(0), battleContext);
        if (eventTarget != null) {
            String move = battleEvent.getContents().get(1);
            PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.playerNumber() - 1);
            PokemonBattleStat pokemonBattleStat = playerStat.getPokemonBattleStat(
                    eventTarget.targetName());
            pokemonBattleStat.setMoveCount(pokemonBattleStat.getMoveCount() + 1);
            playerStat.setMoveCount(playerStat.getMoveCount() + 1);
            battleEvent.setBattleEventStat(new MoveEventStat(eventTarget, move));

            if (StringUtils.equals(move, TRICK)) {
                setTrickItem(battleEvent, battleContext);
            }

            if (isSelfFaintMove(move) && isMoveSucceess(battleEvent)) {
                DamageEventStat damageEventStat = setSelfFaintDamage(move, eventTarget, battleStat, battleContext);
                battleEvent.setBattleEventStat(damageEventStat);
                setBattleDamageStat(battleStat, damageEventStat);
            }
        }
    }

    private DamageEventStat setSelfFaintDamage(String move, EventTarget eventTarget, BattleStat battleStat,
                                               BattleContext battleContext) {
        PokemonStatus pokemonStatus = battleContext.getPlayerStatusList().get(eventTarget.playerNumber() - 1).getPokemonStatus(eventTarget.targetName());
        BigDecimal healthDiff = pokemonStatus.getHealth();
        pokemonStatus.setHealth(healthDiff.subtract(healthDiff));

        EventTarget damageOf = BattleEventUtil.getOpponentTurnStartPokemonTarget(battleContext, eventTarget);
        String damageFrom = move;
        PokemonBattleStat damageOfPokemonStat = battleStat.playerStatList().get(damageOf.playerNumber() - 1)
                .getPokemonBattleStat(damageOf.targetName());
        damageOfPokemonStat.setHealthValue(damageOfPokemonStat.getHealthValue().add(healthDiff));
        damageOfPokemonStat.setAttackValue(damageOfPokemonStat.getAttackValue().add(healthDiff));

        PokemonBattleStat faintPokemonStat = battleStat.playerStatList().get(eventTarget.playerNumber() - 1)
                .getPokemonBattleStat(eventTarget.targetName());
        faintPokemonStat.setHealthValue(faintPokemonStat.getHealthValue().subtract(healthDiff));

        return new DamageEventStat(eventTarget, damageOf, damageFrom, healthDiff);
    }

    private void setBattleDamageStat(BattleStat battleStat, DamageEventStat damageEventStat) {
        if (damageEventStat == null || damageEventStat.damageOf() == null) {
            log.error("can not set damage stat: {}", damageEventStat);
            return;
        }

        if (damageEventStat.damageOf().playerNumber() == damageEventStat.eventTarget().playerNumber()) {
            // self damage, no need to record
            return;
        }

        EventTarget damageOf = damageEventStat.damageOf();
        PokemonBattleStat pokemonBattleStat = battleStat.playerStatList()
                .get(damageOf.playerNumber() - 1).getPokemonBattleStat(damageOf.targetName());
        if (pokemonBattleStat == null) {
            log.error("pokemon not find,can not set damage stat: {}", damageOf);
            return;
        }
        BattleDamageStat battleDamageStat = new BattleDamageStat();
        battleDamageStat.setDamage(damageEventStat.healthDiff());
        battleDamageStat.setDamageOf(damageOf.targetName());
        battleDamageStat.setDamageFrom(damageEventStat.damageFrom());
        battleDamageStat.setDamageTarget(damageEventStat.eventTarget().targetName());
        battleDamageStat.setTriggerCount(1);

        List<BattleDamageStat> existBattleDamageStats = pokemonBattleStat.getBattleDamageStats();
        if (!existBattleDamageStats.contains(battleDamageStat)) {
            existBattleDamageStats.add(battleDamageStat);
        } else {
            BattleDamageStat existStat = existBattleDamageStats.stream()
                    .filter(o -> Objects.equals(o, battleDamageStat)).findFirst().orElseThrow();
            existStat.setDamage(existStat.getDamage().add(battleDamageStat.getDamage()));
            existStat.setTriggerCount(existStat.getTriggerCount() + 1);
        }

        // sort damage by damage target
        Collections.sort(existBattleDamageStats, Comparator.comparing(BattleDamageStat::getDamageTarget)
                .thenComparing(BattleDamageStat::getDamage, Comparator.reverseOrder()));
    }

    private boolean isMoveSucceess(BattleEvent battleEvent) {
        if (battleEvent.getChildrenEvents() == null || battleEvent.getChildrenEvents().isEmpty()) {
            return true;
        }
        for (BattleEvent child : battleEvent.getChildrenEvents()) {
            if (StringUtils.equals(FAIL, child.getType())) {
                return false;
            }
        }

        return true;
    }

    private boolean isSelfFaintMove(String move) {
        return StringUtils.equals(move, "Self-Destruct")
                || StringUtils.equals(move, "Explosion")
                || StringUtils.equals(move, "Misty Explosion")
                || StringUtils.equals(move, "Memento")
                || StringUtils.equals(move, "Lunar Dance")
                || StringUtils.equals(move, "Healing Wish");
    }

    private void setTrickItem(BattleEvent battleEvent, BattleContext battleContext) {
        List<EventTarget> trickTargetList = new ArrayList<>();
        List<String> trickItems = new ArrayList<>();
        for (BattleEvent childrenBattleEvent : battleEvent.getChildrenEvents()) {
            if (StringUtils.equals(childrenBattleEvent.getType(), ITEM)) {
                EventTarget eventTarget = BattleEventUtil.getEventTarget(childrenBattleEvent.getContents().get(0), battleContext);
                String item = childrenBattleEvent.getContents().get(1);
                trickTargetList.add(eventTarget);
                trickItems.add(item);
            }
        }

        if (trickTargetList.size() < 2) {
            log.warn("trick item target {} is invalid, turn {}", trickTargetList, battleContext.getTurn());
            return;
        }

        EventTarget eventTargetA = trickTargetList.get(0);
        EventTarget eventTargetB = trickTargetList.get(1);
        String itemA = trickItems.get(0);
        String itemB = trickItems.get(1);
        battleContext.setPokemonItem(eventTargetA.playerNumber(), eventTargetA.targetName(), itemB);
        battleContext.setPokemonItem(eventTargetB.playerNumber(), eventTargetB.targetName(), itemA);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}