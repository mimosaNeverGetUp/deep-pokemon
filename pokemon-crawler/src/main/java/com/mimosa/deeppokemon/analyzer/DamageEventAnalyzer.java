/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.*;
import com.mimosa.deeppokemon.analyzer.entity.event.DamageEventStat;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.analyzer.utils.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < 2) {
            log.error("can not match player content: {}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX), battleStatus);
        if (eventTarget != null) {
            int pokemonHealth =
                    Integer.parseInt(battleEvent.getContents().get(HEALTH_INDEX).split(EventConstants.HEALTH_SPLIT)[0]);
            // set health status
            PlayerStatus playerStatus = battleStatus.getPlayerStatusList().get(eventTarget.plyayerNumber() - 1);
            PokemonStatus pokemonStatus = playerStatus.getPokemonStatus(eventTarget.targetName());
            int healthDiff = pokemonStatus.getHealth() - pokemonHealth;
            pokemonStatus.setHealth(pokemonHealth);

            // set health value、attack value
            EventTarget damageFrom = setHealthValueStat(battleEvent, battleStat, battleStatus, eventTarget,
                    playerStatus, healthDiff);
            battleEvent.setBattleEventStat(new DamageEventStat(eventTarget, damageFrom, healthDiff));

            setPlayerSwitchDamageStat(battleEvent, battleStat, eventTarget, healthDiff);
        }
    }

    private void setPlayerSwitchDamageStat(BattleEvent battleEvent, BattleStat battleStat, EventTarget eventTarget, int healthDiff) {
        if (battleEvent.getParentEvent() != null && EventConstants.SWITCH_TYPE.equals(battleEvent.getParentEvent().getType())) {
            PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.plyayerNumber() - 1);
            playerStat.setSwitchDamage(playerStat.getSwitchDamage() + healthDiff);
        }
    }

    private EventTarget setHealthValueStat(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus,
                                           EventTarget eventTarget, PlayerStatus targetPlayerStatus, int healthDiff) {
        PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.plyayerNumber() - 1);
        PokemonBattleStat pokemonBattleStat = playerStat.getPokemonBattleStat(targetPlayerStatus.getTurnStartPokemonName());
        pokemonBattleStat.setHealthValue(pokemonBattleStat.getHealthValue() - healthDiff);

        EventTarget damageFrom = null;
        PokemonBattleStat damageOfPokemonStat = null;
        if (OF_INDEX < battleEvent.getContents().size()) {
            // get stat by damage from xxx of xxx
            damageFrom = BattleEventUtil.getEventTarget(battleEvent.getContents().get(OF_INDEX),
                    battleStatus);
        } else if (FROM_INDEX == battleEvent.getContents().size() - 1) {
            // get stat by damage from xxx
            setHealthValueStatByFrom(battleEvent, battleStat, targetPlayerStatus, healthDiff);
        } else if (battleEvent.getParentEvent() != null && battleEvent.getParentEvent().getBattleEventStat()
                instanceof MoveEventStat moveEventStat) {
            // get stat by parent move event
            damageFrom = moveEventStat.eventTarget();
        } else {
            // may be is opponent player turn start pokemon
            int opponentPlayerNumber = 3 - eventTarget.plyayerNumber();
            PlayerStatus oppentPlayerStatus = battleStatus.getPlayerStatusList().get(opponentPlayerNumber - 1);
            damageFrom = new EventTarget(opponentPlayerNumber, oppentPlayerStatus.getTurnStartPokemonName(), null);
        }
        if (damageFrom != null) {
            damageOfPokemonStat = battleStat.playerStatList().get(damageFrom.plyayerNumber() - 1)
                    .getPokemonBattleStat(damageFrom.targetName());
            damageOfPokemonStat.setHealthValue(damageOfPokemonStat.getHealthValue() + healthDiff);
            damageOfPokemonStat.setAttackValue(damageOfPokemonStat.getAttackValue() + healthDiff);
        }
        return damageFrom;
    }

    private void setHealthValueStatByFrom(BattleEvent battleEvent, BattleStat battleStat,
                                                 PlayerStatus targetPlayerStatus, int healthDiff) {
        String damageFrom = BattleEventUtil.getEventFrom(battleEvent.getContents().get(FROM_INDEX));
        if (damageFrom == null) {
            log.warn("can not match damage from");
            return;
        }

        if (isSideDamage(damageFrom)) {
            for (Side side : targetPlayerStatus.getSideListByName(damageFrom)) {
                EventTarget fromTarget = side.fromTarget();
                PokemonBattleStat stat =
                        battleStat.playerStatList().get(fromTarget.plyayerNumber() - 1).getPokemonBattleStat(fromTarget.targetName());
                stat.setHealthValue(stat.getHealthValue() + healthDiff);
                stat.setAttackValue(stat.getAttackValue() + healthDiff);
            }
        }
    }

    private static boolean isSideDamage(String damageFrom) {
        return damageFrom.equals(STEALTH_ROCK) || damageFrom.equals(SPIKES);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}