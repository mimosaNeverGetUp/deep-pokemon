/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.Side;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class SwapSideEventAnalyzer implements BattleEventAnalyzer {
    private static final String SWAP_SIDE_CONDITIONS = "swapsideconditions";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(SWAP_SIDE_CONDITIONS);

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        PlayerStatus p1PlayerStatus = battleStatus.getPlayerStatusList().get(0);
        PlayerStatus p2PlayerStatus = battleStatus.getPlayerStatusList().get(1);
        List<Side> p1SideList = p1PlayerStatus.getSideList();
        List<Side> p2SideList = p2PlayerStatus.getSideList();
        p2PlayerStatus.setSideList(p1SideList);
        p1PlayerStatus.setSideList(p2SideList);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}