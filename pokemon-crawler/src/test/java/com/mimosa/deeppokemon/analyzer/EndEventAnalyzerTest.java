/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.EndEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EndEventAnalyzerTest {
    @Autowired
    private EndEventAnalyzer endEventAnalyzer;

    @Test
    void analyzeFutureSight() {
        String hatterene = "Hatterene";
        String ironValiant = "Iron Valiant";
        String futureSight = "Future Sight";

        BattleEvent endEvent = new BattleEvent("end", List.of("p2a: Iron Valiant", "move: Future Sight"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, hatterene, "Nothin' Under")
                .addPokemon(2, ironValiant, ironValiant)
                .setStartMoveTarget(1, futureSight, new EventTarget(1, hatterene, "Nothin' Under"))
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, hatterene)
                .addPokemonStat(2, ironValiant)
                .build();
        assertTrue(endEventAnalyzer.supportAnalyze(endEvent));
        endEventAnalyzer.analyze(endEvent, battleStat, battleContext);
        assertInstanceOf(EndEventStat.class, endEvent.getBattleEventStat());
        EndEventStat endEventStat = (EndEventStat)endEvent.getBattleEventStat();
        assertEquals(1, endEventStat.eventTarget().playerNumber());
        assertEquals(hatterene, endEventStat.eventTarget().targetName());
        assertEquals(futureSight, endEventStat.endEvent());
    }
}