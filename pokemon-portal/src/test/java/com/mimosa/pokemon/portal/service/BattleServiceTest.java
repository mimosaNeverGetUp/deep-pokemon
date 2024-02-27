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

import com.mimosa.deeppokemon.entity.Player;
import com.mimosa.deeppokemon.entity.Team;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
class BattleServiceTest {

    @Autowired
    BattleService battleService;

    @Autowired
    PlayerService playerService;

    @Test
    void listTeamByPlayerList() {
        List<Player> playerList = (List<Player>) (playerService.listPlayerRank(1,25).getData());
        List<Team> teamList = battleService.listTeamByPlayerList(playerList);
        for (Player player : playerList) {
            System.out.println(player.getName());
        }
        for (Team team : teamList) {
            System.out.println(team);
        }
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