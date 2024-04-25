/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.*;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Validated
@Component
public class MoveEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(MoveEventAnalyzer.class);
    private static final String MOVE = "move";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(MOVE);

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(0), battleStatus);
        if (eventTarget != null) {
            String move = battleEvent.getContents().get(1);
            PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.plyayerNumber() - 1);
            PokemonBattleStat pokemonBattleStat = playerStat.getPokemonBattleStat(
                    eventTarget.targetName());
            pokemonBattleStat.setMoveCount(pokemonBattleStat.getMoveCount() + 1);
            playerStat.setMoveCount(playerStat.getMoveCount() + 1);
            battleEvent.setBattleEventStat(new MoveEventStat(eventTarget, move));
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}