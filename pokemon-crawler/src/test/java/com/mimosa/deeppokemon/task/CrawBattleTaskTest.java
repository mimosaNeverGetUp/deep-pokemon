/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.task;

import com.mimosa.deeppokemon.crawler.BattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.matcher.BattleMatcher;
import com.mimosa.deeppokemon.provider.PlayerReplayProvider;
import com.mimosa.deeppokemon.service.BattleService;
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
    @Autowired
    BattleCrawler battleCrawler;

    @MockBean
    BattleService battleService;

    @Test
    void call() {
        Mockito.doReturn(new HashSet<>()).when(battleService).getAllBattleIds();
        long uploadTimeAfter = LocalDateTime.now().minusMonths(3).atZone(ZoneId.systemDefault()).toEpochSecond();
        List<Battle> battles =
                new CrawBattleTask(new PlayerReplayProvider("Separation", "gen9ou", uploadTimeAfter), battleCrawler, battleService).call();
        Assertions.assertFalse(battles.isEmpty());
        MatcherAssert.assertThat(battles, Matchers.everyItem(BattleMatcher.BATTLE_MATCHER));
    }
}