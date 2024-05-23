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

import com.mimosa.deeppokemon.entity.*;
import com.mimosa.pokemon.portal.dto.PlayerRankDTO;
import com.mimosa.pokemon.portal.entity.JsonArrayResponse;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PlayerService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BattleService battleService;

    public PlayerRankDTO queryPlayerLadderRank(@NonNull String playerName) {
        Ladder ladder = getLatestLadder();
        LadderRank playerRank =
                ladder.getLadderRankList().stream().filter(ladderRank -> playerName.equals(ladderRank.getName()))
                        .findFirst().orElse(new LadderRank(playerName, 0, 0, 0.0F));
        return new PlayerRankDTO(ladder.getDate(), playerRank.getName(), playerRank.getElo(),
                playerRank.getRank(), playerRank.getGxe(), null);
    }

    @RegisterReflectionForBinding({Ladder.class})
    public Ladder getLatestLadder() {
        //查询数据库里储存的最新的日期
        Query query = new BasicQuery("{}")
                .with(Sort.by(Sort.Order.desc("date")))
                .limit(1);
        return mongoTemplate.findOne(query, Ladder.class, "ladder");
    }

    public JsonArrayResponse listPlayerRank(int page, int limit) {
        //查询数据库里储存的最新的日期
        int start = limit * (page - 1);
        Query query = new BasicQuery("{}")
                .with(Sort.by(Sort.Order.desc("infoDate")))
                .limit(1);
        Player player = mongoTemplate.findOne(query, Player.class, "player");
        LocalDate date = player.getInfoDate();
        //查询最新日期的列表玩家
        query = new BasicQuery("{}")
                .with(Sort.by(Sort.Order.asc("rank")));
        Criteria criteria = Criteria.where("infoDate").gte(date);
        query.addCriteria(criteria);
        long count = mongoTemplate.count(query, Player.class, "player");
        query.skip(start);
        query.limit(limit);

        List<Player> playerList = mongoTemplate.find(query, Player.class, "player");
        //去除列表里重复和范围外元素
        for (int i = 0; i < playerList.size(); ++i) {
            for (int j = playerList.size() - 1; j > i; --j) {
                if (playerList.get(i).equals(playerList.get(j))) {
                    playerList.remove(j);
                }
            }
        }
        JsonArrayResponse response = new JsonArrayResponse();
        response.setData(playerList);
        response.setCode(0);
        response.setCount(count);
        response.setMsg("");
        return response;
    }

    public JsonArrayResponse listPlayerDTORank(int page, int limit) {
        // 查询数据库里储存的最新的日期
        int start = limit * (page - 1);
        Query query = new BasicQuery("{}")
                .with(Sort.by(Sort.Order.desc("infoDate")))
                .limit(1);
        Player player = mongoTemplate.findOne(query, Player.class, "player");
        LocalDate date = player.getInfoDate();
        // 查询最新日期的列表玩家
        query = new BasicQuery("{}")
                .with(Sort.by(Sort.Order.asc("rank")));
        Criteria criteria = Criteria.where("infoDate").gte(date);
        query.addCriteria(criteria);
        long count = mongoTemplate.count(query, Player.class, "player");
        query.skip(start);
        query.limit(limit);

        List<Player> playerList = mongoTemplate.find(query, Player.class, "player");

        // 去除列表里重复和范围外元素
        for (int i = 0; i < playerList.size(); ++i) {
            for (int j = playerList.size() - 1; j > i; --j) {
                if (playerList.get(i).equals(playerList.get(j))) {
                    playerList.remove(j);
                }
            }
        }
        JsonArrayResponse response = new JsonArrayResponse();
        response.setData(transferPlayerRankDTO(playerList));
        response.setCode(0);
        response.setCount(count);
        response.setMsg("");
        return response;
    }

    public List<PlayerRankDTO> transferPlayerRankDTO(List<Player> playerLists) {
        List<PlayerRankDTO> playerRankDTOS = new ArrayList<>();
        for (Player player : playerLists) {
            PlayerRankDTO playerRankDTO = new PlayerRankDTO(player.getInfoDate(), player.getName(), player.getElo(),
                    player.getRank(), player.getGxe(), player.getFormat());
            playerRankDTO.setRecentTeam(battleService.listTeamByPlayerList(Collections.singletonList(player)));
            playerRankDTOS.add(playerRankDTO);
        }
        return playerRankDTOS;
    }
}