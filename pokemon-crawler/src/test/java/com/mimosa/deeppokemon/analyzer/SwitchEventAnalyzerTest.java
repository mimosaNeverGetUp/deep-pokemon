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
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SwitchEventAnalyzerTest {
    private static final String SLOWKING_GALAR = "Slowking-Galar";
    private static final String DRAGAPULT = "Dragapult";
    @Autowired
    private SwitchEventAnalyzer analyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("switch", List.of("p1a: YOUCANTBREAKME", "Gliscor, M", "100/100"),
                null
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
        Assertions.assertEquals(0, p1.getPokemonBattleStat("Gliscor").getHealthValue());
        Assertions.assertEquals(0, p1.getPokemonBattleStat("Gliscor").getAttackValue());
        Assertions.assertEquals(100, p1Stauts.getPokemonStatus("Gliscor").getHealth());
    }

    @Test
    void analyzeDragEvent() {
        BattleEvent battleEvent = new BattleEvent("drag", List.of("p1a: YOUCANTBREAKME", "Gliscor, M", "100/100"), null
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
        Assertions.assertEquals(100, p1Stauts.getPokemonStatus("Gliscor").getHealth());
    }

    @Test
    void analyzeRegeneratorSwitch() {
        BattleEvent switchEvent = new BattleEvent("switch", List.of("p1a: Slowking", "Slowking-Galar, M", "100/100"),
                null, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(1, SLOWKING_GALAR, "Slowking")
                .addPokemon(2, DRAGAPULT, DRAGAPULT)
                .setHealth(1, SLOWKING_GALAR, 80)
                .setTurnStartPokemon(1, 2, DRAGAPULT)
                .setLastMoveTurn(1, SLOWKING_GALAR, 1)
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, SLOWKING_GALAR)
                .addPokemonStat(2, DRAGAPULT)
                .build();
        analyzer.analyze(switchEvent, battleStat, battleStatus);

        PokemonBattleStat slowkingStat = battleStat.playerStatList().get(0).getPokemonBattleStat(SLOWKING_GALAR);
        PokemonBattleStat dragapultStat = battleStat.playerStatList().get(1).getPokemonBattleStat(DRAGAPULT);
        Assertions.assertEquals(20, slowkingStat.getHealthValue());
        Assertions.assertEquals(0, slowkingStat.getAttackValue());
        Assertions.assertEquals(-20, dragapultStat.getHealthValue());
        Assertions.assertEquals(-20, dragapultStat.getAttackValue());
    }
}