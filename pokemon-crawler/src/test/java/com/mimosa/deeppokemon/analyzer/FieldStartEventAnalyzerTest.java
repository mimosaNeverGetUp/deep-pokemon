/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FieldStartEventAnalyzerTest {
    @Autowired
    private FieldStartEventAnalyzer fieldStartEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("fieldstart", List.of("move: Grassy Terrain", "[from] ability: " +
                "Grassy Surge", "[of] p2a: Rillaboom"), null, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(2, "Rillaboom", "Rillaboom")
                .build();
        assertTrue(fieldStartEventAnalyzer.supportAnalyze(battleEvent));
        fieldStartEventAnalyzer.analyze(battleEvent, null, battleStatus);
        assertNotNull(battleStatus.getField());
        assertNotNull(battleStatus.getField().eventTarget());
        assertEquals("Grassy Terrain", battleStatus.getField().name());
        assertEquals(2, battleStatus.getField().eventTarget().playerNumber());
        assertEquals("Rillaboom", battleStatus.getField().eventTarget().targetName());
    }
}