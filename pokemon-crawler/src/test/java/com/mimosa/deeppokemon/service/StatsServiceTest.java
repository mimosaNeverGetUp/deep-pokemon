/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.crawler.stat.MonthlyStatCrawler;
import com.mimosa.deeppokemon.crawler.stat.dto.MonthlyBattleStatDto;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyMetaStat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class StatsServiceTest {
    protected static final String GEN_9_OU = "gen9ou";
    @Autowired
    private StatsService statsService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @SpyBean
    private MonthlyStatCrawler monthlyStatCrawler;


    @Test
    void crawStat() {
        MonthlyMetaStat latestMetaStat = statsService.getLatestMetaStat(GEN_9_OU);
        MonthlyBattleStatDto mockStat = new MonthlyBattleStatDto((int) latestMetaStat.total(), null, null);
        Mockito.doReturn(mockStat).when(monthlyStatCrawler).craw(GEN_9_OU);
        String statId = "202407gen9ou";
        boolean result = statsService.crawStat(GEN_9_OU, statId);
        assertFalse(result);

        assertNull(mongoTemplate.findById(statId, MonthlyMetaStat.class));
    }

}