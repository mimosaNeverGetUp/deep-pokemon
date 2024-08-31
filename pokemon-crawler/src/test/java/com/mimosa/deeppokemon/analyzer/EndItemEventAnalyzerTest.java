/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
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
class EndItemEventAnalyzerTest {
    @Autowired
    private EndItemEventAnalyzer endItemEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent itemEvent = new BattleEvent("enditem",
                List.of("p2a: Roaring Moon", "Booster Energy"), null, null, null);
        String roaringMoon = "Roaring Moon";
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, roaringMoon)
                .build();
        Battle battle = new BattleBuilder()
                .addPokemon(2, roaringMoon)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, roaringMoon, roaringMoon)
                .setBattle(battle)
                .build();
        assertTrue(endItemEventAnalyzer.supportAnalyze(itemEvent));
        endItemEventAnalyzer.analyze(itemEvent, battleStat, battleContext);
        assertEquals("Booster Energy", battle.getBattleTeams().get(1).findPokemon(roaringMoon).getItem());
    }
}