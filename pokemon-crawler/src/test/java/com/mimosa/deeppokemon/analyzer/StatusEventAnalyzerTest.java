/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Status;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class StatusEventAnalyzerTest {
    @Autowired
    private StatusEventAnalyzer statusEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent moveEvent = new BattleEvent("move", null, null, null);
        String slowking = "Slowking";
        String alomomola = "Alomomola";

        EventTarget eventTarget = new EventTarget(2, slowking, slowking);
        moveEvent.setBattleEventStat(new MoveEventStat(eventTarget, "Sludge Bomb"));
        BattleEvent statusEvent = new BattleEvent("status", List.of("p1a: AK (oppmouto mode)", "psn"), moveEvent, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(1, alomomola,"AK (oppmouto mode)")
                .addPokemon(2, slowking, slowking)
                .build();
        Assertions.assertTrue(statusEventAnalyzer.supportAnalyze(statusEvent));
        statusEventAnalyzer.analyze(statusEvent, null, battleStatus);
        Status status = battleStatus.getPlayerStatusList().get(0).getPokemonStatus(alomomola).getStatus();
        Assertions.assertNotNull(status);
        Assertions.assertEquals("psn", status.name());
        Assertions.assertEquals(eventTarget, status.ofTarget());
    }
}