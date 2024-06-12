/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Side;
import com.mimosa.deeppokemon.analyzer.entity.Status;
import com.mimosa.deeppokemon.analyzer.entity.Weather;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.DamageEventStat;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
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
import java.util.Map;
import java.util.Set;

@Component
public class DamageEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(DamageEventAnalyzer.class);
    private static final String DAMAGE = "damage";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(DAMAGE);
    private static final int TARGET_INDEX = 0;
    private static final int HEALTH_INDEX = 1;
    private static final int OF_INDEX = 3;
    private static final int FROM_INDEX = 2;
    public static final String STEALTH_ROCK = "Stealth Rock";
    public static final String SPIKES = "Spikes";
    private static final String ITEM = "item";
    private static final String RECOIL = "Recoil";
    private static final Map<String, String> statusFromMap = new HashMap<>();

    static {
        statusFromMap.put("tox", "psn");
    }

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < 2) {
            log.error("can not match player content: {}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX), battleStatus);
        if (eventTarget != null) {
            BigDecimal pokemonHealth = BattleEventUtil.getHealthPercentage(battleEvent.getContents().get(HEALTH_INDEX));
            // set health status
            PlayerStatus playerStatus = battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1);
            PokemonStatus pokemonStatus = playerStatus.getPokemonStatus(eventTarget.targetName());
            BigDecimal healthDiff = pokemonStatus.getHealth().subtract(pokemonHealth);
            pokemonStatus.setHealth(pokemonHealth);

            // set health value、attack value
            DamageEventStat damageEventStat = setHealthValueStat(battleEvent, battleStat, battleStatus, eventTarget,
                    playerStatus, healthDiff);
            battleEvent.setBattleEventStat(damageEventStat);

            setPlayerSwitchDamageStat(battleEvent, battleStat, eventTarget, healthDiff);
        }
    }

    private void setPlayerSwitchDamageStat(BattleEvent battleEvent, BattleStat battleStat, EventTarget eventTarget,
                                           BigDecimal healthDiff) {
        if (battleEvent.getParentEvent() != null && EventConstants.SWITCH_TYPE.equals(battleEvent.getParentEvent().getType())) {
            PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.playerNumber() - 1);
            playerStat.setSwitchDamage(playerStat.getSwitchDamage().add(healthDiff));
        }
    }

    private DamageEventStat setHealthValueStat(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus,
                                               EventTarget eventTarget, PlayerStatus targetPlayerStatus,
                                               BigDecimal healthDiff) {
        EventTarget damageOf;
        String damageFrom = null;
        PokemonBattleStat damageOfPokemonStat;
        if (FROM_INDEX <= battleEvent.getContents().size() - 1) {
            damageFrom = BattleEventUtil.getEventFrom(battleEvent.getContents().get(FROM_INDEX));
        }

        if (OF_INDEX < battleEvent.getContents().size()) {
            // get stat by damage from xxx of xxx
            damageOf = BattleEventUtil.getEventTarget(battleEvent.getContents().get(OF_INDEX),
                    battleStatus);
        } else if (FROM_INDEX == battleEvent.getContents().size() - 1) {
            // get stat by damage from xxx
            damageOf = getDamageOfByFrom(eventTarget, damageFrom, battleStatus, targetPlayerStatus);
        } else if (battleEvent.getParentEvent() != null && battleEvent.getParentEvent().getBattleEventStat()
                instanceof MoveEventStat moveEventStat) {
            // get stat by parent move event
            damageOf = moveEventStat.eventTarget();
            damageFrom = moveEventStat.moveName();
        } else {
            // may be is opponent player turn start pokemon
            damageOf = BattleEventUtil.getOpponentTurnStartPokemonTarget(battleStatus, eventTarget);
        }
        if (damageOf != null) {
            damageOfPokemonStat = battleStat.playerStatList().get(damageOf.playerNumber() - 1)
                    .getPokemonBattleStat(damageOf.targetName());
            if (damageOf.playerNumber() != eventTarget.playerNumber()) {
                damageOfPokemonStat.setHealthValue(damageOfPokemonStat.getHealthValue().add(healthDiff));
                damageOfPokemonStat.setAttackValue(damageOfPokemonStat.getAttackValue().add(healthDiff));
                minusTurnStartPokemonHealthStat(battleStat, battleStatus, eventTarget.playerNumber(), healthDiff);
            } else {
                damageOfPokemonStat.setHealthValue(damageOfPokemonStat.getHealthValue().subtract(healthDiff));
                addTurnStartPokemonHealthStat(battleStat, battleStatus, 3 - eventTarget.playerNumber(),
                        healthDiff);
            }
        } else {
            // default damage of opponent turn start pokemon
            log.warn("can not get damage source from {}", battleEvent);
            minusTurnStartPokemonHealthStat(battleStat, battleStatus, eventTarget.playerNumber(), healthDiff);
            addTurnStartPokemonHealthStat(battleStat, battleStatus, 3 - eventTarget.playerNumber(),
                    healthDiff);
        }
        return new DamageEventStat(eventTarget, damageOf, damageFrom, healthDiff);
    }

    private void minusTurnStartPokemonHealthStat(BattleStat battleStat, BattleStatus battleStatus, int playerNumber,
                                                 BigDecimal healthDiff) {
        PokemonBattleStat turnStartPokemonBattleStat =
                getTurnStartPokemonBattleStat(battleStat, battleStatus, playerNumber);
        turnStartPokemonBattleStat.setHealthValue(turnStartPokemonBattleStat.getHealthValue().subtract(healthDiff));
    }

    private void addTurnStartPokemonHealthStat(BattleStat battleStat, BattleStatus battleStatus,
                                               int playerNumber, BigDecimal healthDiff) {
        PokemonBattleStat opponentTurnStartPokemonBattleStat = getTurnStartPokemonBattleStat(battleStat,
                battleStatus, playerNumber);
        opponentTurnStartPokemonBattleStat
                .setHealthValue(opponentTurnStartPokemonBattleStat.getHealthValue().add(healthDiff));
        opponentTurnStartPokemonBattleStat
                .setAttackValue(opponentTurnStartPokemonBattleStat.getAttackValue().add(healthDiff));
    }

    private PokemonBattleStat getTurnStartPokemonBattleStat(BattleStat battleStat, BattleStatus battleStatus,
                                                            int playerNumber) {
        PlayerStat playerStat = battleStat.playerStatList().get(playerNumber - 1);
        PlayerStatus playerStatus = battleStatus.getPlayerStatusList().get(playerNumber - 1);
        return playerStat.getPokemonBattleStat(playerStatus.getTurnStartPokemonName());
    }

    private EventTarget getDamageOfByFrom(EventTarget eventTarget, String damageFrom, BattleStatus battleStatus,
                                          PlayerStatus targetPlayerStatus) {
        if (damageFrom == null) {
            log.warn("can not match damage from");
            return null;
        }

        return getOfTarget(eventTarget, damageFrom, battleStatus, targetPlayerStatus);
    }

    private EventTarget getOfTarget(EventTarget eventTarget, String damageFrom, BattleStatus battleStatus, PlayerStatus targetPlayerStatus) {
        EventTarget ofTarget = null;
        if (isSideDamage(damageFrom)) {
            for (Side side : targetPlayerStatus.getSideListByName(damageFrom)) {
                ofTarget = side.ofTarget();
            }
        } else if (isWeatherDamage(damageFrom, battleStatus.getWeather())) {
            ofTarget = battleStatus.getWeather().ofTarget();
        } else if (isStatusDamage(damageFrom, eventTarget, battleStatus)) {
            ofTarget = targetPlayerStatus.getPokemonStatus(eventTarget.targetName()).getStatus().ofTarget();
        } else if (isItemDamage(damageFrom)) {
            // maybe damage from life orb
            ofTarget = eventTarget;
        } else if (isBuffDamage(damageFrom, targetPlayerStatus.getPokemonStatus(eventTarget.targetName()))) {
            ofTarget = targetPlayerStatus.getPokemonStatus(eventTarget.targetName()).getBuffOf(damageFrom);
        } else if (isRecoilDamage(damageFrom)) {
            // recoil is damage by opponent active pokemon
            ofTarget = BattleEventUtil.getOpponentActivePokemonTarget(battleStatus, eventTarget);
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

    private boolean isStatusDamage(String damageFrom, EventTarget eventTarget, BattleStatus battleStatus) {
        Status status =
                battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1).getPokemonStatus(eventTarget.targetName()).getStatus();
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

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}