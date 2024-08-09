/*
 * The MIT License
 *
 * Copyright (c) [2022] [Xiaocong Huang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mimosa.deeppokemon.config;


import com.mimosa.deeppokemon.crawler.LadderCrawler;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Profile({"crawDaily", "startCraw"})
public class ScheduledConfig {
    private static final Logger log = LoggerFactory.getLogger(ScheduledConfig.class);
    private final LadderCrawler battleCrawler;
    private final BattleService battleService;
    private final StatsService statsService;

    public ScheduledConfig(LadderCrawler battleCrawler, StatsService statsService, BattleService battleService) {
        this.battleCrawler = battleCrawler;
        this.statsService = statsService;
        this.battleService = battleService;
    }

    /**
     * 定时任务爬取排行榜与回放
     *
     * @author huangxiaocong(2070132549 @ qq.com)
     */
    @Scheduled(cron = "0 0 1 * * ?")
    private void crawLadder() {
        log.info("start craw ladder");
        battleCrawler.crawLadder(false).analyzeFuture().join();
        log.info("craw ladder success");
        battleService.updateTeamGroup();
    }

    @Scheduled(cron = "0 0 4 * * ?")
    private void crawMonthlyStat() {
        statsService.craw("gen9ou");
    }
}