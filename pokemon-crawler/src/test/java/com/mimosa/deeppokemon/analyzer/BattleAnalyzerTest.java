/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.Battle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class BattleAnalyzerTest {
    @Autowired
    BattleAnalyzer battleAnalyzer;

    @Value("classpath:battlereplay/smogtours-gen9ou-746547")
    private Resource battereReplayResource;

    @Value("classpath:battlereplay/stat/smogtours-gen9ou-746547.stat")
    private Resource batterStat;


    @Test
    void analyze() throws IOException {
        Battle battle = new Battle();
        BattleStat exceptBattleStat =
                new ObjectMapper().readValue(batterStat.getContentAsString(StandardCharsets.UTF_8), BattleStat.class);
        battle.setBattleID(exceptBattleStat.battleId());
        battle.setLog(battereReplayResource.getContentAsString(StandardCharsets.UTF_8));
        List<BattleStat> battleStats = battleAnalyzer.analyze(Collections.singletonList(battle));
        Assertions.assertEquals(1, battleStats.size());
        Assertions.assertEquals(exceptBattleStat,battleStats.get(0));
    }
}