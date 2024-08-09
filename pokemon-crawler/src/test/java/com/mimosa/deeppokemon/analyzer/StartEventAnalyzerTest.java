/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StartEventAnalyzerTest {

    private static final String GARGANACL = "Garganacl";
    private static final String SALT_CURE = "Salt Cure";
    private static final String IRON_VALIANT = "Iron Valiant";
    private static final String PECHARUNT = "Pecharunt";
    private static final String DRAGONITE = "Dragonite";
    private static final String CONFUSION = "confusion";
    @Autowired
    private StartEventAnalyzer startEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent moveEvent = new BattleEvent("move", null, null, null);
        moveEvent.setBattleEventStat(new MoveEventStat(new EventTarget(1, GARGANACL, GARGANACL), SALT_CURE));
        BattleEvent startEvent = new BattleEvent("start", List.of("p2a: Iron Valiant", "Salt Cure"), moveEvent, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, IRON_VALIANT, IRON_VALIANT)
                .addPokemon(1, GARGANACL, GARGANACL)
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, IRON_VALIANT)
                .addPokemonStat(1, GARGANACL)
                .build();
        assertTrue(startEventAnalyzer.supportAnalyze(startEvent));
        startEventAnalyzer.analyze(startEvent, battleStat, battleContext);
        EventTarget buffOf = battleContext.getPlayerStatusList().get(1).getPokemonStatus(IRON_VALIANT).getBuffOf(SALT_CURE);
        assertNotNull(buffOf);
        assertEquals(1, buffOf.playerNumber());
        assertEquals(GARGANACL, buffOf.targetName());
    }

    @Test
    void analyzeConfusion() {
        BattleEvent startEvent = new BattleEvent("start", List.of("p1a: Dragonite", CONFUSION, "[from] ability: " +
                "Poison Puppeteer", "[of] p2a: Pecharunt"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, PECHARUNT, PECHARUNT)
                .addPokemon(1, DRAGONITE, DRAGONITE)
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, PECHARUNT)
                .addPokemonStat(1, DRAGONITE)
                .build();
        startEventAnalyzer.analyze(startEvent, battleStat, battleContext);
        EventTarget buffOf = battleContext.getPlayerStatusList().get(0).getPokemonStatus(DRAGONITE).getBuffOf(CONFUSION);
        assertNotNull(buffOf);
        assertEquals(2, buffOf.playerNumber());
        assertEquals(PECHARUNT, buffOf.targetName());
    }
}