/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class HealEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(HealEventAnalyzer.class);
    private static final String HEAL = "heal";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(HEAL);
    private static final int TARGET_INDEX = 0;
    private static final int HEALTH_INDEX = 1;
    private static final int FROM_INDEX = 2;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }

        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX), battleStatus);
        if (eventTarget != null) {
            BigDecimal health = BattleEventUtil.getHealthPercentage(battleEvent.getContents().get(HEALTH_INDEX));
            PlayerStatus targetPlayerStatus = battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1);
            PokemonStatus pokemonStatus =
                    targetPlayerStatus.getPokemonStatus(eventTarget.targetName());
            BigDecimal healthDiff = health.subtract(pokemonStatus.getHealth());
            String healthFrom = null;
            if (battleEvent.getContents().size() > FROM_INDEX) {
                healthFrom = BattleEventUtil.getEventFrom(battleEvent.getContents().get(FROM_INDEX));
            }

            pokemonStatus.setHealth(health);
            setHealthStat(battleEvent, battleStat, battleStatus, healthFrom, eventTarget, healthDiff);
        }
    }

    private void setHealthStat(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus,
                               String healthFrom, EventTarget eventTarget, BigDecimal healthDiff) {
        // set health value
        EventTarget healthOfTarget;
        if (battleEvent.getParentEvent() != null &&
                battleEvent.getParentEvent().getBattleEventStat() instanceof MoveEventStat moveEventStat) {
            healthOfTarget = moveEventStat.eventTarget();
        } else if (isFieldHealth(healthFrom, battleStatus)) {
            healthOfTarget = battleStatus.getField().eventTarget();
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
                        battleStatus.getPlayerStatusList().get(opponentPlayerNumber - 1);
                PokemonBattleStat opponentPokemonStat =
                        opponentPlayerStat.getPokemonBattleStat(opponentPlayerStatus.getTurnStartPokemonName());
                opponentPokemonStat.setHealthValue(opponentPokemonStat.getHealthValue().add(healthDiff));
            } else {
                healthPokemonStat.setHealthValue(healthPokemonStat.getHealthValue().add(healthDiff));
                int opponentPlayerNumber = 3 - eventTarget.playerNumber();
                PlayerStat opponentPlayerStat = battleStat.playerStatList().get(opponentPlayerNumber - 1);
                PlayerStatus opponentPlayerStatus =
                        battleStatus.getPlayerStatusList().get(opponentPlayerNumber - 1);
                PokemonBattleStat opponentPokemonStat =
                        opponentPlayerStat.getPokemonBattleStat(opponentPlayerStatus.getTurnStartPokemonName());
                opponentPokemonStat.setHealthValue(opponentPokemonStat.getHealthValue().subtract(healthDiff));
                opponentPokemonStat.setAttackValue(opponentPokemonStat.getAttackValue().subtract(healthDiff));
            }
        }
    }

    private boolean isFieldHealth(String from, BattleStatus battleStatus) {
        if (from == null || battleStatus.getField() == null) {
            return false;
        }
        return battleStatus.getField().name().equals(from);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}