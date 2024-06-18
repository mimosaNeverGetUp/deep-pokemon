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
import com.mimosa.deeppokemon.service.LadderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

@Configuration
@EnableScheduling
@Profile({"crawDaily", "startCraw"})
public class ScheduledConfig {
    LadderCrawler battleCrawler;
    BattleService battleSevice;
    LadderService ladderService;

    private static Logger log = LoggerFactory.getLogger(ScheduledConfig.class);

    public ScheduledConfig(LadderCrawler battleCrawler, BattleService battleSevice, LadderService ladderService) {
        this.battleCrawler = battleCrawler;
        this.battleSevice = battleSevice;
        this.ladderService = ladderService;
    }

    /**
     * 定时任务爬取排行榜与回放
     *
     * @author huangxiaocong(2070132549 @ qq.com)
     */
    @Scheduled(cron = "0 0 * * * ?")
    private void crawLadder() throws IOException {
        log.info("start craw ladder");
        try {
            battleCrawler.crawLadder(false);
        } catch (Exception e) {
            throw new RuntimeException("craw ladder error", e);
        }
        log.info("craw ladder success");
    }
}