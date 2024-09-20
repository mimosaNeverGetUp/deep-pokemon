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
        assertEquals("Air Balloon", battle.getBattleTeams().get(0).findPokemon(kingambit).getItem());
    }

    @Test
    void analyzeMagician() {
        BattleEvent itemEvent = new BattleEvent("item",
                List.of("p1a: Hoopa", "Terrain Extender", "[from] ability: Magician", "[of] p2a: SpongeShock"), null, null, null);
        String hoopa = "Hoopa-Unbound";
        String pincurchin = "Pincurchin";
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, hoopa)
                .addPokemonStat(2, pincurchin)
                .build();
        Battle battle = new BattleBuilder()
                .addPokemon(1, hoopa)
                .addPokemon(2, pincurchin)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, hoopa, "Hoopa")
                .addPokemon(2, pincurchin, "SpongeShock")
                .setBattle(battle)
                .build();
        assertTrue(itemEventAnalyzer.supportAnalyze(itemEvent));
        itemEventAnalyzer.analyze(itemEvent, battleStat, battleContext);
        assertNull(battle.getBattleTeams().get(0).findPokemon(hoopa).getItem());
        assertEquals("Terrain Extender", battle.getBattleTeams().get(1).findPokemon(pincurchin).getItem());
    }
}