/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Field;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HealEventAnalyzerTest {
    private static final String RILLABOOM = "Rillaboom";
    private static final String CORVIKNIGHT = "Corviknight";
    private static final String TING_LU = "Ting-Lu";
    private static final String GLISCOR = "Gliscor";
    @Autowired
    private HealEventAnalyzer healEventAnalyzer;

    @Test
    void analyzeMoveHeal() {
        BattleEvent moveEvent = new BattleEvent("move", null, null, null, null);
        moveEvent.setBattleEventStat(new MoveEventStat(new EventTarget(1, CORVIKNIGHT, "in a groove"), "Roost"));
        BattleEvent healthEvent = new BattleEvent("heal", List.of("p1a: in a groove", "99/100"), moveEvent, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, RILLABOOM)
                .addPokemonStat(1, CORVIKNIGHT)
                .build();
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(2, RILLABOOM, RILLABOOM)
                .addPokemon(1, CORVIKNIGHT, "in a groove")
                .setTurnStartPokemon(2, RILLABOOM)
                .setHealth(1, CORVIKNIGHT, BigDecimal.valueOf(49))
                .build();

        assertTrue(healEventAnalyzer.supportAnalyze(healthEvent));
        healEventAnalyzer.analyze(healthEvent, battleStat, battleStatus);

        PokemonBattleStat corviknightStat = battleStat.playerStatList().get(0).getPokemonBattleStat(CORVIKNIGHT);
        PokemonBattleStat rillaboomStat = battleStat.playerStatList().get(1).getPokemonBattleStat(RILLABOOM);
        assertEquals(BigDecimal.valueOf(50.0), corviknightStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(0.0), corviknightStat.getAttackValue());
        assertEquals(BigDecimal.valueOf(-50.0), rillaboomStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(-50.0), rillaboomStat.getAttackValue());

        PokemonStatus corviknightStatus = battleStatus.getPlayerStatusList().get(0).getPokemonStatus(CORVIKNIGHT);
        assertEquals(BigDecimal.valueOf(99.0), corviknightStatus.getHealth());
    }

    @Test
    void analyzeItemHeal() {
        BattleEvent healthEvent = new BattleEvent("heal",
                List.of("p2a: Ting-Lu", "95/100", "[from] item: Leftovers"), null, null, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, TING_LU)
                .addPokemonStat(1, CORVIKNIGHT)
                .build();
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(2, TING_LU, TING_LU)
                .addPokemon(1, CORVIKNIGHT, "in a groove")
                .setTurnStartPokemon(1, CORVIKNIGHT)
                .setHealth(2, TING_LU, BigDecimal.valueOf(89))
                .build();
        assertTrue(healEventAnalyzer.supportAnalyze(healthEvent));
        healEventAnalyzer.analyze(healthEvent, battleStat, battleStatus);

        PokemonBattleStat corviknightStat = battleStat.playerStatList().get(0).getPokemonBattleStat(CORVIKNIGHT);
        PokemonBattleStat tiluStat = battleStat.playerStatList().get(1).getPokemonBattleStat(TING_LU);
        assertEquals(BigDecimal.valueOf(-6.0), corviknightStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(-6.0), corviknightStat.getAttackValue());
        assertEquals(BigDecimal.valueOf(6.0), tiluStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(0.0), tiluStat.getAttackValue());

        PokemonStatus tiluStatus = battleStatus.getPlayerStatusList().get(1).getPokemonStatus(TING_LU);
        assertEquals(BigDecimal.valueOf(95.0), tiluStatus.getHealth());
    }

    @Test
    void analyzeAbilityHeal() {
        BattleEvent healthEvent = new BattleEvent("heal",
                List.of("p1a: Gliscor", "97/100 tox", "[from] ability: Poison Heal"), null, null, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, TING_LU)
                .addPokemonStat(1, GLISCOR)
                .build();
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(2, TING_LU, TING_LU)
                .addPokemon(1, GLISCOR, GLISCOR)
                .setTurnStartPokemon(2, TING_LU)
                .setHealth(1, GLISCOR, BigDecimal.valueOf(84))
                .build();
        assertTrue(healEventAnalyzer.supportAnalyze(healthEvent));
        healEventAnalyzer.analyze(healthEvent, battleStat, battleStatus);

        PokemonBattleStat gliscorStat = battleStat.playerStatList().get(0).getPokemonBattleStat(GLISCOR);
        PokemonBattleStat tingluStat = battleStat.playerStatList().get(1).getPokemonBattleStat(TING_LU);
        assertEquals(BigDecimal.valueOf(13.0), gliscorStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(0.0), gliscorStat.getAttackValue());
        assertEquals(BigDecimal.valueOf(-13.0), tingluStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(-13.0), tingluStat.getAttackValue());

        PokemonStatus gliscorStatus = battleStatus.getPlayerStatusList().get(0).getPokemonStatus(GLISCOR);
        assertEquals(BigDecimal.valueOf(97.0), gliscorStatus.getHealth());
    }

    @Test
    void analyzeFieldHeal() {
        BattleEvent healthEvent = new BattleEvent("heal",
                List.of("p1a: in a groove", "100/100", "[from] Grassy Terrain"), null, null, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, RILLABOOM)
                .addPokemonStat(1, CORVIKNIGHT)
                .build();
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(2, RILLABOOM, RILLABOOM)
                .addPokemon(1, CORVIKNIGHT, "in a groove")
                .setTurnStartPokemon(2, RILLABOOM)
                .setTurnStartPokemon(1, CORVIKNIGHT)
                .setHealth(1, CORVIKNIGHT, BigDecimal.valueOf(99))
                .setFiled(new Field("Grassy Terrain", new EventTarget(2, RILLABOOM, RILLABOOM)))
                .build();
        assertTrue(healEventAnalyzer.supportAnalyze(healthEvent));
        healEventAnalyzer.analyze(healthEvent, battleStat, battleStatus);

        PokemonBattleStat corviknightStat = battleStat.playerStatList().get(0).getPokemonBattleStat(CORVIKNIGHT);
        PokemonBattleStat rillaboomStat = battleStat.playerStatList().get(1).getPokemonBattleStat(RILLABOOM);
        assertEquals(BigDecimal.valueOf(1.0), corviknightStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(0.0), corviknightStat.getAttackValue());
        assertEquals(BigDecimal.valueOf(-1.0), rillaboomStat.getHealthValue());
        assertEquals(BigDecimal.valueOf(-1.0), rillaboomStat.getAttackValue());

        PokemonStatus corviknightStatus = battleStatus.getPlayerStatusList().get(0).getPokemonStatus(CORVIKNIGHT);
        assertEquals(BigDecimal.valueOf(100.0), corviknightStatus.getHealth());
    }
}