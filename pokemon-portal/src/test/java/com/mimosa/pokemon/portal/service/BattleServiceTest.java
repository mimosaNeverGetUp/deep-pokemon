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

import com.mimosa.deeppokemon.entity.Ladder;
import com.mimosa.deeppokemon.entity.LadderRank;
import com.mimosa.pokemon.portal.config.MongodbTestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class BattleServiceTest {
    @Autowired
    BattleService battleService;

    @Autowired
    PlayerService playerService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    static void initDb(@Autowired MongoTemplate mongoTemplate) {
        LadderRank mimosa = new LadderRank("mimosa", 1000, 0, 0f);
        Ladder ladder = new Ladder();
        ladder.setLadderRankList(List.of(mimosa));
        ladder.setDate(LocalDate.now());
        mongoTemplate.save(ladder);
    }

    @Test
    void listPlayer() throws Exception {
        mockMvc.perform(get("/record")
                .with(oauth2Login())
                .queryParam("name", "mimosa")
                .queryParam("page", "1")).andExpect(status().isOk()).andDo(print());
        Assertions.assertNotNull(playerService.queryPlayerLadderRank("mimosa"));
    }

    @Test
    void team() {
//        List<Pair<Team, String>> list = battleService.Team(1);
//        for (Pair<Team, String> pair : list) {
//            System.out.println(pair.getKey()+pair.getValue());
//        }
    }

    //    @Test
    void statistic() throws Exception {


    }

    @Test
    void mapReduce() throws Exception {
        // todo补充新统计方法单元测试
    }

    @Test
    void mapReduceAll() throws Exception {

    }

    @Test
    void mapReduceAllTest() throws Exception {

    }
}