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

package com.mimosa.pokemon.portal.service;

import com.mimosa.pokemon.portal.config.MongodbTestConfig;
import com.mimosa.pokemon.portal.dto.PlayerRankDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ContextConfiguration(classes = MongodbTestConfig.class)
@AutoConfigureMockMvc
@SpringBootTest
class BattleServiceTest {
    public static final String EXIST_PLAYER_NAME = "fuck-yu chi-yu";
    @Autowired
    BattleService battleService;

    @Autowired
    PlayerService playerService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MockMvc mockMvc;

    @Test
    void listPlayer() throws Exception {
        PlayerRankDTO playerRankDTO = playerService.queryPlayerLadderRank(EXIST_PLAYER_NAME);
        Assertions.assertAll(
                () -> Assertions.assertNotNull(playerRankDTO),
                () -> Assertions.assertNotNull(playerRankDTO.getRank()),
                () -> Assertions.assertNotNull(playerRankDTO.getElo()),
//                ()->Assertions.assertNotNull(playerRankDTO.getFormat()),
                () -> Assertions.assertNotNull(playerRankDTO.getInfoDate())
        );
    }
}