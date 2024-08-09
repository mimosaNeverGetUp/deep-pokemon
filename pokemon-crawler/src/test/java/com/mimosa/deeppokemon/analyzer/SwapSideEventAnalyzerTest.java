/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Side;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SwapSideEventAnalyzerTest {
    @Autowired
    private SwapSideEventAnalyzer swapSideEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("swapsideconditions", Collections.emptyList(), null, null, null);
        BattleStat battleStat = new BattleStatBuilder().build();
        Side side = new Side("Stealth Rock", new EventTarget(2, "Landorus-Therian", "Landorus"));
        BattleContext battleContext = new BattleContextBuilder()
                .addSide(1, side)
                .build();
        assertTrue(swapSideEventAnalyzer.supportAnalyze(battleEvent));
        swapSideEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        List<Side> p1SideList = battleContext.getPlayerStatusList().get(0).getSideList();
        List<Side> p2SideList = battleContext.getPlayerStatusList().get(1).getSideList();
        assertEquals(0, p1SideList.size());
        assertEquals(1, p2SideList.size());
        assertEquals(side, p2SideList.get(0));
    }
}