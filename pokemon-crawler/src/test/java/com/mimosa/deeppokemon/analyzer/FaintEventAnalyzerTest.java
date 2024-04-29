/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.DamageEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FaintEventAnalyzerTest {

    @Autowired
    private FaintEventAnalyzer faintEventAnalyzer;

    @Test
    void analyze() {
        String ogerpon = "Ogerpon";
        String gliscor = "Gliscor";
        int killPlyayerNumber = 2;

        BattleEvent damageEvent = new BattleEvent("damage", null, null, null);
        EventTarget damageTarget = new EventTarget(1, ogerpon, ogerpon);
        EventTarget damageFrom = new EventTarget(killPlyayerNumber, gliscor, gliscor);
        damageEvent.setBattleEventStat(new DamageEventStat(damageTarget, damageFrom, 27));
        BattleEvent faintEvent = new BattleEvent("faint", List.of("p1a: Ogerpon"), null, null, damageEvent);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(killPlyayerNumber, gliscor, gliscor)
                .addPokemon(1, ogerpon, ogerpon)
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(killPlyayerNumber, gliscor)
                .addPokemonStat(1, ogerpon)
                .build();
        Assertions.assertTrue(faintEventAnalyzer.supportAnalyze(faintEvent));
        faintEventAnalyzer.analyze(faintEvent, battleStat, battleStatus);
        PokemonBattleStat killPokemonBattleStat =
                battleStat.playerStatList().get(killPlyayerNumber - 1).getPokemonBattleStat(gliscor);
        Assertions.assertEquals(1, killPokemonBattleStat.getKillCount());
    }
}