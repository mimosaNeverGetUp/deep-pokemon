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
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
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
        BattleStatus battleStatus = new BattleStatus(List.of(p1Status));
        battleStatus.setTurn(10);
        Assertions.assertTrue(sideStartEventAnalyzer.supportAnalyze(sideEvent));
        sideStartEventAnalyzer.analyze(sideEvent, battleStat, battleStatus);
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
}