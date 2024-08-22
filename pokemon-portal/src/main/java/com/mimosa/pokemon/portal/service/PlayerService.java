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
import com.mimosa.pokemon.portal.dto.PlayerRankDTO;
import com.mimosa.pokemon.portal.entity.PageResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PlayerService {
    private final MongoTemplate mongoTemplate;
    private final BattleService battleService;

    public PlayerService(MongoTemplate mongoTemplate, BattleService battleService) {
        this.mongoTemplate = mongoTemplate;
        this.battleService = battleService;
    }

    @Cacheable("playerRank")
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

    public LocalDate getUpdateTime() {
        Query query = new BasicQuery("{}")
                .with(Sort.by(Sort.Order.desc("date")))
                .limit(1);
        query.fields().include("date");
        return mongoTemplate.findOne(query, Ladder.class, "ladder").getDate();
    }

    @Cacheable("rank")
    public PageResponse<PlayerRankDTO> rank(@Min(0) int page, @Min(1) @Max(100) int row) {
        int start = page * row;
        int end = start + row;
        Ladder ladder = getLatestLadder();
        List<LadderRank> ladderRank = ladder.getLadderRankList();
        ladderRank.sort(Comparator.comparingInt(LadderRank::getRank));
        List<LadderRank> segmentLadderRank = new ArrayList<>(ladderRank.subList(start, end));

        List<PlayerRankDTO> playerRankDTOS = new ArrayList<>();

        for (var rank : segmentLadderRank) {
            var playerRankDTO = new PlayerRankDTO();
            playerRankDTO.setRank(rank.getRank());
            playerRankDTO.setElo(rank.getElo());
            playerRankDTO.setName(rank.getName());
            playerRankDTO.setGxe(rank.getGxe());
            playerRankDTO.setInfoDate(ladder.getDate());
            playerRankDTO.setRecentTeam(battleService.listRecentTeam(rank.getName()));
            playerRankDTOS.add(playerRankDTO);
        }
        return new PageResponse<>(ladderRank.size(), page, row, playerRankDTOS);
    }
}