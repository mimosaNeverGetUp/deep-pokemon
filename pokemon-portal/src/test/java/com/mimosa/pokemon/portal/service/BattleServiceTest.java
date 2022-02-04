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
import com.mimosa.pokemon.portal.entity.MapResult;
import com.mimosa.pokemon.portal.entity.Statistic;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
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
        LocalDate dayB = LocalDate.now();
        LocalDate dayA = dayB.minusMonths(1);
        Pair<Pair<Float, Float>, List<Team>> pair = battleService.statistic("Cinderace", dayA, dayB);
        System.out.println(pair.getKey().getKey());
        System.out.println(pair.getKey().getValue());

    }

    @Test
    void mapReduce() throws Exception {
        Statistic statistic = battleService.mapReduce(new Query(), "Chansey");
        System.out.println(statistic);
    }

    @Test
    void mapReduceAll() throws Exception {
        Query query = new BasicQuery("{}");
        List<MapResult> list=battleService.mapReduceAll(query);
        System.out.println(list.size());
        for (MapResult result : list) {
            System.out.println(result);
        }
    }

    @Test
    void mapReduceAllTest() throws Exception {
        List<MapResult> mapResultList = battleService.mapReduceAllDetails(new Query());
        for (MapResult result : mapResultList) {
            System.out.println(result);
        }
    }
}