/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.task;

import com.mimosa.deeppokemon.crawler.BattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.matcher.BattleMatcher;
import com.mimosa.deeppokemon.provider.FixedReplayProvider;
import com.mimosa.deeppokemon.provider.PlayerReplayProvider;
import com.mimosa.deeppokemon.provider.ReplayProvider;
import com.mimosa.deeppokemon.service.BattleService;
import org.apache.commons.lang.time.StopWatch;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
class CrawBattleTaskTest {
    protected static final int CRAW_PERIOD = 100;
    @Autowired
    BattleCrawler battleCrawler;

    @MockBean
    BattleService battleService;

    @Test
    void call() {
        Mockito.doReturn(new HashSet<>()).when(battleService).getAllBattleIds();
        long uploadTimeAfter = LocalDateTime.now().minusMonths(3).atZone(ZoneId.systemDefault()).toEpochSecond();
        List<Battle> battles =
                new CrawBattleTask(new PlayerReplayProvider("Separation", "gen9ou", uploadTimeAfter), battleCrawler,
                        battleService, false, 0).call();
        Assertions.assertFalse(battles.isEmpty());
        MatcherAssert.assertThat(battles, Matchers.everyItem(BattleMatcher.BATTLE_MATCHER));
    }

    @Test
    void crawReplayExcetpion() {
        CrawBattleTask crawBattleTask = new CrawBattleTask(new MockExceptionReplayProvider(10), battleCrawler,
                battleService, false, 0);
        Assertions.assertDoesNotThrow(crawBattleTask::call);
    }

    @Test
    void crawReplayWithCrawPeriod() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CrawBattleTask crawBattleTask = new CrawBattleTask(new FixedReplayProvider(List.of("test1", "test2", "test3")),
                new NoOpBattleCrawler(),
                battleService, false, CRAW_PERIOD);
        crawBattleTask.call();
        stopWatch.stop();
        Assertions.assertTrue(stopWatch.getTime() > 3 * CRAW_PERIOD);
    }


    private static class MockExceptionReplayProvider implements ReplayProvider {
        int replayNumber;

        public MockExceptionReplayProvider(int replayNumber) {
            this.replayNumber = replayNumber;
        }

        @Override
        public ReplaySource next() {
            throw new RuntimeException("mock exception");
        }

        @Override
        public boolean hasNext() {
            return replayNumber-- == 0;
        }
    }

    private static class NoOpBattleCrawler implements BattleCrawler {
        @Override
        public Battle craw(Replay replay) {
            return new Battle(null);
        }
    }
}