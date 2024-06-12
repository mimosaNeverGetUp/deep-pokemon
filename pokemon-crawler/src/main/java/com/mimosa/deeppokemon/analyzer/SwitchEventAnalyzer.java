/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.analyzer.utils.EventConstants;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Component
public class SwitchEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(SwitchEventAnalyzer.class);
    private static final String SWITCH = "switch";
    private static final String DRAG = "drag";
    private static final String REPLACE = "replace";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(SWITCH, DRAG, REPLACE);
    private static final String FORM_SPLIT = "-";
    private static final int HEALTH_INDEX = 2;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < HEALTH_INDEX) {
            log.warn("can not match battle event contents: {}", battleEvent);
            return;
        }

        String pokemonName = battleEvent.getContents().get(1).split(EventConstants.NAME_SPLIT)[0];
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(0));
        if (eventTarget != null) {
            PlayerStatus playerStatus = battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1);
            playerStatus.setPokemonNickNameMap(eventTarget.nickName(), pokemonName);
            playerStatus.setActivePokemonName(pokemonName);
            changeFormChangingPokemonName(battleStatus, battleStat, eventTarget.playerNumber(), pokemonName);

            if (battleEvent.getContents().size() > HEALTH_INDEX) {
                BigDecimal pokemonHealth = BattleEventUtil.getHealthPercentage(battleEvent.getContents().get(HEALTH_INDEX));
                BigDecimal healthDiff = setBattleHealthStatus(battleStatus, eventTarget, pokemonName, pokemonHealth);
                setBattleStat(battleEvent, battleStat, battleStatus, eventTarget, pokemonName, healthDiff);
                playerStatus.getPokemonStatus(pokemonName).setLastActivateTurn(battleStatus.getTurn());
            }
        }
    }

    /**
     * if switch pokemon is changing form, check and modify blur name in battle stat and battle status
     */
    private void changeFormChangingPokemonName(BattleStatus battleStatus, BattleStat battleStat,
                                               int playerNumber, String switchPokemonName) {
        PlayerStatus switchPlayerStatus = battleStatus.getPlayerStatusList().get(playerNumber - 1);
        if (switchPlayerStatus.getPokemonStatus(switchPokemonName) == null) {
            Optional<String> blurPokemonNameOptional = switchPlayerStatus.getPokemonStatusMap().keySet().stream()
                    .filter(pokemonName -> getOriginFormPokemonName(pokemonName)
                            .contains(getOriginFormPokemonName(switchPokemonName)))
                    .findFirst();
            if (blurPokemonNameOptional.isEmpty()) {
                log.warn("can not match origin blur pokemon name by {}", switchPokemonName);
                return;
            }

            String blurPokemonName = blurPokemonNameOptional.get();
            battleStatus.changePokemonName(playerNumber, blurPokemonName, switchPokemonName);
            battleStat.changePokemonName(playerNumber, blurPokemonName, switchPokemonName);
        }
    }

    private String getOriginFormPokemonName(String pokemonName) {
        return pokemonName.split(FORM_SPLIT)[0];
    }

    private void setBattleStat(BattleEvent event, BattleStat battleStat, BattleStatus battleStatus,
                               EventTarget eventTarget, String pokemonName, BigDecimal healthDiff) {
        PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.playerNumber() - 1);
        if (SWITCH.equals(event.getType())) {
            playerStat.setSwitchCount(playerStat.getSwitchCount() + 1);
        }
        PokemonBattleStat pokemonBattleStat =
                playerStat.getPokemonBattleStat(pokemonName);
        pokemonBattleStat.setSwitchCount(pokemonBattleStat.getSwitchCount() + 1);
        // pokemon maybe is Regenerator ability, set health value
        if (healthDiff.compareTo(BigDecimal.ZERO) != 0) {
            pokemonBattleStat.setHealthValue(pokemonBattleStat.getHealthValue().add(healthDiff));

            // pokemon which stand with switch pokemon in opponent also should set health value
            int opponentPlayerNumber = 3 - eventTarget.playerNumber();
            PokemonStatus healthPokemonStatus = BattleEventUtil.getPokemonStatus(battleStatus,
                    eventTarget.playerNumber(), pokemonName);
            PlayerStatus opponentPlayerStatus = battleStatus.getPlayerStatusList().get(opponentPlayerNumber - 1);
            String opponentLastStandPokemon = opponentPlayerStatus.getTurnStartPokemonName(
                    healthPokemonStatus.getLastActivateTurn());
            PokemonBattleStat opponentPokemonBattleStat =
                    BattleEventUtil.getPokemonStat(battleStat, opponentPlayerNumber, opponentLastStandPokemon);
            opponentPokemonBattleStat.setHealthValue(opponentPokemonBattleStat.getHealthValue().subtract(healthDiff));
            opponentPokemonBattleStat.setAttackValue(opponentPokemonBattleStat.getAttackValue().subtract(healthDiff));
        }
    }

    private BigDecimal setBattleHealthStatus(BattleStatus battleStatus, EventTarget eventTarget, String pokemonName,
                                             BigDecimal pokemonHealth) {
        PlayerStatus playerStatus = battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1);
        BigDecimal healthBefore = playerStatus.getPokemonStatus(pokemonName).getHealth();
        playerStatus.getPokemonStatus(pokemonName).setHealth(pokemonHealth);
        return pokemonHealth.subtract(healthBefore);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}