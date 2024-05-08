/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.Ladder;
import com.mimosa.deeppokemon.entity.LadderRank;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.LadderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@ContextConfiguration(classes = {LadderCrawlerTest.TestConfig.class, MongodbTestConfig.class})
public class LadderCrawlerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean(name = "testLadderCrawler")
        public LadderCrawler ladderCrawler() {
            LadderCrawler crawler = new LadderCrawler("gen9ou", 1,
                    3, 1600, LocalDate.now().minusMonths(1), 60.0f);
            return crawler;
        }
    }

    @Autowired
    @Qualifier("testLadderCrawler")
    LadderCrawler ladderCrawler;

    @Autowired
    BattleService battleSevice;

    @Autowired
    LadderService ladderService;

    @Test
    public void crawLadderRank() throws IOException {
        Ladder ladder = ladderCrawler.crawLadderRank();
        assertNotNull(ladder);
        assertNotNull(ladder.getLadderRankList());

        for (LadderRank ladderRank : ladder.getLadderRankList()) {
            assertNotNull(ladderRank.getName());
            assertNotEquals(0, ladderRank.getRank());
            assertNotEquals(0, ladderRank.getElo());
            assertNotEquals(0, ladderRank.getGxe());
        }
    }

//    @Test
//    public void crawLadderBattle() throws IOException {
//        assertNotNull(ladderCrawler.getDateAfter());
//        List<Battle> battles = ladderCrawler.crawLadder();
//        assertNotNull(battles);
//        MatcherAssert.assertThat(battles, Matchers.everyItem(BattleMatcher.BATTLE_MATCHER));
//    }
}