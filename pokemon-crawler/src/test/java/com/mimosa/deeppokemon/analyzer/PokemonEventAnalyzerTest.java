/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
class PokemonEventAnalyzerTest {
    @Autowired
    PokemonEventAnalyzer pokemonEventAnalyzer;

    public static Stream<Arguments> provideBattleEvent() {
        return Stream.of(
                Arguments.of(new BattleEvent("poke", List.of("p1", "Roaring Moon"), null,
                        null), 1, "Roaring Moon"),
                Arguments.of(new BattleEvent("poke", List.of("p2", "Dragonite, F"), null,
                        null), 2, "Dragonite"));
    }

    @ParameterizedTest
    @MethodSource("provideBattleEvent")
    void analyze(BattleEvent battleEvent, int playerNumber, String exceptPokemonName) {
        BattleStat battleStat = new BattleStat(null,
                List.of(new PlayerStat(1, ""), new PlayerStat(2, "")),
                new ArrayList<>());
        BattleContext battleContext = new BattleContextBuilder().build();
        Assertions.assertTrue(pokemonEventAnalyzer.supportAnalyze(battleEvent));
        pokemonEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        Collection<PokemonBattleStat> pokemonBattleStats =
                battleStat.playerStatList().get(playerNumber - 1).getPokemonBattleStats();
        Assertions.assertEquals(1, pokemonBattleStats.size());
        Assertions.assertTrue(pokemonBattleStats.stream().map(PokemonBattleStat::getName).anyMatch(exceptPokemonName::equals));
        Assertions.assertNotNull(battleContext.getPlayerStatusList().get(playerNumber-1).getPokemonStatus(exceptPokemonName));
    }
}