/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.PlayerStat;
import com.mimosa.deeppokemon.analyzer.entity.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.analyzer.utils.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Order(2)
public class SwitchEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(SwitchEventAnalyzer.class);
    private static final String SWITCH = "switch";
    private static final String DRAG = "drag";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(SWITCH, DRAG);

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < 3) {
            log.warn("can not match battle event contents: {}", battleEvent);
            return;
        }

        String pokemonName = battleEvent.getContents().get(1).split(EventConstants.NAME_SPLIT)[0];
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(0));
        if (eventTarget != null) {
            int pokemonHealth = Integer.parseInt(battleEvent.getContents().get(2).split(EventConstants.HEALTH_SPLIT)[0]);
            int healthDiff = setBattleHealthStatus(battleStatus, eventTarget, pokemonName, pokemonHealth);
            setBattleStat(battleEvent, battleStat, battleStatus, eventTarget, pokemonName, healthDiff);
        }
    }

    private static void setBattleStat(BattleEvent event, BattleStat battleStat, BattleStatus battleStatus,
                                      EventTarget eventTarget, String pokemonName, int healthDiff) {
        PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.playerNumber() - 1);
        if (SWITCH.equals(event.getType())) {
            playerStat.setSwitchCount(playerStat.getSwitchCount() + 1);
        }
        PokemonBattleStat pokemonBattleStat =
                playerStat.getPokemonBattleStat(pokemonName);
        pokemonBattleStat.setSwitchCount(pokemonBattleStat.getSwitchCount() + 1);
        // pokemon maybe is Regenerator ability, set health value
        if (healthDiff != 0) {
            pokemonBattleStat.setHealthValue(pokemonBattleStat.getHealthValue() + healthDiff);

            // pokemon which stand with switch pokemon in opponent also should set health value
            int opponentPlayerNumber = 3 - eventTarget.playerNumber();
            PokemonStatus healthPokemonStatus = BattleEventUtil.getPokemonStatus(battleStatus,
                    eventTarget.playerNumber(), pokemonName);
            PlayerStatus opponentPlayerStatus = battleStatus.getPlayerStatusList().get(opponentPlayerNumber - 1);
            String opponentLastStandPokemon = opponentPlayerStatus.getTurnStartPokemonName(
                    healthPokemonStatus.getLastMoveTurn());
            PokemonBattleStat opponentPokemonBattleStat =
                    BattleEventUtil.getPokemonStat(battleStat, opponentPlayerNumber, opponentLastStandPokemon);
            opponentPokemonBattleStat.setHealthValue(opponentPokemonBattleStat.getHealthValue() - healthDiff);
            opponentPokemonBattleStat.setAttackValue(opponentPokemonBattleStat.getAttackValue() - healthDiff);
        }
    }

    private int setBattleHealthStatus(BattleStatus battleStatus, EventTarget eventTarget, String pokemonName,
                                      int pokemonHealth) {
        // set pokemon nickname
        PlayerStatus playerStatus = battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1);
        playerStatus.setPokemonNickNameMap(eventTarget.nickName(), pokemonName);
        playerStatus.setActivePokemonName(pokemonName);
        if (playerStatus.getPokemonStatus(pokemonName) == null) {
            // first switch, init pokemon status
            playerStatus.setPokemonStatus(pokemonName, new PokemonStatus(pokemonName));
        }
        // set pokemon health
        int healthBefore = playerStatus.getPokemonStatus(pokemonName).getHealth();
        playerStatus.getPokemonStatus(pokemonName).setHealth(pokemonHealth);
        return pokemonHealth - healthBefore;
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}