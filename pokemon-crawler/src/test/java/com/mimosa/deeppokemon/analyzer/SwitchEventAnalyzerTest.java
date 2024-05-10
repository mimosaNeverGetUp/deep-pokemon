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
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        BattleStat battleStat = new BattleStat(null, List.of(p1), new ArrayList<>());

        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(1, "Gliscor", "YOUCANTBREAKME")
                .build();
        battleStatus.setTurn(10);

        Assertions.assertTrue(analyzer.supportAnalyze(battleEvent));
        analyzer.analyze(battleEvent, battleStat, battleStatus);
        PlayerStatus p1Status = battleStatus.getPlayerStatusList().get(0);
        Assertions.assertEquals("Gliscor", p1Status.getActivePokemonName());
        Assertions.assertEquals(1, p1.getSwitchCount());
        Assertions.assertEquals(1, p1.getPokemonBattleStat("Gliscor").getSwitchCount());
        Assertions.assertEquals(BigDecimal.valueOf(0.0), p1.getPokemonBattleStat("Gliscor").getHealthValue());
        Assertions.assertEquals(BigDecimal.valueOf(0.0), p1.getPokemonBattleStat("Gliscor").getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(100.0), p1Status.getPokemonStatus("Gliscor").getHealth());
        Assertions.assertEquals(10, p1Status.getPokemonStatus("Gliscor").getLastActivateTurn());
    }

    @Test
    void analyzeDragEvent() {
        BattleEvent battleEvent = new BattleEvent("drag", List.of("p1a: YOUCANTBREAKME", "Gliscor, M", "100/100"), null
                , null);
        PlayerStat p1 = new PlayerStat(1, "");
        p1.addPokemonBattleStat(new PokemonBattleStat("Gliscor"));
        BattleStat battleStat = new BattleStat(null, List.of(p1), new ArrayList<>());

        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(1, "Gliscor", "YOUCANTBREAKME")
                .build();

        Assertions.assertTrue(analyzer.supportAnalyze(battleEvent));
        analyzer.analyze(battleEvent, battleStat, battleStatus);
        PlayerStatus p1Status = battleStatus.getPlayerStatusList().get(0);
        Assertions.assertEquals("Gliscor", p1Status.getPokemonName("YOUCANTBREAKME"));
        Assertions.assertEquals("Gliscor", p1Status.getActivePokemonName());
        Assertions.assertEquals(0, p1.getSwitchCount());
        Assertions.assertEquals(1, p1.getPokemonBattleStat("Gliscor").getSwitchCount());
        Assertions.assertEquals(BigDecimal.valueOf(100.0), p1Status.getPokemonStatus("Gliscor").getHealth());
    }

    @Test
    void analyzeRegeneratorSwitch() {
        BattleEvent switchEvent = new BattleEvent("switch", List.of("p1a: Slowking", "Slowking-Galar, M", "100/100"),
                null, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(1, SLOWKING_GALAR, "Slowking")
                .addPokemon(2, DRAGAPULT, DRAGAPULT)
                .setHealth(1, SLOWKING_GALAR, BigDecimal.valueOf(80.0))
                .setTurnStartPokemon(1, 2, DRAGAPULT)
                .setLastActivateTurn(1, SLOWKING_GALAR, 1)
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, SLOWKING_GALAR)
                .addPokemonStat(2, DRAGAPULT)
                .build();
        analyzer.analyze(switchEvent, battleStat, battleStatus);

        PokemonBattleStat slowkingStat = battleStat.playerStatList().get(0).getPokemonBattleStat(SLOWKING_GALAR);
        PokemonBattleStat dragapultStat = battleStat.playerStatList().get(1).getPokemonBattleStat(DRAGAPULT);
        Assertions.assertEquals(BigDecimal.valueOf(20.0), slowkingStat.getHealthValue());
        Assertions.assertEquals(BigDecimal.valueOf(0.0), slowkingStat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(-20.0), dragapultStat.getHealthValue());
        Assertions.assertEquals(BigDecimal.valueOf(-20.0), dragapultStat.getAttackValue());
    }
}