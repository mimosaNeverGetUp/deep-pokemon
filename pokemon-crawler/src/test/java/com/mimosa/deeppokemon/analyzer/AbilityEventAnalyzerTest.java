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
class AbilityEventAnalyzerTest {

    @Autowired
    private AbilityEventAnalyzer abilityEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent abilityEvent = new BattleEvent("ability",
                List.of("p2a: Corviknight", "Pressure"), null, null, null);
        String corviknight = "Corviknight";
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, corviknight)
                .build();
        Battle battle = new BattleBuilder()
                .addPokemon(2, corviknight)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, corviknight, corviknight)
                .setBattle(battle)
                .build();
        assertTrue(abilityEventAnalyzer.supportAnalyze(abilityEvent));
        abilityEventAnalyzer.analyze(abilityEvent, battleStat, battleContext);
        assertEquals("Pressure", battle.getBattleTeams().get(1).findPokemon(corviknight).getAbility());
        assertNull(battle.getBattleTeams().get(1).findPokemon(corviknight).getItem());
    }
}