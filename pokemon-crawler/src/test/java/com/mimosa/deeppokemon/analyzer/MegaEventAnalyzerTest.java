/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.util.BattleBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MegaEventAnalyzerTest {
    protected static final String SCIZOR = "Scizor";

    @Autowired
    MegaEventAnalyzer megaEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("mega", List.of("p1a: Scizor", SCIZOR, "Scizorite"), null, null);
        Battle battle = new BattleBuilder()
                .addPokemon(1, SCIZOR)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, SCIZOR, SCIZOR)
                .setBattle(battle)
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, SCIZOR)
                .build();
        assertTrue(megaEventAnalyzer.supportAnalyze(battleEvent));
        megaEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        assertEquals(SCIZOR, battleContext.getBattle().getBattleTeams().get(0).findPokemon(SCIZOR).getName());
        assertEquals("Scizorite", battleContext.getBattle().getBattleTeams().get(0).findPokemon(SCIZOR).getItem());
    }
}