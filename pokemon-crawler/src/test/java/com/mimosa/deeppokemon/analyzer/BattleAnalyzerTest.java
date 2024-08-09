/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.matcher.BattleStatMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
        ClassPathResource replayDirectory = new ClassPathResource("battlereplay/gen9ou");
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
                    battle.setTeams(extractTeam(log));
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
        List<BattleStat> battleStats = battleAnalyzer.analyze(Collections.singletonList(battle));
        Assertions.assertEquals(1, battleStats.size());
        Assertions.assertEquals(exceptBattleStat, battleStats.get(0));
    }

    @ParameterizedTest
    @MethodSource("provideBattleLog")
    void analyze_noException(Battle battle) {
        List<BattleStat> battleStats = battleAnalyzer.analyze(Collections.singletonList(battle));
        MatcherAssert.assertThat(battleStats, Matchers.everyItem(BattleStatMatcher.BATTLE_STAT_MATCHER));
        Assertions.assertEquals(1, battleStats.size());
    }

    private static Team[] extractTeam(String html) {
        Pattern pattern = Pattern.compile("\\|poke\\|p([12])\\|([^//|,]*)[\\|,]");
        Matcher matcher = pattern.matcher(html);
        ArrayList<Pokemon> pokemons1 = new ArrayList<Pokemon>(6);
        ArrayList<Pokemon> pokemons2 = new ArrayList<Pokemon>(6);
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
        if (pokemons1.size() == 0 && pokemons2.size() == 0) {
            throw new RuntimeException("A Team match failed");
        }
        Team team1 = new Team(pokemons1);
        Team team2 = new Team(pokemons2);
        Team[] teams = new Team[2];
        teams[0] = team1;
        teams[1] = team2;
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
            String pokemonMoveName = matcher.group(1).trim();
            return pokemonMoveName;
        }
        return pokemonName;
    }
}