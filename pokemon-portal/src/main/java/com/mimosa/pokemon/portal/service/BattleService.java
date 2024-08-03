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

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.stat.*;
import com.mimosa.pokemon.portal.dto.BattleDto;
import com.mimosa.pokemon.portal.dto.BattleTeamDto;
import com.mimosa.pokemon.portal.entity.PageResponse;
import com.mimosa.pokemon.portal.service.microservice.CrawlerApi;
import com.mimosa.pokemon.portal.util.CollectionUtils;
import com.mimosa.pokemon.portal.util.MongodbUtils;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class BattleService {
    protected static final String BATTLE = "battle";
    protected static final String TEAMS = "teams";
    protected static final String BATTLE_ID = "battleId";
    protected static final String BATTLE_TEAM = "battle_team";
    protected static final String ID = "_id";
    protected static final String DATE = "date";
    protected static final String TYPE = "type";
    protected static final String AVAGE_RATING = "avageRating";
    protected static final String WINNER = "winner";
    private final MongoTemplate mongoTemplate;
    private final CrawlerApi crawlerApi;

    public BattleService(MongoTemplate mongoTemplate, CrawlerApi crawlerApi) {
        this.mongoTemplate = mongoTemplate;
        this.crawlerApi = crawlerApi;
    }

    @Cacheable("playerBattle")
    public PageResponse<BattleDto> listBattleByName(String playerName, int page, int row) {
        Criteria criteria = Criteria.where("players").in(playerName);
        long count = mongoTemplate.count(new Query(criteria), Battle.class);
        if (count == 0) {
            return new PageResponse<>(count, page, row, Collections.emptyList());
        }

        MatchOperation matchOperation = Aggregation.match(criteria);
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, DATE);
        ProjectionOperation projectionOperation = Aggregation.project(ID, TYPE, AVAGE_RATING, WINNER, DATE);
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from(BATTLE_TEAM)
                .localField(ID)
                .foreignField(BATTLE_ID)
                .as(TEAMS);
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                sortOperation,
                projectionOperation,
                Aggregation.skip((long) (page) * row),
                Aggregation.limit(row),
                lookupOperation
        );

        List<BattleDto> battles = mongoTemplate.aggregate(aggregation, BATTLE, BattleDto.class).getMappedResults();
        return new PageResponse<>(count, page, row, battles);
    }

    public List<BattleTeam> listRecentTeam(String playerName) {
        Criteria criteria = Criteria.where("playerName").is(playerName)
                .andOperator(Criteria.where("tier").in("gen9ou", "[Gen 9] OU"));
        Query query = new Query(criteria)
                .with(Sort.by(Sort.Order.desc("battleDate")))
                .limit(2);
        return mongoTemplate.find(query, BattleTeam.class);
    }

    @Cacheable("team")
    @RegisterReflectionForBinding(BattleTeamDto.class)
    public PageResponse<BattleTeam> team(int page, int row, List<String> tags, List<String> pokemonNames, String dayAfter,
                                            String dayBefore) {
        Criteria criteria = new Criteria();
        if (StringUtils.hasText(dayAfter)) {
            LocalDate after = LocalDate.parse(dayAfter, DateTimeFormatter.ISO_DATE);
            criteria.andOperator(Criteria.where(DATE).lte(after));
        }
        if (StringUtils.hasText(dayBefore)) {
            LocalDate before = LocalDate.parse(dayBefore, DateTimeFormatter.ISO_DATE);
            criteria.andOperator(Criteria.where(DATE).gte(before));

        }
        if (CollectionUtils.hasNotNullObject(tags)) {
            criteria.andOperator(Criteria.where("tagSet").all(tags));
        }
        if (CollectionUtils.hasNotNullObject(pokemonNames)) {
            criteria.andOperator(Criteria.where("pokemons.name").all(pokemonNames));
        }

        Query query = new Query(criteria).with(Sort.by(Sort.Order.desc("battleDate")));
        long count = mongoTemplate.count(query, BattleTeam.class);
        MongodbUtils.withPageOperation(query, page, row);
        List<BattleTeam> battleTeams = mongoTemplate.find(query, BattleTeam.class);
        return new PageResponse<>(count, page, row, battleTeams);
    }

    @Cacheable("battlestat")
    @RegisterReflectionForBinding({BattleStat.class, PlayerStat.class, PokemonBattleStat.class, TurnStat.class,
            TurnPlayerStat.class, TurnPokemonStat.class})
    public BattleStat battleStat(String battleId) {
        BattleStat battleStat = mongoTemplate.findById(battleId, BattleStat.class);
        if (battleStat == null) {
            battleStat = crawlerApi.battleStat(battleId);
        }
        return battleStat;
    }
}