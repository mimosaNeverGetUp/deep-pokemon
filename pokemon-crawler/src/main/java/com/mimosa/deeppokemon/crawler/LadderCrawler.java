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
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.LadderService;
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
import java.util.*;


public class LadderCrawler {
    private static final String ladderQueryUrl = "https://play.pokemonshowdown.com/ladder.php?&server=showdown&output=html&prefix=";
    private static final String playerQueryUrl = "https://replay.pokemonshowdown.com/api/replays/search";
    private String format;
    private int pageLimit;
    private int rankMoreThan;
    private int minElo;
    private float minGxe;
    private LocalDate dateAfter;

    @Autowired
    private TeamCrawler teamCrawler;

    @Lazy
    private BattleService battleService;

    @Lazy
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
        LinkedList<Battle> battles = new LinkedList<>();
        log.info(String.format("craw start: format:%s pageLimit:%d rankLimit:%d eloLimit:%d gxeLimit:%f dateLimit:%tF",
                getFormat(), getPageLimit(), getRankMoreThan(),
                getMinElo(), getMinGxe(), getDateAfter()));
        log.info("start craw ladder player");

        HashSet<String> preUrls = new HashSet<>();
        for (LadderRank ladderRank : ladder.getLadderRankList()) {
            log.info("start craw battle of player : {}", ladderRank.getName());
            String playerName = ladderRank.getName();
            List<Battle> battleList = crawPlayerBattleIfAbesent(playerName, preUrls, ladder.getFormat());
            if (battleList != null) {
                battles.addAll(battleList);
            }
        }
        battleService.savaAll(battles);
        return battles;
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

    public LinkedList<Battle> crawPlayerBattle(String name) {
        return crawPlayerBattleIfAbesent(name, null, null);
    }

    private LinkedList<Battle> crawPlayerBattleIfAbesent(String name, HashSet<String> preUrls,String format) {
        try (CloseableHttpClient httpClient = initClient()) {
            LinkedList<String> replayUrls = new LinkedList<>();
            for (int i = 1; i <= pageLimit; ++i) {
                HttpGet httpGet = initPlayerQueryGet(name, i, format);
                CloseableHttpResponse HttpResponse = httpClient.execute(httpGet);
                String response = EntityUtils.toString(HttpResponse.getEntity());
                ArrayList<String> playerReplayUrls = PlayerUrlExtracter.extract(response);
                if (playerReplayUrls.size() == 0) {
                    break;
                }
                replayUrls.addAll(playerReplayUrls);
                HttpResponse.close();
            }
            if (preUrls != null) {
                replayUrls.removeIf(preUrls::contains);
                preUrls.addAll(replayUrls);
            }
            String latestBattleId = null;
            if (battleService != null) {
                latestBattleId = battleService.findPlayerBattleIdLatest(name);
                log.debug(String.format("find %s  latestBattleId:%s", name, latestBattleId));
            }
            LinkedList<Battle> battles = new LinkedList<>();
            for (String url : replayUrls) {
                String battleId = url.substring(url.indexOf("//"));
                if (latestBattleId != null && latestBattleId.equals(battleId)) {
                    // 爬取到已有的数据，结束
                    break;
                }
                log.info("extract url:" + url);
                if (url.contains(format)) {
                    Battle battle = teamCrawler.craw(url);
                    if (battle != null) {
                        LocalDate date = battle.getDate();
                        if (dateAfter != null && date.isBefore(dateAfter)) {
                            // 爬取日期不符合要求，结束
                            break;
                        }
                        battles.add(battle);
                    }
                }
            }
            return battles;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
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
