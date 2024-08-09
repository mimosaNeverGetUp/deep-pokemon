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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ItemEventAnalyzerTest {

    @Autowired
    private ItemEventAnalyzer itemEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent itemEvent = new BattleEvent("item",
                List.of("p1a: Kingambit", "Air Balloon"), null, null, null);
        String kingambit = "Kingambit";
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, kingambit)
                .build();
        Battle battle = new BattleBuilder()
                .addPokemon(1, kingambit)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, kingambit, kingambit)
                .setBattle(battle)
                .build();
        assertTrue(itemEventAnalyzer.supportAnalyze(itemEvent));
        itemEventAnalyzer.analyze(itemEvent, battleStat, battleContext);
        assertEquals("Air Balloon", battle.getTeams()[0].getPokemon(kingambit).getItem());
    }
}