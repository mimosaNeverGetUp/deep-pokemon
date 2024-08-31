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
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
class CrawBattleTaskTest {
    protected static final int CRAW_PERIOD = 100;
    @Autowired
    BattleCrawler battleCrawler;

    @SpyBean
    BattleService battleService;

    @Test
    void call() {
        Mockito.doReturn(new HashSet<>()).when(battleService).getAllBattleIds();
        Mockito.doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(battleService).save(Mockito.any(),
                Mockito.anyBoolean());
        long uploadTimeAfter = LocalDateTime.now().minusMonths(3).atZone(ZoneId.systemDefault()).toEpochSecond();
        List<Battle> battles =
                new CrawBattleTask(new PlayerReplayProvider("Separation", "gen9ou", uploadTimeAfter),
                        battleCrawler, null, battleService, false, 0).call();
        Assertions.assertFalse(battles.isEmpty());
        MatcherAssert.assertThat(battles, Matchers.everyItem(BattleMatcher.BATTLE_MATCHER));
    }

    @Test
    void crawReplayException() {
        CrawBattleTask crawBattleTask = new CrawBattleTask(new MockExceptionReplayProvider(10), battleCrawler
                , null, battleService, false, 0);
        Assertions.assertDoesNotThrow(crawBattleTask::call);
        // mock has next exception
        crawBattleTask = new CrawBattleTask(new MockHasNextExceptionReplayProvider(10), battleCrawler
                , null, battleService, false, 0);
        Assertions.assertDoesNotThrow(crawBattleTask::call);
    }

    @Test
    void crawReplayWithCrawPeriod() {
        Mockito.doReturn(new HashSet<>()).when(battleService).getAllBattleIds();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CrawBattleTask crawBattleTask = new CrawBattleTask(new FixedReplayProvider(List.of("test1", "test2", "test3")),
                new NoOpBattleCrawler(), null, battleService, false, CRAW_PERIOD);
        crawBattleTask.call();
        stopWatch.stop();
        Assertions.assertTrue(stopWatch.getTime() > 3 * CRAW_PERIOD);
    }


    @Test
    void crawHugeReplay() {
        Mockito.doReturn(new HashSet<>()).when(battleService).getAllBattleIds();
        Mockito.doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(battleService).save(Mockito.any(),
                Mockito.anyBoolean());
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 110; ++i) {
            ids.add("test" + i);
        }
        CrawBattleTask crawBattleTask = new CrawBattleTask(new FixedReplayProvider(ids),
                new NoOpBattleCrawler(), null, battleService, false, 0);
        List<Battle> battles = crawBattleTask.call();
        Assertions.assertEquals(110, battles.size());
    }

    private static class MockExceptionReplayProvider implements ReplayProvider {
        protected int replayNumber;

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


    private static class MockHasNextExceptionReplayProvider extends MockExceptionReplayProvider {

        public MockHasNextExceptionReplayProvider(int replayNumber) {
            super(replayNumber);
        }

        @Override
        public boolean hasNext() {
            throw new RuntimeException("mock exception");
        }
    }


    private static class NoOpBattleCrawler implements BattleCrawler {
        @Override
        public Battle craw(Replay replay) {
            return new Battle(null);
        }
    }
}