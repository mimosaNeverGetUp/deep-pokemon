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
import com.mimosa.deeppokemon.entity.Battle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RatingEventAnalyzerTest {

    @Autowired
    private RatingEventAnalyzer ratingEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("raw", List.of(
                "LT114FP EVIL LOOR's rating: 1916 &rarr; <strong>1891</strong><br />(-25 for losing)"), null, null);

        Battle battle = new BattleBuilder()
                .setPlayerName(1, "LT114FP EVIL LOOR")
                .setRating(1, 1854)
                .setPlayerName(2, "LT114FP raincandy")
                .setRating(2, 1854)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .setBattle(battle)
                .build();

        assertTrue(ratingEventAnalyzer.supportAnalyze(battleEvent));
        ratingEventAnalyzer.analyze(battleEvent, null, battleContext);
        assertEquals(1916, battleContext.getBattle().getTeams()[0].getRating());
        assertEquals(1854, battleContext.getBattle().getTeams()[1].getRating());

        battleEvent = new BattleEvent("raw", List.of(
                "LT114FP raincandy's rating: 1829 &rarr; <strong>1854</strong><br />(-25 for losing)"), null, null);
        ratingEventAnalyzer.analyze(battleEvent, null, battleContext);
        assertEquals(1854, battleContext.getBattle().getTeams()[1].getRating());
        assertEquals(1916, battleContext.getBattle().getTeams()[0].getRating());
    }
}