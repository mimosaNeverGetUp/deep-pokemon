/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TurnEventAnalyzerTest {

    @Autowired
    private TurnEventAnalyzer turnEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("turn", List.of("1"), true, null);
        BattleStatus battleStatus = new BattleStatus(null);
        Assertions.assertTrue(turnEventAnalyzer.supportAnalyze(battleEvent));

        turnEventAnalyzer.analyze(battleEvent, null, battleStatus);
        Assertions.assertEquals(1, battleStatus.getTurn());
    }
}