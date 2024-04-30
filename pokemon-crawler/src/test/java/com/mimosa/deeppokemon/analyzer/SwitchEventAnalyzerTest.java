/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.*;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SwitchEventAnalyzerTest {
    @Autowired
    private SwitchEventAnalyzer analyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("switch", List.of("p1a: YOUCANTBREAKME", "Gliscor, M", "71/100"), null
                , null);
        PlayerStat p1 = new PlayerStat(1, "");
        p1.addPokemonBattleStat(new PokemonBattleStat("Gliscor"));
        BattleStat battleStat = new BattleStat(List.of(p1));

        PlayerStatus p1Stauts = new PlayerStatus();
        BattleStatus battleStatus = new BattleStatus(List.of(p1Stauts));
        Assertions.assertTrue(analyzer.supportAnalyze(battleEvent));
        analyzer.analyze(battleEvent, battleStat, battleStatus);
        Assertions.assertEquals("Gliscor", p1Stauts.getPokemonName("YOUCANTBREAKME"));
        Assertions.assertEquals("Gliscor", p1Stauts.getActivePokemonName());
        Assertions.assertEquals(1, p1.getSwitchCount());
        Assertions.assertEquals(1, p1.getPokemonBattleStat("Gliscor").getSwitchCount());
        Assertions.assertEquals(71, p1Stauts.getPokemonStatus("Gliscor").getHealth());
    }

    @Test
    void analyzeDragEvent() {
        BattleEvent battleEvent = new BattleEvent("drag", List.of("p1a: YOUCANTBREAKME", "Gliscor, M", "71/100"), null
                , null);
        PlayerStat p1 = new PlayerStat(1, "");
        p1.addPokemonBattleStat(new PokemonBattleStat("Gliscor"));
        BattleStat battleStat = new BattleStat(List.of(p1));

        PlayerStatus p1Stauts = new PlayerStatus();
        BattleStatus battleStatus = new BattleStatus(List.of(p1Stauts));
        Assertions.assertTrue(analyzer.supportAnalyze(battleEvent));
        analyzer.analyze(battleEvent, battleStat, battleStatus);
        Assertions.assertEquals("Gliscor", p1Stauts.getPokemonName("YOUCANTBREAKME"));
        Assertions.assertEquals("Gliscor", p1Stauts.getActivePokemonName());
        Assertions.assertEquals(0, p1.getSwitchCount());
        Assertions.assertEquals(1, p1.getPokemonBattleStat("Gliscor").getSwitchCount());
        Assertions.assertEquals(71, p1Stauts.getPokemonStatus("Gliscor").getHealth());
    }
}