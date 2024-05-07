/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.*;
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
        EventTarget damageOf = new EventTarget(killPlyayerNumber, gliscor, gliscor);
        damageEvent.setBattleEventStat(new DamageEventStat(damageTarget, damageOf, "Knock off", 27));
        BattleEvent moveEvent = new BattleEvent("move", null, null, List.of(damageEvent));
        BattleEvent faintEvent = new BattleEvent("faint", List.of("p1a: Ogerpon"), null, null, moveEvent);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(killPlyayerNumber, gliscor, gliscor)
                .addPokemon(1, ogerpon, ogerpon)
                .setTurn(5)
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(killPlyayerNumber, gliscor)
                .addPokemonStat(1, ogerpon)
                .build();
        Assertions.assertTrue(faintEventAnalyzer.supportAnalyze(faintEvent));
        faintEventAnalyzer.analyze(faintEvent, battleStat, battleStatus);
        PlayerStat killPlayerStat = battleStat.playerStatList().get(killPlyayerNumber - 1);
        PokemonBattleStat killPokemonBattleStat =
                killPlayerStat.getPokemonBattleStat(gliscor);
        Assertions.assertEquals(1, killPokemonBattleStat.getKillCount());
        Assertions.assertEquals(1, killPlayerStat.getHighLights().size());
        BattleHighLight battleHighLight = killPlayerStat.getHighLights().get(0);
        Assertions.assertEquals(BattleHighLight.HighLightType.KILL, battleHighLight.type());
        Assertions.assertEquals(5, battleHighLight.turn());
        Assertions.assertNotNull(battleHighLight.description());
    }
}