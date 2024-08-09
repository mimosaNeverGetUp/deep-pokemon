/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.*;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.DamageEventStat;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.analyzer.utils.EventConstants;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DamageEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(DamageEventAnalyzer.class);
    private static final String DAMAGE = "damage";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(DAMAGE);
    private static final String STEELBEAM = "steelbeam";
    private static final String SUPERCELLSLAM = "supercellslam";
    private static final String HIGHJUMPKICK = "highjumpkick";
    private static final Set<String> SPECIAL_RECOIL_DAMAGE = Set.of(STEELBEAM, SUPERCELLSLAM, HIGHJUMPKICK);
    private static final int TARGET_INDEX = 0;
    private static final int HEALTH_INDEX = 1;
    private static final int FROM_INDEX = 2;
    private static final int OF_INDEX = 3;
    public static final String STEALTH_ROCK = "Stealth Rock";
    public static final String SPIKES = "Spikes";
    private static final String ITEM = "item";
    private static final String RECOIL = "Recoil";
    private static final Map<String, String> statusFromMap = new HashMap<>();
    private static final String OF = "of";

    static {
        statusFromMap.put("tox", "psn");
    }

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().size() < 2) {
            log.error("can not match player content: {}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX), battleContext);
        if (eventTarget != null) {
            BigDecimal pokemonHealth = BattleEventUtil.getHealthPercentage(battleEvent.getContents().get(HEALTH_INDEX));
            // set health status
            PlayerStatus playerStatus = battleContext.getPlayerStatusList().get(eventTarget.playerNumber() - 1);
            PokemonStatus pokemonStatus = playerStatus.getPokemonStatus(eventTarget.targetName());
            BigDecimal healthDiff = pokemonStatus.getHealth().subtract(pokemonHealth);
            pokemonStatus.setHealth(pokemonHealth);

            // set health value、attack value
            DamageEventStat damageEventStat = setHealthValueStat(battleEvent, battleStat, battleContext, eventTarget,
                    playerStatus, healthDiff);
            battleEvent.setBattleEventStat(damageEventStat);

            setPlayerSwitchDamageStat(battleEvent, battleStat, eventTarget, healthDiff);

            setPokemonItem(battleContext, damageEventStat);
        }
    }

    private static void setPokemonItem(BattleContext battleContext, DamageEventStat damageEventStat) {
        String damageFrom = damageEventStat.damageFrom();
        EventTarget damageOf = damageEventStat.damageOf();
        if (damageFrom != null && damageFrom.contains(ITEM) && damageOf != null) {
            String[] splits = damageFrom.split("item:");
            if (splits.length < 2) {
                log.error("can not get item by from str:{}", damageFrom);
            }
            String item = splits[1].strip();
            battleContext.setPokemonItem(damageOf.playerNumber(), damageOf.targetName(), item);
        }
    }

    private void setPlayerSwitchDamageStat(BattleEvent battleEvent, BattleStat battleStat, EventTarget eventTarget,
                                           BigDecimal healthDiff) {
        if (battleEvent.getParentEvent() != null && EventConstants.SWITCH_TYPE.equals(battleEvent.getParentEvent().getType())) {
            PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.playerNumber() - 1);
            playerStat.setSwitchDamage(playerStat.getSwitchDamage().add(healthDiff));
        }
    }

    private DamageEventStat setHealthValueStat(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext,
                                               EventTarget eventTarget, PlayerStatus targetPlayerStatus,
                                               BigDecimal healthDiff) {
        EventTarget damageOf;
        String damageFrom = null;
        PokemonBattleStat damageOfPokemonStat;
        if (FROM_INDEX <= battleEvent.getContents().size() - 1) {
            damageFrom = BattleEventUtil.getEventFrom(battleEvent.getContents().get(FROM_INDEX));
        }

        if (OF_INDEX < battleEvent.getContents().size() && battleEvent.getContents().get(OF_INDEX).contains(OF)) {
            // get stat by damage from xxx of xxx
            damageOf = BattleEventUtil.getEventTarget(battleEvent.getContents().get(OF_INDEX),
                    battleContext);
        } else if (FROM_INDEX == battleEvent.getContents().size() - 1) {
            // get stat by damage from xxx
            damageOf = getDamageOfByFrom(eventTarget, damageFrom, battleContext, targetPlayerStatus);
        } else if (battleEvent.getParentEvent() != null && battleEvent.getParentEvent().getBattleEventStat()
                instanceof MoveEventStat moveEventStat) {
            // get stat by parent move event
            damageOf = moveEventStat.eventTarget();
            damageFrom = moveEventStat.moveName();
        } else {
            // may be is opponent player turn start pokemon
            damageOf = BattleEventUtil.getOpponentTurnStartPokemonTarget(battleContext, eventTarget);
        }
        if (damageOf != null) {
            damageOfPokemonStat = battleStat.playerStatList().get(damageOf.playerNumber() - 1)
                    .getPokemonBattleStat(damageOf.targetName());
            if (damageOf.playerNumber() != eventTarget.playerNumber()) {
                damageOfPokemonStat.setHealthValue(damageOfPokemonStat.getHealthValue().add(healthDiff));
                damageOfPokemonStat.setAttackValue(damageOfPokemonStat.getAttackValue().add(healthDiff));
                minusTurnStartPokemonHealthStat(battleStat, battleContext, eventTarget.playerNumber(), healthDiff);
            } else {
                damageOfPokemonStat.setHealthValue(damageOfPokemonStat.getHealthValue().subtract(healthDiff));
                addTurnStartPokemonHealthStat(battleStat, battleContext, 3 - eventTarget.playerNumber(),
                        healthDiff);
            }
        } else {
            // default damage of opponent turn start pokemon
            log.warn("can not get damage source from {}", battleEvent);
            minusTurnStartPokemonHealthStat(battleStat, battleContext, eventTarget.playerNumber(), healthDiff);
            addTurnStartPokemonHealthStat(battleStat, battleContext, 3 - eventTarget.playerNumber(),
                    healthDiff);
        }
        return new DamageEventStat(eventTarget, damageOf, damageFrom, healthDiff);
    }

    private void minusTurnStartPokemonHealthStat(BattleStat battleStat, BattleContext battleContext, int playerNumber,
                                                 BigDecimal healthDiff) {
        PokemonBattleStat turnStartPokemonBattleStat =
                getTurnStartPokemonBattleStat(battleStat, battleContext, playerNumber);
        turnStartPokemonBattleStat.setHealthValue(turnStartPokemonBattleStat.getHealthValue().subtract(healthDiff));
    }

    private void addTurnStartPokemonHealthStat(BattleStat battleStat, BattleContext battleContext,
                                               int playerNumber, BigDecimal healthDiff) {
        PokemonBattleStat opponentTurnStartPokemonBattleStat = getTurnStartPokemonBattleStat(battleStat,
                battleContext, playerNumber);
        opponentTurnStartPokemonBattleStat
                .setHealthValue(opponentTurnStartPokemonBattleStat.getHealthValue().add(healthDiff));
        opponentTurnStartPokemonBattleStat
                .setAttackValue(opponentTurnStartPokemonBattleStat.getAttackValue().add(healthDiff));
    }

    private PokemonBattleStat getTurnStartPokemonBattleStat(BattleStat battleStat, BattleContext battleContext,
                                                            int playerNumber) {
        PlayerStat playerStat = battleStat.playerStatList().get(playerNumber - 1);
        PlayerStatus playerStatus = battleContext.getPlayerStatusList().get(playerNumber - 1);
        return playerStat.getPokemonBattleStat(playerStatus.getTurnStartPokemonName());
    }

    private EventTarget getDamageOfByFrom(EventTarget eventTarget, String damageFrom, BattleContext battleContext,
                                          PlayerStatus targetPlayerStatus) {
        if (damageFrom == null) {
            log.warn("can not match damage from");
            return null;
        }

        return getOfTarget(eventTarget, damageFrom, battleContext, targetPlayerStatus);
    }

    private EventTarget getOfTarget(EventTarget eventTarget, String damageFrom, BattleContext battleContext, PlayerStatus targetPlayerStatus) {
        EventTarget ofTarget = null;
        if (isSideDamage(damageFrom)) {
            for (Side side : targetPlayerStatus.getSideListByName(damageFrom)) {
                ofTarget = side.ofTarget();
            }
        } else if (isWeatherDamage(damageFrom, battleContext.getWeather())) {
            ofTarget = battleContext.getWeather().ofTarget();
        } else if (isStatusDamage(damageFrom, eventTarget, battleContext)) {
            ofTarget = targetPlayerStatus.getPokemonStatus(eventTarget.targetName()).getStatus().ofTarget();
        } else if (isItemDamage(damageFrom)) {
            // maybe damage from life orb
            ofTarget = eventTarget;
        } else if (isBuffDamage(damageFrom, targetPlayerStatus.getPokemonStatus(eventTarget.targetName()))) {
            ofTarget = targetPlayerStatus.getPokemonStatus(eventTarget.targetName()).getBuffOf(damageFrom);
        } else if (isRecoilDamage(damageFrom) || isSpecialRecoilDamage(damageFrom)) {
            // recoil is damage by opponent active pokemon
            ofTarget = BattleEventUtil.getOpponentActivePokemonTarget(battleContext, eventTarget);
        } else if (isActivateDamage(damageFrom, targetPlayerStatus.getPokemonStatus(eventTarget.targetName()))) {
            List<ActivateStatus> activateStatuses =
                    targetPlayerStatus.getPokemonStatus(eventTarget.targetName()).getActivateStatusList();
            ofTarget = activateStatuses.stream().filter(activateStatus -> StringUtils.equals(activateStatus.content()
                    , damageFrom) || StringUtils.equals(activateStatus.status(), damageFrom)).findFirst().orElseThrow().ofTarget();
        } else {
            log.error("can not get damage from {},turn: {}", damageFrom, battleContext.getTurn());
        }
        return ofTarget;
    }

    private boolean isRecoilDamage(String damageFrom) {
        return damageFrom.contains(RECOIL);
    }

    private boolean isBuffDamage(String damageFrom, PokemonStatus pokemonStatus) {
        return pokemonStatus.getBuffOf(damageFrom) != null;
    }

    private boolean isItemDamage(String damageFrom) {
        return damageFrom.contains(ITEM);
    }

    private boolean isStatusDamage(String damageFrom, EventTarget eventTarget, BattleContext battleContext) {
        Status status =
                battleContext.getPlayerStatusList().get(eventTarget.playerNumber() - 1).getPokemonStatus(eventTarget.targetName()).getStatus();
        if (status == null) {
            return false;
        }

        return status.name().equals(damageFrom) || StringUtils.equals(statusFromMap.get(status.name()), damageFrom);
    }

    private boolean isWeatherDamage(String damageFrom, Weather weather) {
        return weather != null && weather.name().equals(damageFrom);
    }

    private static boolean isSideDamage(String damageFrom) {
        return damageFrom.equals(STEALTH_ROCK) || damageFrom.equals(SPIKES);
    }

    private boolean isActivateDamage(String damageFrom, PokemonStatus pokemonStatus) {
        return pokemonStatus.getActivateStatusList().stream().anyMatch(activateStatus ->
                StringUtils.equals(activateStatus.content(), damageFrom)
                        || StringUtils.equals(activateStatus.status(), damageFrom));
    }

    private boolean isSpecialRecoilDamage(String damageFrom) {
        return SPECIAL_RECOIL_DAMAGE.contains(damageFrom);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}