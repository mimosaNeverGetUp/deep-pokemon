/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Ladder;
import com.mimosa.deeppokemon.entity.LadderRank;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.LadderService;
import com.mimosa.deeppokemon.task.entity.CrawBattleFuture;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@SpringBootTest
@ContextConfiguration(classes = {LadderCrawlerTest.TestConfig.class})
class LadderCrawlerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean(name = "testLadderCrawler")
        public LadderCrawler ladderCrawler() {
            return new LadderCrawler("gen9ou", 1,
                    3, 1600, LocalDate.now().minusMonths(1), 60.0f);
        }
    }

    @Autowired
    @Qualifier("testLadderCrawler")
    LadderCrawler ladderCrawler;

    @SpyBean
    BattleService battleSevice;

    @MockBean
    LadderService ladderService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Value("classpath:api/ladder/ladder.html")
    Resource ladderResource;

    @Test
    void crawLadderRank() throws IOException {
        LocalDate today = LocalDate.now();
        String exceptId = DateTimeFormatter.BASIC_ISO_DATE.format(today);

        Ladder ladder;
        try (var mockHttpUtil = Mockito.mockStatic(HttpUtil.class)) {
            mockHttpUtil.when(() -> HttpUtil.request(Mockito.any()))
                    .thenReturn(ladderResource.getContentAsString(StandardCharsets.UTF_8));
            ladder = ladderCrawler.crawLadderRank(false);
        }
        Assertions.assertNotNull(ladder);
        Assertions.assertEquals(exceptId, ladder.getId());
        Assertions.assertEquals(today, ladder.getDate());
        Assertions.assertEquals("gen9ou", ladder.getFormat());
        Assertions.assertNotNull(ladder.getLadderRankList());
        Assertions.assertFalse(ladder.getLadderRankList().isEmpty());

        for (LadderRank ladderRank : ladder.getLadderRankList()) {
            Assertions.assertNotNull(ladderRank.getName());
            Assertions.assertNotEquals(0, ladderRank.getRank());
            Assertions.assertNotEquals(0, ladderRank.getElo());
            Assertions.assertNotEquals(0, ladderRank.getGxe());
        }
    }

    @Test
    void crawLadder() throws ExecutionException, InterruptedException {
        Ladder ladder = new Ladder();
        ladder.setLadderRankList(Collections.nCopies(5, new LadderRank("testUser", 0, 0, 0.0F)));

        List<Battle> battleList = Collections.nCopies(10, new Battle(null));
        Mockito.doReturn(new CrawBattleFuture(CompletableFuture.completedFuture(battleList)))
                .when(battleSevice).crawBattle(Mockito.any());
        List<Battle> result = ladderCrawler.crawLadderBattle(ladder).crawFuture().get();
        Assertions.assertEquals(50, result.size());
    }
}