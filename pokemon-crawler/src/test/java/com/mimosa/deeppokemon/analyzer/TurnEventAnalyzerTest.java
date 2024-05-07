/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.TurnStat;
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
class TurnEventAnalyzerTest {

    public static final String PIKACHU = "pikachu";
    public static final String IRON_VALIANT = "Iron Valiant";
    @Autowired
    private TurnEventAnalyzer turnEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("turn", List.of("1"), null, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(1, PIKACHU, PIKACHU)
                .addPokemon(2, PIKACHU, PIKACHU)
                .addPokemon(1, IRON_VALIANT, IRON_VALIANT)
                .addPokemon(2, IRON_VALIANT, IRON_VALIANT)
                .setActivePokemonName(1, PIKACHU)
                .setActivePokemonName(2, IRON_VALIANT)
                .setHealth(1, PIKACHU, 50)
                .setHealth(2, IRON_VALIANT, 25)
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .build();


        Assertions.assertTrue(turnEventAnalyzer.supportAnalyze(battleEvent));

        turnEventAnalyzer.analyze(battleEvent, battleStat, battleStatus);
        PlayerStatus p1 = battleStatus.getPlayerStatusList().get(0);
        PlayerStatus p2 = battleStatus.getPlayerStatusList().get(1);
        Assertions.assertEquals(1, battleStatus.getTurn());
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
        Assertions.assertEquals(50, turnStat.getTurnPlayerStatList().get(0).getTurnPokemonStat(PIKACHU).getHealth());
        Assertions.assertEquals(25,
                turnStat.getTurnPlayerStatList().get(1).getTurnPokemonStat(IRON_VALIANT).getHealth());
        Assertions.assertEquals(150, turnStat.getTurnPlayerStatList().get(0).getTotalHealth());
        Assertions.assertEquals(125, turnStat.getTurnPlayerStatList().get(1).getTotalHealth());
    }
}