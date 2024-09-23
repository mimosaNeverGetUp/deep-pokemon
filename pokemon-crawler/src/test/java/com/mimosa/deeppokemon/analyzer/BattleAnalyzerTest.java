/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.matcher.BattleStatMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SpringBootTest
class BattleAnalyzerTest {
    @Autowired
    BattleAnalyzer battleAnalyzer;

    @Value("classpath:battlereplay/gen9ou/smogtours-gen9ou-746547.log")
    private Resource battleReplay;

    @Value("classpath:battlereplay/gen9ou/stat/smogtours-gen9ou-746547.stat")
    private Resource battleStat;

    public static Stream<Arguments> provideBattleLog() throws IOException {
        return provideBattleLog("battlereplay/gen9ou");
    }


    public static Stream<Arguments> provideGen8ouBattleLog() throws IOException {
        return provideBattleLog("battlereplay/gen8ou");
    }

    public static Stream<Arguments> provideBattleLog(String path) throws IOException {
        ClassPathResource replayDirectory = new ClassPathResource(path);
        List<Arguments> arguments = new ArrayList<>();
        try (Stream<Path> battleLogPaths = Files.list(replayDirectory.getFile().toPath())) {
            battleLogPaths.forEach(battleReplay -> {
                try {
                    if (Files.isDirectory(battleReplay)) {
                        return;
                    }
                    Battle battle = new Battle();
                    battle.setBattleID(battleReplay.getFileName().toString().split("\\.")[0]);
                    String log = Files.readString(battleReplay);
                    battle.setLog(log);
                    battle.setBattleTeams(extractTeam(log));
                    arguments.add(Arguments.of(battle));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return arguments.stream();
    }


    @Test
    void analyze_matchExceptStat() throws IOException {
        Battle battle = new Battle();
        BattleStat exceptBattleStat =
                new ObjectMapper().readValue(battleStat.getContentAsString(StandardCharsets.UTF_8), BattleStat.class);
        battle.setBattleID(exceptBattleStat.battleId());
        battle.setLog(battleReplay.getContentAsString(StandardCharsets.UTF_8));
        battleAnalyzer.analyze(Collections.singletonList(battle));
        Assertions.assertNotNull(battle.getBattleStat());
        Assertions.assertEquals(exceptBattleStat, battle.getBattleStat());
    }

    @ParameterizedTest
    @MethodSource("provideBattleLog")
    void analyze_noException(Battle battle) {
        battleAnalyzer.analyze(Collections.singletonList(battle));
        MatcherAssert.assertThat(battle.getBattleStat(), BattleStatMatcher.BATTLE_STAT_MATCHER);
        Assertions.assertNotNull(battle.getBattleStat());
    }


    @ParameterizedTest
    @MethodSource("provideGen8ouBattleLog")
    void analyze_gen8ou_noException(Battle battle) {
        battleAnalyzer.analyze(Collections.singletonList(battle));
        MatcherAssert.assertThat(battle.getBattleStat(), BattleStatMatcher.BATTLE_STAT_MATCHER);
        Assertions.assertNotNull(battle.getBattleStat());
    }

    private static List<BattleTeam> extractTeam(String html) {
        Pattern pattern = Pattern.compile("\\|poke\\|p([12])\\|([^//|,]*)[\\|,]");
        Matcher matcher = pattern.matcher(html);
        ArrayList<Pokemon> pokemons1 = new ArrayList<>(6);
        ArrayList<Pokemon> pokemons2 = new ArrayList<>(6);
        while (matcher.find()) {
            if (matcher.group(1).equals("1")) {
                String pokemonName = matcher.group(2).trim();
                Pokemon pokemon = extractPokemon(html, pokemonName, 1);
                pokemons1.add(pokemon);
            } else {
                String pokemonName = matcher.group(2).trim();
                Pokemon pokemon = extractPokemon(html, pokemonName, 2);
                pokemons2.add(pokemon);
            }
        }
        if (pokemons1.isEmpty() && pokemons2.isEmpty()) {
            throw new RuntimeException("A Team match failed");
        }
        List<BattleTeam> teams = new ArrayList<>(2);
        BattleTeam team1 = new BattleTeam();
        team1.setPokemons(pokemons1);
        BattleTeam team2 = new BattleTeam();
        team2.setPokemons(pokemons2);
        teams.add(team1);
        teams.add(team2);
        return teams;
    }


    private static Pokemon extractPokemon(String html, String pokemonName, int playerNumber) {
        Pokemon pokemon = new Pokemon(pokemonName);
        String pokemonMoveName = extractMoveName(html, pokemonName, playerNumber);
        if ("Ditto".equals(pokemonName)) {
            pokemon.setMoves(new HashSet<>(Collections.singletonList("Transform")));
        } else {
            String regex = String.format("move\\|p%da: %s\\|([^\\|]*)\\|", playerNumber, Pattern.quote(pokemonMoveName));
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(html);
            HashSet<String> moves = new HashSet<>(4);
            while (matcher.find()) {
                moves.add(matcher.group(1));
            }
            pokemon.setMoves(moves);
        }
        return pokemon;
    }


    private static String extractMoveName(String html, String pokemonName, int playerNumber) {
        String regex = String.format("switch\\|p%da: ([^\\|]*)\\|%s", playerNumber, pokemonName);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return pokemonName;
    }
}