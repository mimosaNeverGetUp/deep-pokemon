/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.*;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MoveEventAnalyzerTest {
    @Autowired
    private MoveEventAnalyzer moveEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("move", List.of("p1a: YOUCANTBREAKME", "Protect", "p1a: Gliscor"),
                null, null);
        PlayerStat p1 = new PlayerStat(1, "");
        PokemonBattleStat gliscor = new PokemonBattleStat("Gliscor");
        p1.addPokemonBattleStat(gliscor);
        BattleStat battleStat = new BattleStat(List.of(p1));

        PlayerStatus p1Stauts = new PlayerStatus();
        p1Stauts.setPokemonNickNameMap("YOUCANTBREAKME", "Gliscor");
        BattleStatus battleStatus = new BattleStatus(List.of(p1Stauts));
        Assertions.assertTrue(moveEventAnalyzer.supportAnalyze(battleEvent));
        moveEventAnalyzer.analyze(battleEvent, battleStat, battleStatus);
        Assertions.assertEquals(1, gliscor.getMoveCount());
        Assertions.assertEquals(1, p1.getMoveCount());
        assertInstanceOf(MoveEventStat.class, battleEvent.getBattleEventStat());
        MoveEventStat stat = (MoveEventStat) battleEvent.getBattleEventStat();
        Assertions.assertEquals(1, stat.eventTarget().playerNumber());
        Assertions.assertEquals("Gliscor", stat.eventTarget().targetName());
        Assertions.assertEquals("YOUCANTBREAKME", stat.eventTarget().nickName());
        Assertions.assertEquals("Protect", stat.moveName());
    }
}