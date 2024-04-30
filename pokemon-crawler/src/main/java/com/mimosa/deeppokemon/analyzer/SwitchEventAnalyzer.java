/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.*;
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
            setBattleStatus(battleStatus, eventTarget, pokemonName, pokemonHealth);
            setBattleStat(battleEvent, battleStat, eventTarget, pokemonName);
        }
    }

    private static void setBattleStat(BattleEvent event, BattleStat battleStat, EventTarget eventTarget,
                                      String pokemonName) {
        PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.playerNumber() - 1);
        if (SWITCH.equals(event.getType())) {
            playerStat.setSwitchCount(playerStat.getSwitchCount() + 1);
        }
        PokemonBattleStat pokemonBattleStat =
                playerStat.getPokemonBattleStat(pokemonName);
        pokemonBattleStat.setSwitchCount(pokemonBattleStat.getSwitchCount() + 1);
    }

    private void setBattleStatus(BattleStatus battleStatus, EventTarget eventTarget, String pokemonName,
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
        playerStatus.getPokemonStatus(pokemonName).setHealth(pokemonHealth);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}