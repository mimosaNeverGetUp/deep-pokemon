/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.entity.stat.*;
import com.mimosa.deeppokemon.utils.MatcherUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class HealEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(HealEventAnalyzer.class);
    private static final String HEAL = "heal";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(HEAL);
    private static final int TARGET_INDEX = 0;
    private static final int HEALTH_INDEX = 1;
    private static final int FROM_INDEX = 2;
    private static final int OF_INDEX = 3;
    protected static final String ITEM = "item";
    protected static final String WISH = "Wish";
    protected static final String HEALING_WISH = "Healing Wish";
    protected static final String ABILITY = "ability";
    private static final Pattern ABILITY_PATTERN = Pattern.compile(Pattern.quote("ability: ") + "(.+)");

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }

        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX), battleContext);
        if (eventTarget != null) {
            BigDecimal health = BattleEventUtil.getHealthPercentage(battleEvent.getContents().get(HEALTH_INDEX));
            PlayerStatus targetPlayerStatus = battleContext.getPlayerStatusList().get(eventTarget.playerNumber() - 1);
            PokemonStatus pokemonStatus =
                    targetPlayerStatus.getPokemonStatus(eventTarget.targetName());
            BigDecimal healthDiff = health.subtract(pokemonStatus.getHealth());
            String healthFrom = null;
            if (battleEvent.getContents().size() > FROM_INDEX &&
                    BattleEventUtil.getEventFrom(battleEvent.getContents().get(FROM_INDEX)) != null) {
                healthFrom = BattleEventUtil.getEventFrom(battleEvent.getContents().get(FROM_INDEX));
            } else if (battleEvent.getParentEvent() != null &&
                    battleEvent.getParentEvent().getBattleEventStat() instanceof MoveEventStat moveEventStat) {
                healthFrom = moveEventStat.moveName();
            }

            pokemonStatus.setHealth(health);
            setHealthStat(battleEvent, battleStat, battleContext, healthFrom, eventTarget, healthDiff);
            setPokemonItem(battleContext, healthFrom, eventTarget);

            if (battleEvent.getContents().size() > FROM_INDEX && battleEvent.getContents().get(FROM_INDEX).contains(ABILITY)) {
                log.debug("set heal ability");
                String ability = MatcherUtil.groupMatch(ABILITY_PATTERN, battleEvent.getContents().get(FROM_INDEX), 1);
                battleContext.setPokemonAbility(eventTarget.playerNumber(), eventTarget.targetName(), ability);
            }
        }
    }

    private void setHealthStat(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext,
                               String healthFrom, EventTarget eventTarget, BigDecimal healthDiff) {
        // set health value
        EventTarget healthOfTarget;
        if (battleEvent.getParentEvent() != null &&
                battleEvent.getParentEvent().getBattleEventStat() instanceof MoveEventStat moveEventStat) {
            healthOfTarget = moveEventStat.eventTarget();
        } else if (isFieldHealth(healthFrom, battleContext)) {
            healthOfTarget = battleContext.getField().eventTarget();
        } else if (isWishRecovery(healthFrom)) {
            healthOfTarget = getWishOfTarget(battleEvent, eventTarget, battleContext);
        } else if (isHealingWish(healthFrom)) {
            // no analyze tmp
            return;
        } else {
            healthOfTarget = eventTarget;
        }

        if (healthOfTarget != null) {
            PokemonBattleStat healthPokemonStat =
                    battleStat.playerStatList().get(healthOfTarget.playerNumber() - 1).getPokemonBattleStat(healthOfTarget.targetName());
            if (eventTarget.playerNumber() != healthOfTarget.playerNumber()) {
                healthPokemonStat.setHealthValue(healthPokemonStat.getHealthValue().subtract(healthDiff));
                healthPokemonStat.setAttackValue(healthPokemonStat.getAttackValue().subtract(healthDiff));
                int opponentPlayerNumber = 3 - healthOfTarget.playerNumber();
                PlayerStat opponentPlayerStat = battleStat.playerStatList().get(opponentPlayerNumber - 1);
                PlayerStatus opponentPlayerStatus =
                        battleContext.getPlayerStatusList().get(opponentPlayerNumber - 1);
                PokemonBattleStat opponentPokemonStat =
                        opponentPlayerStat.getPokemonBattleStat(opponentPlayerStatus.getTurnStartPokemonName());
                opponentPokemonStat.setHealthValue(opponentPokemonStat.getHealthValue().add(healthDiff));
            } else {
                healthPokemonStat.setHealthValue(healthPokemonStat.getHealthValue().add(healthDiff));
                int opponentPlayerNumber = 3 - eventTarget.playerNumber();
                PlayerStat opponentPlayerStat = battleStat.playerStatList().get(opponentPlayerNumber - 1);
                PlayerStatus opponentPlayerStatus =
                        battleContext.getPlayerStatusList().get(opponentPlayerNumber - 1);
                PokemonBattleStat opponentPokemonStat =
                        opponentPlayerStat.getPokemonBattleStat(opponentPlayerStatus.getTurnStartPokemonName());
                opponentPokemonStat.setHealthValue(opponentPokemonStat.getHealthValue().subtract(healthDiff));
                opponentPokemonStat.setAttackValue(opponentPokemonStat.getAttackValue().subtract(healthDiff));
                setBattleDamageStat(opponentPokemonStat, healthFrom, eventTarget, healthOfTarget, healthDiff);
                addPokemonHealthValueStat(healthPokemonStat, healthDiff, opponentPokemonStat);
            }
        }
    }

    private void setBattleDamageStat(PokemonBattleStat opponentPokemonStat, String healthFrom, EventTarget eventTarget,
                                     EventTarget healthOfTarget, BigDecimal healthDiff) {

        BattleDamageStat battleDamageStat = new BattleDamageStat();
        battleDamageStat.setDamage(healthDiff.negate());
        battleDamageStat.setDamageOf(healthOfTarget.targetName());
        battleDamageStat.setDamageFrom(healthFrom);
        battleDamageStat.setDamageTarget(eventTarget.targetName());
        battleDamageStat.setTriggerCount(1);

        List<BattleDamageStat> existBattleDamageStats = opponentPokemonStat.getBattleDamageStats();
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

    private void addPokemonHealthValueStat(PokemonBattleStat pokemonBattleStat, BigDecimal healthDiff,
                                           PokemonBattleStat opponentPokemonStat) {
        addPokemonHealthValueStat(pokemonBattleStat, opponentPokemonStat.getName(), healthDiff);
        addPokemonOpponentLossHealthValueStat(opponentPokemonStat, pokemonBattleStat.getName(), healthDiff);
    }

    private void addPokemonHealthValueStat(PokemonBattleStat pokemonStat, String opponentPokemon,
                                           BigDecimal healthDiff) {
        List<HealthValueStat> healthValueStats = pokemonStat.getHealthValueStats();
        HealthValueStat targetHealthValueStat = null;
        for (HealthValueStat healthValueStat : healthValueStats) {
            if (StringUtils.equals(healthValueStat.getOpponentPokemon(), opponentPokemon)) {
                targetHealthValueStat = healthValueStat;
            }
        }


        if (targetHealthValueStat == null) {
            targetHealthValueStat = new HealthValueStat();
            targetHealthValueStat.setOpponentPokemon(opponentPokemon);
            targetHealthValueStat.setHealthValue(BigDecimal.valueOf(0.0));
            targetHealthValueStat.setLossHealthValue(BigDecimal.valueOf(0.0));
            targetHealthValueStat.setOpponentLossHealthValue(BigDecimal.valueOf(0.0));
            healthValueStats.add(targetHealthValueStat);
        }

        targetHealthValueStat.setHealthValue(targetHealthValueStat.getHealthValue().add(healthDiff));
        targetHealthValueStat.setLossHealthValue(targetHealthValueStat.getLossHealthValue().subtract(healthDiff));

    }

    private void addPokemonOpponentLossHealthValueStat(PokemonBattleStat pokemonStat, String opponentPokemon,
                                                       BigDecimal healthDiff) {
        List<HealthValueStat> healthValueStats = pokemonStat.getHealthValueStats();
        HealthValueStat targetHealthValueStat = null;
        for (HealthValueStat healthValueStat : healthValueStats) {
            if (StringUtils.equals(healthValueStat.getOpponentPokemon(), opponentPokemon)) {
                targetHealthValueStat = healthValueStat;
            }
        }


        if (targetHealthValueStat == null) {
            targetHealthValueStat = new HealthValueStat();
            targetHealthValueStat.setOpponentPokemon(opponentPokemon);
            targetHealthValueStat.setHealthValue(BigDecimal.valueOf(0.0));
            targetHealthValueStat.setLossHealthValue(BigDecimal.valueOf(0.0));
            targetHealthValueStat.setOpponentLossHealthValue(BigDecimal.valueOf(0.0));
            healthValueStats.add(targetHealthValueStat);
        }

        targetHealthValueStat.setHealthValue(targetHealthValueStat.getHealthValue().subtract(healthDiff));
        targetHealthValueStat.setOpponentLossHealthValue(targetHealthValueStat.getOpponentLossHealthValue().subtract(healthDiff));
    }

    private EventTarget getWishOfTarget(BattleEvent battleEvent, EventTarget eventTarget, BattleContext battleContext) {
        String ofTargetContent = battleEvent.getContents().get(OF_INDEX);
        if (ofTargetContent.contains("[wisher]")) {
            String[] split = ofTargetContent.split("]");
            if (split.length >= 2) {
                String nickName = split[1].trim();
                String pokemonName = battleContext.getPlayerStatusList()
                        .get(eventTarget.playerNumber() - 1).getPokemonName(nickName);
                return new EventTarget(eventTarget.playerNumber(), pokemonName, nickName);
            } else {
                log.warn("can not find wisher by {}", ofTargetContent);
                return eventTarget;
            }
        } else {
            log.warn("wisher content {} is invalid", ofTargetContent);
            return eventTarget;
        }
    }

    private boolean isWishRecovery(String healthFrom) {
        return StringUtils.equals(WISH, healthFrom);
    }

    private boolean isHealingWish(String healthFrom) {
        return StringUtils.equals(HEALING_WISH, healthFrom);
    }

    private static void setPokemonItem(BattleContext battleContext, String healthFrom, EventTarget eventTarget) {
        if (healthFrom != null && healthFrom.contains(ITEM)) {
            String item;
            if (healthFrom.contains(":")) {
                String[] splits = healthFrom.split(":");
                if (splits.length < 2) {
                    log.error("can not get item by from str:{}", healthFrom);
                    return;
                }
                item = splits[1].strip();
            } else {
                item = healthFrom;
            }
            battleContext.setPokemonItem(eventTarget.playerNumber(), eventTarget.targetName(), item);
        }
    }

    private boolean isFieldHealth(String from, BattleContext battleContext) {
        if (from == null || battleContext.getField() == null) {
            return false;
        }
        return battleContext.getField().name().equals(from);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}