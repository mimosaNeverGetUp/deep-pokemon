/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleHighLight;
import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Side;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SideEndEventAnalyzerTest {

    public static final String CELEBI = "Celebi";
    @Autowired
    private SideEndEventAnalyzer sideEndEventAnalyzer;

    @Test
    void analyze() {
        String stealthRock = "Stealth Rock";
        BattleEvent sideEndEvent = new BattleEvent("sideend", List.of("p2: SOULWIND", stealthRock, "[from] move: " +
                "Rapid Spin", "[of] p2a: Starmie"), null, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .setTurn(4)
                .addSide(2, new Side(stealthRock, new EventTarget(1, CELEBI, CELEBI)))
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .build();
        Assertions.assertTrue(sideEndEventAnalyzer.supportAnalyze(sideEndEvent));
        sideEndEventAnalyzer.analyze(sideEndEvent, battleStat, battleStatus);
        Assertions.assertEquals(0, battleStatus.getPlayerStatusList().get(1).getSideList().size());
        Assertions.assertEquals(1, battleStat.playerStatList().get(1).getHighLights().size());
        BattleHighLight highLight = battleStat.playerStatList().get(1).getHighLights().get(0);
        Assertions.assertEquals(4, highLight.turn());
        Assertions.assertEquals(BattleHighLight.HighLightType.END_SIDE, highLight.type());
        Assertions.assertNotNull(highLight.description());
    }
}