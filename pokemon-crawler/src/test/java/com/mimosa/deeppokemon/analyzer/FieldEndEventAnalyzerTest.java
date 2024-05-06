/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.Field;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FieldEndEventAnalyzerTest {
    @Autowired
    private FieldEndEventAnalyzer fieldEndEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("fieldend", List.of("move: Grassy Terrain"), null, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .setFiled(new Field("Grassy Terrain", null))
                .build();
        assertTrue(fieldEndEventAnalyzer.supportAnalyze(battleEvent));
        fieldEndEventAnalyzer.analyze(battleEvent, null, battleStatus);
        assertNull(battleStatus.getField());
    }
}