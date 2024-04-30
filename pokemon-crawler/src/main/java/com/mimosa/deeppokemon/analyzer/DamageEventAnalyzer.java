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
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.analyzer.utils.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static final Pattern FAINT_PATTERN = Pattern.compile("(\\d+) fnt");

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < 2) {
            log.error("can not match player content: {}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX), battleStatus);
        if (eventTarget != null) {
            int pokemonHealth =
                    Integer.parseInt(getHealth(battleEvent));
            // set health status
            PlayerStatus playerStatus = battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1);
            PokemonStatus pokemonStatus = playerStatus.getPokemonStatus(eventTarget.targetName());
            int healthDiff = pokemonStatus.getHealth() - pokemonHealth;
            pokemonStatus.setHealth(pokemonHealth);

            // set health value、attack value
            DamageEventStat damageEventStat = setHealthValueStat(battleEvent, battleStat, battleStatus, eventTarget,
                    playerStatus, healthDiff);
            battleEvent.setBattleEventStat(damageEventStat);

            setPlayerSwitchDamageStat(battleEvent, battleStat, eventTarget, healthDiff);
        }
    }

    private static String getHealth(BattleEvent battleEvent) {
        String healthContext = battleEvent.getContents().get(HEALTH_INDEX);
        Matcher matcher = FAINT_PATTERN.matcher(healthContext);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return healthContext.split(EventConstants.HEALTH_SPLIT)[0];
        }
    }

    private void setPlayerSwitchDamageStat(BattleEvent battleEvent, BattleStat battleStat, EventTarget eventTarget, int healthDiff) {
        if (battleEvent.getParentEvent() != null && EventConstants.SWITCH_TYPE.equals(battleEvent.getParentEvent().getType())) {
            PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.playerNumber() - 1);
            playerStat.setSwitchDamage(playerStat.getSwitchDamage() + healthDiff);
        }
    }

    private DamageEventStat setHealthValueStat(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus,
                                               EventTarget eventTarget, PlayerStatus targetPlayerStatus, int healthDiff) {
        PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.playerNumber() - 1);
        PokemonBattleStat pokemonBattleStat = playerStat.getPokemonBattleStat(targetPlayerStatus.getTurnStartPokemonName());
        pokemonBattleStat.setHealthValue(pokemonBattleStat.getHealthValue() - healthDiff);

        EventTarget damageOf = null;
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
            setHealthValueStatByFrom(eventTarget, damageFrom, battleStatus, battleStat, targetPlayerStatus, healthDiff);
        } else if (battleEvent.getParentEvent() != null && battleEvent.getParentEvent().getBattleEventStat()
                instanceof MoveEventStat moveEventStat) {
            // get stat by parent move event
            damageOf = moveEventStat.eventTarget();
            damageFrom = moveEventStat.moveName();
        } else {
            // may be is opponent player turn start pokemon
            int opponentPlayerNumber = 3 - eventTarget.playerNumber();
            PlayerStatus oppentPlayerStatus = battleStatus.getPlayerStatusList().get(opponentPlayerNumber - 1);
            damageOf = new EventTarget(opponentPlayerNumber, oppentPlayerStatus.getTurnStartPokemonName(), null);
        }
        if (damageOf != null) {
            damageOfPokemonStat = battleStat.playerStatList().get(damageOf.playerNumber() - 1)
                    .getPokemonBattleStat(damageOf.targetName());
            damageOfPokemonStat.setHealthValue(damageOfPokemonStat.getHealthValue() + healthDiff);
            damageOfPokemonStat.setAttackValue(damageOfPokemonStat.getAttackValue() + healthDiff);
        }
        return new DamageEventStat(eventTarget, damageOf, damageFrom, healthDiff);
    }

    private void setHealthValueStatByFrom(EventTarget eventTarget, String damageFrom, BattleStatus battleStatus,
                                          BattleStat battleStat, PlayerStatus targetPlayerStatus, int healthDiff) {
        if (damageFrom == null) {
            log.warn("can not match damage from");
            return;
        }

        EventTarget ofTarget = null;
        if (isSideDamage(damageFrom)) {
            for (Side side : targetPlayerStatus.getSideListByName(damageFrom)) {
                ofTarget = side.ofTarget();
            }
        } else if (isWeatherDamage(damageFrom, battleStatus.getWeather())) {
            ofTarget = battleStatus.getWeather().ofTarget();
        } else if (isStatusDamage(damageFrom, eventTarget, battleStatus)) {
            ofTarget = targetPlayerStatus.getPokemonStatus(eventTarget.targetName()).getStatus().ofTarget();
        }

        if (ofTarget != null) {
            PokemonBattleStat stat =
                    battleStat.playerStatList().get(ofTarget.playerNumber() - 1).getPokemonBattleStat(ofTarget.targetName());
            if (ofTarget.playerNumber() != eventTarget.playerNumber()) {
                stat.setHealthValue(stat.getHealthValue() + healthDiff);
                stat.setAttackValue(stat.getAttackValue() + healthDiff);
            } else {
                stat.setHealthValue(stat.getHealthValue() - healthDiff);
            }
        }
    }

    private boolean isStatusDamage(String damageFrom, EventTarget eventTarget, BattleStatus battleStatus) {
        Status status =
                battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1).getPokemonStatus(eventTarget.targetName()).getStatus();

        return status != null && status.name().equals(damageFrom);
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