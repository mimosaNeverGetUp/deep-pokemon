/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Status;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.util.BattleBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Pokemon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class StatusEventAnalyzerTest {
    @Autowired
    private StatusEventAnalyzer statusEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent moveEvent = new BattleEvent("move", null, null, null);
        String slowking = "Slowking";
        String alomomola = "Alomomola";

        EventTarget eventTarget = new EventTarget(2, slowking, slowking);
        moveEvent.setBattleEventStat(new MoveEventStat(eventTarget, "Sludge Bomb"));
        BattleEvent statusEvent = new BattleEvent("status", List.of("p1a: AK (oppmouto mode)", "psn"), moveEvent, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, alomomola, "AK (oppmouto mode)")
                .addPokemon(2, slowking, slowking)
                .build();
        Assertions.assertTrue(statusEventAnalyzer.supportAnalyze(statusEvent));
        statusEventAnalyzer.analyze(statusEvent, null, battleContext);
        Status status = battleContext.getPlayerStatusList().get(0).getPokemonStatus(alomomola).getStatus();
        Assertions.assertNotNull(status);
        Assertions.assertEquals("psn", status.name());
        Assertions.assertEquals(eventTarget, status.ofTarget());
    }

    @Test
    void analyzeFlameOrb() {
        String ursaluna = "Ursaluna";
        EventTarget exceptTarget = new EventTarget(1, ursaluna, ursaluna);

        BattleEvent statusEvent = new BattleEvent("status", List.of("p1a: Ursaluna", "brn", "[from] item: Flame Orb"), null, null);
        Battle battle = new BattleBuilder()
                .addPokemon(1, ursaluna)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, ursaluna, ursaluna)
                .setBattle(battle)
                .build();

        Assertions.assertTrue(statusEventAnalyzer.supportAnalyze(statusEvent));
        statusEventAnalyzer.analyze(statusEvent, null, battleContext);
        Status status = battleContext.getPlayerStatusList().get(0).getPokemonStatus(ursaluna).getStatus();
        Assertions.assertNotNull(status);
        Assertions.assertEquals("brn", status.name());
        Assertions.assertEquals(exceptTarget
                , status.ofTarget());
        Pokemon pokemon = battle.getBattleTeams().get(0).findPokemon(ursaluna);
        Assertions.assertNotNull(pokemon);
        Assertions.assertEquals("Flame Orb", pokemon.getItem());
    }
}