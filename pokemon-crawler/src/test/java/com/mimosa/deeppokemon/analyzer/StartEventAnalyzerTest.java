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
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
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
    @Autowired
    private StartEventAnalyzer startEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent moveEvent = new BattleEvent("move", null, null, null);
        moveEvent.setBattleEventStat(new MoveEventStat(new EventTarget(1, GARGANACL, GARGANACL), SALT_CURE));
        BattleEvent startEvent = new BattleEvent("start", List.of("p2a: Iron Valiant", "Salt Cure"), moveEvent, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(2, IRON_VALIANT, IRON_VALIANT)
                .addPokemon(1, GARGANACL, GARGANACL)
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, IRON_VALIANT)
                .addPokemonStat(1, GARGANACL)
                .build();
        assertTrue(startEventAnalyzer.supportAnalyze(startEvent));
        startEventAnalyzer.analyze(startEvent, battleStat, battleStatus);
        EventTarget buffOf = battleStatus.getPlayerStatusList().get(1).getPokemonStatus(IRON_VALIANT).getBuffOf(SALT_CURE);
        assertNotNull(buffOf);
        assertEquals(1, buffOf.playerNumber());
        assertEquals(GARGANACL, buffOf.targetName());
    }
}