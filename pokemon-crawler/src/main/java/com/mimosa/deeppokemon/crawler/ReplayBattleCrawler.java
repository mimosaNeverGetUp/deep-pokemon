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
import com.mimosa.deeppokemon.entity.BattleReplayData;
import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReplayBattleCrawler implements BattleCrawler {
    public static final String REPLAY_URL_PATTERN = "https://replay.pokemonshowdown.com/%s.json";

    @Autowired
    private BattleReplayExtractor battleReplayExtractor;

    public ReplayBattleCrawler() {
    }

    @Override
    public Battle craw(Replay replay) {
        HttpGet httpGet = initGet(replay.id());
        BattleReplayData battleReplay = HttpUtil.request(httpGet, BattleReplayData.class);
        Battle battle = battleReplayExtractor.extract(battleReplay);
        String battleID = replay.id();
        battle.setBattleID(battleID);
        return battle;
    }

    private HttpGet initGet(String url) {
        HttpGet httpGet = new HttpGet(String.format(REPLAY_URL_PATTERN, url));
        RequestConfig config = RequestConfig.custom().setConnectTimeout(20000)//创建连接的最长时间，单位是毫秒.
                .setConnectionRequestTimeout(20000)//设置获取连接的最长时间，单位毫秒
                .setSocketTimeout(20000)//设置数据传输的最长时间，单位毫秒
                .build();
        httpGet.setConfig(config);
        return httpGet;
    }
}