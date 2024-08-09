/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class MoveEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(MoveEventAnalyzer.class);
    private static final String MOVE = "move";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(MOVE);
    protected static final String ITEM = "item";
    protected static final String TRICK = "Trick";

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }
        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(0), battleContext);
        if (eventTarget != null) {
            String move = battleEvent.getContents().get(1);
            PlayerStat playerStat = battleStat.playerStatList().get(eventTarget.playerNumber() - 1);
            PokemonBattleStat pokemonBattleStat = playerStat.getPokemonBattleStat(
                    eventTarget.targetName());
            pokemonBattleStat.setMoveCount(pokemonBattleStat.getMoveCount() + 1);
            playerStat.setMoveCount(playerStat.getMoveCount() + 1);
            battleEvent.setBattleEventStat(new MoveEventStat(eventTarget, move));

            if (StringUtils.equals(move, TRICK)) {
                setTrickItem(battleEvent, battleContext);
            }
        }
    }

    private void setTrickItem(BattleEvent battleEvent, BattleContext battleContext) {
        List<EventTarget> trickTargetList = new ArrayList<>();
        List<String> trickItems = new ArrayList<>();
        for (BattleEvent childrenBattleEvent : battleEvent.getChildrenEvents()) {
            if (StringUtils.equals(childrenBattleEvent.getType(), ITEM)) {
                EventTarget eventTarget = BattleEventUtil.getEventTarget(childrenBattleEvent.getContents().get(0), battleContext);
                String item = childrenBattleEvent.getContents().get(1);
                trickTargetList.add(eventTarget);
                trickItems.add(item);
            }
        }

        if (trickTargetList.size() < 2) {
            log.warn("trick item target {} is invalid, turn {}", trickTargetList, battleContext.getTurn());
            return;
        }

        EventTarget eventTargetA = trickTargetList.get(0);
        EventTarget eventTargetB = trickTargetList.get(1);
        String itemA = trickItems.get(0);
        String itemB = trickItems.get(1);
        battleContext.setPokemonItem(eventTargetA.playerNumber(), eventTargetA.targetName(), itemB);
        battleContext.setPokemonItem(eventTargetB.playerNumber(), eventTargetB.targetName(), itemA);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}