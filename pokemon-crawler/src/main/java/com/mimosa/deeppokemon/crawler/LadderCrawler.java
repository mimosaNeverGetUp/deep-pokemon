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

package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Ladder;
import com.mimosa.deeppokemon.entity.LadderRank;
import com.mimosa.deeppokemon.provider.PlayerReplayProvider;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.LadderService;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


public class LadderCrawler {
    private static final String ladderQueryUrl = "https://play.pokemonshowdown.com/ladder.php?&server=showdown&output=html&prefix=";

    private String format;
    private int pageLimit;
    private int rankMoreThan;
    private int minElo;
    private float minGxe;
    private LocalDate dateAfter;

    @Lazy
    @Autowired
    private BattleService battleService;

    @Lazy
    @Autowired
    private LadderService ladderService;

    private static final Logger log = LoggerFactory.getLogger(LadderCrawler.class);

    public LadderCrawler() {
        //magic number to provide qualify
        this.format = "gen9ou";
        this.pageLimit = 1;
        this.rankMoreThan = 150;
        this.minElo = 1800;
        this.minGxe = 77.0f;
        this.dateAfter = LocalDate.now().minusMonths(1);
    }

    public LadderCrawler(String format, int pageLimit, int rankMoreThan, int minElo, LocalDate dateAfter, float minGxe) {
        this.format = format;
        this.pageLimit = pageLimit;
        this.rankMoreThan = rankMoreThan;
        this.minElo = minElo;
        this.minGxe = minGxe;
        this.dateAfter = dateAfter;
    }

    public List<Battle> crawLadder() throws IOException {
        Ladder ladder = crawLadderRank();
        return crawLadderBattle(ladder);
    }

    public List<Battle> crawLadderBattle(Ladder ladder) {
        log.info(String.format("craw start: format:%s pageLimit:%d rankLimit:%d eloLimit:%d gxeLimit:%f dateLimit:%tF",
                getFormat(), getPageLimit(), getRankMoreThan(),
                getMinElo(), getMinGxe(), getDateAfter()));

        List<Future<List<Battle>>> crawFutures = new ArrayList<>();
        for (LadderRank ladderRank : ladder.getLadderRankList()) {
            String playerName = ladderRank.getName();
            PlayerReplayProvider replayProvider = new PlayerReplayProvider(playerName, format,
                    getDateAfter().atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
            var crawPlayerBattleFuture = battleService.crawBattleAndAnalyze(replayProvider);
            crawFutures.add(crawPlayerBattleFuture.crawFuture());
        }

        return crawFutures.stream().map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                log.error("craw ladder battle occur exception", e);
                throw new RuntimeException("craw ladder battle occur exception", e);
            }
        }).flatMap(List::stream).collect(Collectors.toList());
    }

    public Ladder crawLadderRank() throws IOException {
        ClassicHttpRequest httpGet = initLadderQueryGet();
        try {
            String html = HttpUtil.request(httpGet);
            Ladder ladder = LadderExtracter.extract(html, rankMoreThan, minElo, minGxe, format);
            ladderService.save(ladder);
            return ladder;
        } catch (Exception e) {
            log.error("craw ladder failed", e);
            throw e;
        }
    }

    private ClassicHttpRequest initLadderQueryGet() {
        String url = ladderQueryUrl + String.format("&format=%s", format);
        log.debug("init ladderQuery: {}", url);

        return ClassicRequestBuilder.get(url).build();
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public void setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
    }

    public int getRankMoreThan() {
        return rankMoreThan;
    }

    public void setRankMoreThan(int rankMoreThan) {
        this.rankMoreThan = rankMoreThan;
    }

    public int getMinElo() {
        return minElo;
    }

    public void setMinElo(int minElo) {
        this.minElo = minElo;
    }

    public float getMinGxe() {
        return minGxe;
    }

    public void setMinGxe(float minGxe) {
        this.minGxe = minGxe;
    }

    public LocalDate getDateAfter() {
        return dateAfter;
    }

    public void setDateAfter(LocalDate dateAfter) {
        this.dateAfter = dateAfter;
    }
}