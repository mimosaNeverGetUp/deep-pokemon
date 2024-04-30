/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.Status;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CureStatusEventAnalyzerTest {

    @Autowired
    private CureStatusEventAnalyzer analyzer;

    @Test
    void analyze() {
        String seismitoad = "Seismitoad";
        BattleEvent battleEvent = new BattleEvent("curestatus", List.of("p2: PANENKA", "brn", "[msg]"), null, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(2, seismitoad, "PANENKA")
                .setStatus(2, seismitoad, new Status("brn", null))
                .build();

        Assertions.assertTrue(analyzer.supportAnalyze(battleEvent));
        analyzer.analyze(battleEvent, null, battleStatus);
        Assertions.assertNull(battleStatus.getPlayerStatusList().get(1).getPokemonStatus(seismitoad).getStatus());
    }
}