/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
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
        BattleEvent battleEvent = new BattleEvent("turn", List.of("1"), null, null);
        PlayerStatus p1 = new PlayerStatus();
        p1.setActivePokemonName("pikachu");
        BattleStatus battleStatus = new BattleStatus(List.of(p1));
        Assertions.assertTrue(turnEventAnalyzer.supportAnalyze(battleEvent));

        turnEventAnalyzer.analyze(battleEvent, null, battleStatus);
        Assertions.assertEquals(1, battleStatus.getTurn());
        Assertions.assertEquals("pikachu", p1.getTurnStartPokemonName());
    }
}