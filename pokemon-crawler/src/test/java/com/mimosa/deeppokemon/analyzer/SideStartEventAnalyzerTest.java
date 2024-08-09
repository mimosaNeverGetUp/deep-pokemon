/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.*;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import com.mimosa.deeppokemon.entity.stat.BattleHighLight;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SideStartEventAnalyzerTest {
    @Autowired
    private SideStartEventAnalyzer sideStartEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent moveEvent = new BattleEvent("move", null, null, null);
        moveEvent.setBattleEventStat(new MoveEventStat(new EventTarget(2, "Gliscor",
                "Gliscor"), "Stealth Rock"));
        BattleEvent sideEvent = new BattleEvent("sidestart", List.of("p1: namegenerator214", "move: Stealth Rock"),
                moveEvent, null);
        BattleStat battleStat = new BattleStatBuilder().build();

        PlayerStatus p1Status = new PlayerStatus();
        BattleContext battleContext = new BattleContext(List.of(p1Status));
        battleContext.setTurn(10);
        Assertions.assertTrue(sideStartEventAnalyzer.supportAnalyze(sideEvent));
        sideStartEventAnalyzer.analyze(sideEvent, battleStat, battleContext);
        Assertions.assertEquals(1, p1Status.getSideList().size());
        Side side = p1Status.getSideListByName("Stealth Rock").stream().findFirst().orElseThrow();
        Assertions.assertEquals("Stealth Rock", side.name());
        Assertions.assertEquals("Gliscor", side.ofTarget().targetName());
        Assertions.assertEquals(2, side.ofTarget().playerNumber());

        PlayerStat p1PlayerStat = battleStat.playerStatList().get(0);
        Assertions.assertEquals(1, p1PlayerStat.getHighLights().size());
        BattleHighLight battleHighLight = p1PlayerStat.getHighLights().get(0);
        Assertions.assertEquals(BattleHighLight.HighLightType.SIDE, battleHighLight.type());
        Assertions.assertEquals(10, battleHighLight.turn());
    }

    @Test
    void analyzeScreenEvent() {
        String reflect = "Reflect";
        String serperior = "Serperior";
        BattleEvent moveEvent = new BattleEvent("move", null, null, null);
        moveEvent.setBattleEventStat(new MoveEventStat(new EventTarget(1, serperior,
                serperior), reflect));
        BattleEvent sideEvent = new BattleEvent("sidestart", List.of("p1: RUBYBLOOD", "Reflect"),
                moveEvent, null);
        BattleStat battleStat = new BattleStatBuilder().build();
        BattleContext battleContext = new BattleContextBuilder()
                .setTurn(7)
                .build();
        sideStartEventAnalyzer.analyze(sideEvent, battleStat, battleContext);

        PlayerStatus p1Status = battleContext.getPlayerStatusList().get(0);
        Assertions.assertEquals(1, p1Status.getSideList().size());
        Side side = p1Status.getSideListByName(reflect).stream().findFirst().orElseThrow();
        Assertions.assertEquals(reflect, side.name());
        Assertions.assertEquals(serperior, side.ofTarget().targetName());
        Assertions.assertEquals(1, side.ofTarget().playerNumber());

        PlayerStat p1PlayerStat = battleStat.playerStatList().get(0);
        Assertions.assertEquals(1, p1PlayerStat.getHighLights().size());
        BattleHighLight battleHighLight = p1PlayerStat.getHighLights().get(0);
        Assertions.assertEquals(BattleHighLight.HighLightType.SIDE, battleHighLight.type());
        Assertions.assertEquals(7, battleHighLight.turn());
    }
}