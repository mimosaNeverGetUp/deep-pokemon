/*
 * The MIT License
 *
 * Copyright (c) [2023]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mimosa.deeppokemon.crawler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.BattleReplayData;
import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.matcher.BattleMatcher;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@SpringBootTest
class ReplayBattleCrawlerTest {
    final private String ID_GEN9 = "test123";

    @Autowired
    private ReplayBattleCrawler crawler;

    @Value("classpath:api/battleReplay.json")
    Resource battleReplay;

    @Autowired
    BattleService battleSevice;

    @Autowired
    private final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    @Test
    void crawler() throws IOException {
        try (var mockHttpUtil = Mockito.mockStatic(HttpUtil.class)) {
            BattleReplayData battleReplayData =
                    OBJECT_MAPPER.readValue(battleReplay.getContentAsString(StandardCharsets.UTF_8), BattleReplayData.class);

            mockHttpUtil.when(() -> HttpUtil.request(Mockito.any(), Mockito.any(Class.class))).thenReturn(battleReplayData);
            Replay replay = new Replay(ID_GEN9, 0, null, 1790, new String[]{"PTLT508 jojo", "OU G Herbo"}, false);
            Battle battle = crawler.craw(new ReplaySource(null, Collections.singletonList(replay))).get(0);
            MatcherAssert.assertThat(battle, BattleMatcher.BATTLE_MATCHER);
            Assertions.assertNotEquals(0, battle.getAvageRating());
        }
    }
}