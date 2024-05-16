/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.matcher.BattleStatMatcher;
import com.mimosa.deeppokemon.service.BattleService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class BattleAnalyzerTest {
    @Autowired
    BattleAnalyzer battleAnalyzer;

    @MockBean
    BattleService battleService;

    @Value("classpath:battlereplay/gen9ou/smogtours-gen9ou-746547.log")
    private Resource battleReplay;

    @Value("classpath:battlereplay/gen9ou/stat/smogtours-gen9ou-746547.stat")
    private Resource battleStat;

    @Value("classpath:battlereplay/gen9ou")
    private Resource battleReplayResource;

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

    @Test
    void analyze_noException() throws IOException {
        Path replayDirectory = battleReplayResource.getFile().toPath();
        List<Battle> battles = new ArrayList<>();
        Files.list(replayDirectory).forEach(battleReplay -> {
            try {
                if (Files.isDirectory(battleReplay)) {
                    return;
                }
                Battle battle = new Battle();
                battle.setBattleID(battleReplay.getFileName().toString().split("\\.")[0]);
                battle.setLog(Files.readString(battleReplay));
                battles.add(battle);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        List<BattleStat> battleStats = battleAnalyzer.analyze(battles);
        MatcherAssert.assertThat(battleStats, Matchers.everyItem(BattleStatMatcher.BATTLE_STAT_MATCHER));
        Assertions.assertEquals(battles.size(), battleStats.size());
    }
}