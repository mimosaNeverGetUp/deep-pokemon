/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DetailsChangeEventAnalyzerTest {

    private static final String OGERPON = "Ogerpon";
    private static final String OGERPON_WELLSPRING = "Ogerpon-Wellspring";
    private static final String OGERPON_WELLSPRING_TERA = "Ogerpon-Wellspring-Tera";
    @Autowired
    DetailsChangeEventAnalyzer detailsChangeEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("detailschange", List.of("p1a: Ogerpon", "Ogerpon-Wellspring-Tera, " +
                "F, tera:Water"), null, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(1, OGERPON_WELLSPRING, OGERPON)
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, OGERPON_WELLSPRING)
                .build();
        assertTrue(detailsChangeEventAnalyzer.supportAnalyze(battleEvent));
        detailsChangeEventAnalyzer.analyze(battleEvent, battleStat, battleStatus);
        assertNotNull(battleStat.playerStatList().get(0).getPokemonBattleStat(OGERPON_WELLSPRING));
        assertNull(battleStat.playerStatList().get(0).getPokemonBattleStat(OGERPON_WELLSPRING_TERA));
        assertNotNull(battleStatus.getPlayerStatusList().get(0).getPokemonStatus(OGERPON_WELLSPRING));
        assertNull(battleStatus.getPlayerStatusList().get(0).getPokemonStatus(OGERPON_WELLSPRING_TERA));
        assertEquals(OGERPON_WELLSPRING_TERA,battleStatus.getPlayerStatusList().get(0).getDetailChangeName(OGERPON));
    }
}