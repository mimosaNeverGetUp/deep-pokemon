/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.PlayerStat;
import com.mimosa.deeppokemon.analyzer.entity.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
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
            PokemonStatus pokemonStatus = playerStatus.getPokemonStatus(eventTarget.pokemonName());
            int healthDiff = pokemonStatus.getHealth() - pokemonHealth;
            pokemonStatus.setHealth(pokemonHealth);

            // set health value、attack value
            setHealthValueStat(battleEvent, battleStat, battleStatus, eventTarget, playerStatus, healthDiff);

            setPlayerSwitchDamageStat(battleEvent, battleStat, eventTarget, healthDiff);

        }
    }

    private static void setPlayerSwitchDamageStat(BattleEvent battleEvent, BattleStat battleStat, EventTarget eventTarget, int healthDiff) {
        if (battleEvent.getParentEvent() != null && EventConstants.SWITCH_TYPE.equals(battleEvent.getParentEvent().getType())) {
            PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.plyayerNumber() - 1);
            playerStat.setSwitchDamage(playerStat.getSwitchDamage() + healthDiff);
        }
    }

    private static void setHealthValueStat(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus,
                                           EventTarget eventTarget, PlayerStatus targetPlayerStatus, int healthDiff) {
        PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.plyayerNumber() - 1);
        PokemonBattleStat pokemonBattleStat = playerStat.getPokemonBattleStat(targetPlayerStatus.getTurnStartPokemonName());
        pokemonBattleStat.setHealthValue(pokemonBattleStat.getHealthValue() - healthDiff);

        PokemonBattleStat damageOfPokemonStat = null;
        if (OF_INDEX < battleEvent.getContents().size()) {
            // get stat by damage from xxx of xxx
            EventTarget damageOfTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(OF_INDEX),
                    battleStatus);
            if (damageOfTarget != null) {
                damageOfPokemonStat = battleStat.playerStatList().get(damageOfTarget.plyayerNumber() - 1)
                        .getPokemonBattleStat(damageOfTarget.pokemonName());
            }
        } else if (FROM_INDEX == battleEvent.getContents().size() - 1) {
            // get stat by damage from xxx
            String damageFrom = BattleEventUtil.getEventFrom(battleEvent.getContents().get(FROM_INDEX));
            int opponentPlayerNumber = 3 - eventTarget.plyayerNumber();
            battleStatus.getPlayerStatusList().get(opponentPlayerNumber - 1).getSideSetter(damageFrom);
        } else if (battleEvent.getParentEvent() != null && battleEvent.getParentEvent().getBattleEventStat()
                instanceof MoveEventStat moveEventStat) {
            // get stat by parent move event
            EventTarget damageOfTarget = moveEventStat.eventTarget();
            damageOfPokemonStat = battleStat.playerStatList().get(damageOfTarget.plyayerNumber() - 1)
                    .getPokemonBattleStat(damageOfTarget.pokemonName());
        } else {
            // may be is opponent player turn start pokemon
            int opponentPlayerNumber = 3 - eventTarget.plyayerNumber();
            PlayerStat oppentPlayerStat = battleStat.playerStatList().get(opponentPlayerNumber - 1);
            PlayerStatus oppentPlayerStatus = battleStatus.getPlayerStatusList().get(opponentPlayerNumber - 1);
            damageOfPokemonStat = oppentPlayerStat.getPokemonBattleStat(oppentPlayerStatus.getTurnStartPokemonName());
        }
        if (damageOfPokemonStat != null) {
            damageOfPokemonStat.setHealthValue(damageOfPokemonStat.getHealthValue() + healthDiff);
            damageOfPokemonStat.setAttackValue(damageOfPokemonStat.getAttackValue() + healthDiff);
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}