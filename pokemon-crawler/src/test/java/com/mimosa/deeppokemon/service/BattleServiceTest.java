/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.provider.PlayerReplayProvider;
import com.mimosa.deeppokemon.provider.ReplayProvider;
import com.mimosa.deeppokemon.task.entity.CrawAnalyzeBattleFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class BattleServiceTest {
    public static final String NOT_EXIST_BATTLE_ID = "test-12345";
    @Autowired
    private BattleService battleService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void savaAll() {
        Battle existBattle = battleService.find100BattleSortByDate().get(0);

        Battle notExistBattle = new Battle();
        notExistBattle.setBattleID(NOT_EXIST_BATTLE_ID);
        try {
            List<Battle> insertBattle = battleService.savaAll(List.of(existBattle, notExistBattle));
            Assertions.assertEquals(1, insertBattle.size());
            Assertions.assertEquals(NOT_EXIST_BATTLE_ID, insertBattle.get(0).getBattleID());
        } finally {
            mongoTemplate.remove(notExistBattle);
        }
    }

    @Test
    void crawBattleAndAnalyze() throws ExecutionException, InterruptedException {
        ReplayProvider replayProvider = new PlayerReplayProvider("Separation", "gen9ou",
                LocalDateTime.now().minusYears(1).atZone(ZoneId.systemDefault()).toEpochSecond());
        CrawAnalyzeBattleFuture crawAnalyzeBattleFuture = battleService.crawBattleAndAnalyze(replayProvider);
        List<BattleStat> battleStats = crawAnalyzeBattleFuture.analyzeFuture().get();
        Assertions.assertFalse(battleStats.isEmpty());
    }
}