/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.matcher.BattleStatMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class BattleServiceTest {
    public static final String NOT_EXIST_BATTLE_ID = "test-12345";
    private static final String NOT_SAVE_BATTLE_ID = "smogtours-gen9ou-746547";
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
    void getBattleStat() {
        BattleStat battleStat = null;
        try {
            battleStat = battleService.getBattleStat(NOT_SAVE_BATTLE_ID);
            MatcherAssert.assertThat(battleStat, BattleStatMatcher.BATTLE_STAT_MATCHER);

        } finally {
            if (battleStat != null) {
                mongoTemplate.remove(battleStat);
                mongoTemplate.remove(new Query(Criteria.where("_id").is(NOT_SAVE_BATTLE_ID)),"battle");
            }
        }
    }

    @Test
    void testGetBattleStat() {
        BattleStat battleStat = battleService.getBattleStat("smogtours-gen6ou-767611");
        MatcherAssert.assertThat(battleStat, BattleStatMatcher.BATTLE_STAT_MATCHER);
    }
}