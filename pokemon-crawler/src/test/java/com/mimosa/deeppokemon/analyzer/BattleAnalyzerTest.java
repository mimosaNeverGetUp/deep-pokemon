/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.entity.Battle;
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
import java.util.List;
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
                    battle.setLog(Files.readString(battleReplay));
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
}