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
import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReplayBattleCrawler implements BattleCrawler {
    public static final String REPLAY_URL_PATTERN = "https://replay.pokemonshowdown.com/%s.json";

    private final BattleReplayExtractor battleReplayExtractor;

    public ReplayBattleCrawler(BattleReplayExtractor battleReplayExtractor) {
        this.battleReplayExtractor = battleReplayExtractor;
    }

    @Override
    @RegisterReflectionForBinding(BattleReplayData.class)
    public List<Battle> craw(ReplaySource replaySource) {
        List<Battle> battles = new ArrayList<>();
        for (Replay replay : replaySource.replayList()) {
            ClassicHttpRequest httpGet = initGet(replay.getId());
            BattleReplayData battleReplay = HttpUtil.request(httpGet, BattleReplayData.class);
            Battle battle = battleReplayExtractor.extract(battleReplay);
            battle.setType(replaySource.replayType());
            battles.add(battle);
        }
        return battles;
    }

    private ClassicHttpRequest initGet(String battleId) {
        return ClassicRequestBuilder.get(String.format(REPLAY_URL_PATTERN, battleId)).build();
    }
}