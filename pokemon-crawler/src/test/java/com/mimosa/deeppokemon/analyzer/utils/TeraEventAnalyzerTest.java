/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.utils;

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
class TeraEventAnalyzerTest {
    @Autowired
    private TeraEventAnalyzer teraEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent itemEvent = new BattleEvent("terastallize",
                List.of("p1a: Dragonite", "Steel"), null, null, null);
        String dragonite = "Dragonite";
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, dragonite)
                .build();
        Battle battle = new BattleBuilder()
                .addPokemon(1, dragonite)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, dragonite, dragonite)
                .setBattle(battle)
                .build();
        assertTrue(teraEventAnalyzer.supportAnalyze(itemEvent));
        teraEventAnalyzer.analyze(itemEvent, battleStat, battleContext);
        assertEquals("Steel", battle.getTeams()[0].getPokemon(dragonite).getTeraType());
    }
}