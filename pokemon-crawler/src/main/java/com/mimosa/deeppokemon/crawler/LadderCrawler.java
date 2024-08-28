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
import com.mimosa.deeppokemon.task.entity.CrawBattleFuture;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class LadderCrawler {
    private static final String LADDER_QUERY_URL = "https://play.pokemonshowdown.com/ladder.php?&server=showdown&output=html&prefix=";

    private String format;
    private int pageLimit;
    private int rankMoreThan;
    private int minElo;
    private float minGxe;
    private LocalDate dateAfter;

    @Autowired
    private BattleService battleService;

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

    public CrawBattleFuture crawLadder(boolean overwrite) {
        Ladder ladder = crawLadderRank(overwrite);
        return crawLadderBattle(ladder);
    }

    public CrawBattleFuture crawLadderBattle(Ladder ladder) {
        log.info("craw start: format:{} pageLimit:{} rankLimit:{} eloLimit:{} gxeLimit:{} dateLimit:{}",
                getFormat(), getPageLimit(), getRankMoreThan(),
                getMinElo(), getMinGxe(), getDateAfter());

        List<CompletableFuture<List<Battle>>> crawFutures = new ArrayList<>();
        for (LadderRank ladderRank : ladder.getLadderRankList()) {
            String playerName = ladderRank.getName();
            PlayerReplayProvider replayProvider = new PlayerReplayProvider(playerName, format,
                    getDateAfter().atStartOfDay(ZoneId.systemDefault()).toEpochSecond(), getMinElo());
            var crawPlayerBattleFuture = battleService.crawBattle(replayProvider);
            crawFutures.add(crawPlayerBattleFuture.crawFuture());
        }
        CompletableFuture<Void> allBattleCrawFuture =
                CompletableFuture.allOf(crawFutures.toArray(new CompletableFuture[0]));
        CompletableFuture<List<Battle>> battleCrawFuture = allBattleCrawFuture.thenApply(v ->
                crawFutures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .toList());
        return new CrawBattleFuture(battleCrawFuture);
    }

    @CacheEvict(value = {"rank", "playerRank"}, allEntries = true)
    public Ladder crawLadderRank(boolean overwrite) {
        ClassicHttpRequest httpGet = initLadderQueryGet();
        String html = HttpUtil.request(httpGet);
        Ladder ladder = LadderExtracter.extract(html, rankMoreThan, minElo, minGxe, format);
        ladderService.save(ladder, overwrite);
        return ladder;
    }

    private ClassicHttpRequest initLadderQueryGet() {
        String url = LADDER_QUERY_URL + String.format("&format=%s", format);
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