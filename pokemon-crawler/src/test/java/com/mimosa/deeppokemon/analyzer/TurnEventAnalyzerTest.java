/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.TurnStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
class TurnEventAnalyzerTest {

    public static final String PIKACHU = "pikachu";
    public static final String IRON_VALIANT = "Iron Valiant";
    @Autowired
    private TurnEventAnalyzer turnEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("turn", List.of("1"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, PIKACHU, PIKACHU)
                .addPokemon(2, PIKACHU, PIKACHU)
                .addPokemon(1, IRON_VALIANT, IRON_VALIANT)
                .addPokemon(2, IRON_VALIANT, IRON_VALIANT)
                .setActivePokemonName(1, PIKACHU)
                .setActivePokemonName(2, IRON_VALIANT)
                .setHealth(1, PIKACHU, BigDecimal.valueOf(50.0))
                .setHealth(2, IRON_VALIANT, BigDecimal.valueOf(25.0))
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .build();


        Assertions.assertTrue(turnEventAnalyzer.supportAnalyze(battleEvent));

        turnEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PlayerStatus p1 = battleContext.getPlayerStatusList().get(0);
        PlayerStatus p2 = battleContext.getPlayerStatusList().get(1);
        Assertions.assertEquals(1, battleContext.getTurn());
        Assertions.assertEquals(PIKACHU, p1.getTurnStartPokemonName());
        Assertions.assertEquals(IRON_VALIANT, p2.getTurnStartPokemonName());
        Assertions.assertEquals(PIKACHU, p1.getTurnStartPokemonName(1));
        Assertions.assertEquals(IRON_VALIANT, p2.getTurnStartPokemonName(1));
        Assertions.assertEquals(1, battleStat.turnStats().size());
        TurnStat turnStat = battleStat.turnStats().get(0);
        Assertions.assertEquals(0, turnStat.getTurn());
        Assertions.assertEquals(2, turnStat.getTurnPlayerStatList().size());
        Assertions.assertEquals(2, turnStat.getTurnPlayerStatList().get(0).getTurnPokemonStatMap().size());
        Assertions.assertEquals(2, turnStat.getTurnPlayerStatList().get(1).getTurnPokemonStatMap().size());
        Assertions.assertEquals(BigDecimal.valueOf(50.0),
            turnStat.getTurnPlayerStatList().get(0).getTurnPokemonStat(PIKACHU).getHealth());
        Assertions.assertEquals(BigDecimal.valueOf(25.0),
                turnStat.getTurnPlayerStatList().get(1).getTurnPokemonStat(IRON_VALIANT).getHealth());
        Assertions.assertEquals(BigDecimal.valueOf(150.0), turnStat.getTurnPlayerStatList().get(0).getTotalHealth());
        Assertions.assertEquals(BigDecimal.valueOf(125.0), turnStat.getTurnPlayerStatList().get(1).getTotalHealth());
    }
}