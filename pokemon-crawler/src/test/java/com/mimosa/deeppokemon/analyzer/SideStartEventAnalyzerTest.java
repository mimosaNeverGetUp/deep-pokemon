/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Side;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
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
        PlayerStatus p1Status = new PlayerStatus();
        BattleStatus battleStatus = new BattleStatus(List.of(p1Status));
        Assertions.assertTrue(sideStartEventAnalyzer.supportAnalyze(sideEvent));
        sideStartEventAnalyzer.analyze(sideEvent, null, battleStatus);
        Assertions.assertEquals(1, p1Status.getSideList().size());
        Side side = p1Status.getSideListByName("Stealth Rock").stream().findFirst().orElseThrow();
        Assertions.assertEquals("Stealth Rock", side.name());
        Assertions.assertEquals("Gliscor", side.fromTarget().targetName());
        Assertions.assertEquals(2, side.fromTarget().plyayerNumber());
    }
}