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
import com.mimosa.deeppokemon.task.CrawBattleTask;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class LadderCrawler {
    private static final String ladderQueryUrl = "https://play.pokemonshowdown.com/ladder.php?&server=showdown&output=html&prefix=";
    private static final String playerQueryUrl = "https://replay.pokemonshowdown.com/api/replays/search";
    public static final ThreadPoolExecutor CRAW_BATTLE_EXECUTOR = new ThreadPoolExecutor(12, 12, 0,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private String format;
    private int pageLimit;
    private int rankMoreThan;
    private int minElo;
    private float minGxe;
    private LocalDate dateAfter;

    @Autowired
    private BattleCrawler battleCrawler;

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

    public LadderCrawler(String format, int pageLimit, int rankmoreThan, int minElo, LocalDate dateAfter, float minGxe) {
        this.format = format;
        this.pageLimit = pageLimit;
        this.rankMoreThan = rankmoreThan;
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

        List<Future<List<Battle>>> futures = new ArrayList<>();
        for (LadderRank ladderRank : ladder.getLadderRankList()) {
            String playerName = ladderRank.getName();
            CrawBattleTask crawBattleTask = new CrawBattleTask(new PlayerReplayProvider(playerName, format,
                    getDateAfter().atStartOfDay(ZoneId.systemDefault()).toEpochSecond()),
                    battleCrawler, battleService);
            var crawPlayerBattleFuture = CompletableFuture.supplyAsync(crawBattleTask::call, CRAW_BATTLE_EXECUTOR);
            futures.add(crawPlayerBattleFuture);
        }

        return futures.stream().map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                log.error("craw ladder battle occur exception", e);
                throw new RuntimeException("craw ladder battle occur exception", e);
            }
        }).flatMap(List::stream).collect(Collectors.toList());
    }

    public Ladder crawLadderRank() throws IOException {
        HttpGet httpGet = initLadeerQueryGet();
        try (CloseableHttpClient httpClient = initClient(); CloseableHttpResponse HttpResponse = httpClient.execute(httpGet)) {
            String html = EntityUtils.toString(HttpResponse.getEntity());
            Ladder ladder = LadderExtracter.extract(html, rankMoreThan, minElo, minGxe, format);
            ladderService.save(ladder);
            return ladder;
        } catch (Exception e) {
            log.error("craw ladder failed", e);
            throw e;
        }
    }

    private CloseableHttpClient initClient() {
        CookieStore httpCookieStore = new BasicCookieStore();
        return HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore).build();
    }

    private HttpGet initLadeerQueryGet() {
        String url = ladderQueryUrl + String.format("&format=%s", format);
        log.debug("init ladderQuery: {}", url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "*/*");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Mobile Safari/537.36");
        RequestConfig config = RequestConfig.custom().setConnectTimeout(20000).//创建连接的最长时间，单位是毫秒
                setConnectionRequestTimeout(20000).//设置获取连接的最长时间，单位毫秒
                setSocketTimeout(20000)//设置数据传输的最长时间，单位毫秒
                .build();
        httpGet.setConfig(config);
        return httpGet;
    }

    private HttpGet initPlayerQueryGet(String playerName, int pageNumber, String format) {
        String url = playerQueryUrl + String.format("?username=%s", playerName.replaceAll(" ", "+"))
                + String.format("&page=%d", pageNumber);
        if (format != null) {
            url += "&format=" + format;
        }
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "*/*");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Mobile Safari/537.36");
        RequestConfig config = RequestConfig.custom().setConnectTimeout(20000).//创建连接的最长时间，单位是毫秒
                setConnectionRequestTimeout(20000).//设置获取连接的最长时间，单位毫秒
                setSocketTimeout(20000)//设置数据传输的最长时间，单位毫秒
                .build();
        httpGet.setConfig(config);
        return httpGet;
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