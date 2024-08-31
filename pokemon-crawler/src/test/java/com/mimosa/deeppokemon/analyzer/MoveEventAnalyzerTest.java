/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
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
        BattleStat battleStat = new BattleStat(null, List.of(p1), new ArrayList<>());

        PlayerStatus p1Stauts = new PlayerStatus();
        p1Stauts.setPokemonNickName("YOUCANTBREAKME", "Gliscor");
        BattleContext battleContext = new BattleContext(List.of(p1Stauts), null);
        Assertions.assertTrue(moveEventAnalyzer.supportAnalyze(battleEvent));
        moveEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        Assertions.assertEquals(1, gliscor.getMoveCount());
        Assertions.assertEquals(1, p1.getMoveCount());
        assertInstanceOf(MoveEventStat.class, battleEvent.getBattleEventStat());
        MoveEventStat stat = (MoveEventStat) battleEvent.getBattleEventStat();
        Assertions.assertEquals(1, stat.eventTarget().playerNumber());
        Assertions.assertEquals("Gliscor", stat.eventTarget().targetName());
        Assertions.assertEquals("YOUCANTBREAKME", stat.eventTarget().nickName());
        Assertions.assertEquals("Protect", stat.moveName());
    }

    @Test
    void analyzeTrick() {
        BattleEvent trickEventA = new BattleEvent("item", List.of("p1a: Ah mon gars", "Choice Scarf", "[from] move: Trick"),
                null, null);
        BattleEvent trickEventB = new BattleEvent("item", List.of("p2a: Gholdengo", "Rocky Helmet", "[from] move: " +
                "Trick"), null, null);
        BattleEvent battleEvent = new BattleEvent("move", List.of("p2a: Gholdengo", "Trick", "p1a: Ah mon gars"),
                null, List.of(trickEventA, trickEventB));

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, "Amoonguss")
                .addPokemonStat(2, "Gholdengo")
                .build();
        Battle battle = new BattleBuilder()
                .addPokemon(1, "Amoonguss")
                .addPokemon(2, "Gholdengo")
                .build();

        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, "Gholdengo", "Gholdengo")
                .addPokemon(1, "Amoonguss", "Ah mon gars")
                .setBattle(battle)
                .build();

        Assertions.assertTrue(moveEventAnalyzer.supportAnalyze(battleEvent));
        moveEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        Assertions.assertEquals("Rocky Helmet", battle.getBattleTeams().get(0).findPokemon("Amoonguss").getItem());
        Assertions.assertEquals("Choice Scarf", battle.getBattleTeams().get(1).findPokemon("Gholdengo").getItem());
    }
}