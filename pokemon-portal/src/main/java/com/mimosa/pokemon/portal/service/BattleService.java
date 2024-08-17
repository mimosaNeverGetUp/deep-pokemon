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
import com.mimosa.deeppokemon.entity.TeamGroup;
import com.mimosa.deeppokemon.entity.stat.*;
import com.mimosa.pokemon.portal.dto.BattleDto;
import com.mimosa.pokemon.portal.dto.TeamGroupDto;
import com.mimosa.pokemon.portal.entity.PageResponse;
import com.mimosa.pokemon.portal.service.microservice.CrawlerApi;
import com.mimosa.pokemon.portal.util.CollectionUtils;
import com.mimosa.pokemon.portal.util.MongodbUtils;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BattleService {
    protected static final String BATTLE = "battle";
    protected static final String BATTLE_ID = "battleId";
    protected static final String ID = "_id";
    protected static final String DATE = "date";
    protected static final String TYPE = "type";
    protected static final String AVAGE_RATING = "avageRating";
    protected static final String WINNER = "winner";
    protected static final Set<String> VALIDATE_TEAM_GROUP_SORT = new HashSet<>(List.of("maxRating", "uniquePlayerNum"
            , "latestBattleDate"));
    protected static final Set<String> MULTI_FORM_POKEMON = new HashSet<>(List.of("Urshifu", "Zamazenta"
            , "Greninja", "Dudunsparce"));
    protected static final String TEAM_SET = "team_set";
    protected static final String SET = "set";
    protected static final String TEAM_GROUP = "team_group";
    private final MongoTemplate mongoTemplate;
    private final CrawlerApi crawlerApi;

    public BattleService(MongoTemplate mongoTemplate, CrawlerApi crawlerApi) {
        this.mongoTemplate = mongoTemplate;
        this.crawlerApi = crawlerApi;
    }

    @Cacheable("playerBattle")
    public PageResponse<BattleDto> listBattleByName(String playerName, int page, int row) {
        Criteria criteria = Criteria.where("players").is(playerName);
        Query query = new Query(criteria);
        query.collation(Collation.of("en").strength(2));
        long count = mongoTemplate.count(query, Battle.class);
        if (count == 0) {
            return new PageResponse<>(count, page, row, Collections.emptyList());
        }
        query.with(Sort.by(Sort.Order.desc(DATE)));
        query.skip((long) (page) * row);
        query.limit(row);
        query.fields().include(ID, TYPE, AVAGE_RATING, WINNER, DATE);
        List<BattleDto> battles = mongoTemplate.find(query, BattleDto.class, BATTLE);

        Query statQuery = new Query(Criteria.where(BATTLE_ID).in(battles.stream().map(BattleDto::getId).toList()));
        List<BattleTeam> battleStats = mongoTemplate.find(statQuery, BattleTeam.class);
        Map<String, List<BattleTeam>> battleTeamsMap =
                battleStats.stream().collect(Collectors.groupingBy(BattleTeam::battleId));
        for (BattleDto battleDto : battles) {
            battleDto.setTeams(battleTeamsMap.get(battleDto.getId()));
        }

        return new PageResponse<>(count, page, row, battles);
    }

    public List<BattleTeam> listRecentTeam(String playerName) {
        Criteria criteria = Criteria.where("playerName").is(playerName)
                .andOperator(Criteria.where("tier").in("gen9ou", "[Gen 9] OU"));
        Query query = new Query(criteria)
                .with(Sort.by(Sort.Order.desc("battleDate")))
                .limit(2);
        query.collation(Collation.of("en").strength(2));
        return mongoTemplate.find(query, BattleTeam.class);
    }

    @Cacheable("teamGroup")
    @RegisterReflectionForBinding({TeamGroup.class, BattleTeam.class})
    public PageResponse<TeamGroupDto> teamGroup(int page, int row, List<String> tags, List<String> pokemonNames,
                                                String sort, String groupName) {
        if (!VALIDATE_TEAM_GROUP_SORT.contains(sort)) {
            throw new IllegalArgumentException("Invalid sort value: " + sort);
        }

        Criteria criteria = new Criteria();

        if (CollectionUtils.hasNotNullObject(tags)) {
            criteria.and("tagSet").in(tags);
        }
        if (CollectionUtils.hasNotNullObject(pokemonNames)) {
            List<String> puzzlePokemonNames = getPuzzlePokemonNames(pokemonNames);
            criteria.and("pokemons.name").all(puzzlePokemonNames);
        }

        Query query = new Query(criteria);
        long total = mongoTemplate.count(query, getTeamGroupCollection(groupName));

        MatchOperation matchOperation = Aggregation.match(criteria);
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, sort);
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from(getTeamSetCollection(groupName))
                .localField(ID)
                .foreignField(ID)
                .as(SET);
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                sortOperation,
                Aggregation.skip((long) (page) * row),
                Aggregation.limit(row),
                lookupOperation,
                Aggregation.addFields().
                        addFieldWithValue("teamSet", ArrayOperators.arrayOf(SET).first()).build(),
                Aggregation.stage("{ $project : { 'teams.pokemons': 0, 'teams._id': 0, 'teams.teamId': 0, 'teams" +
                        ".tagSet': 0,'teams.tier': 0, 'teams.battleType': 0, '_id': 0, 'set': 0} }"));
        MongodbUtils.withPageOperation(query, page, row);
        List<TeamGroupDto> battleTeams = mongoTemplate.aggregate(aggregation, getTeamGroupCollection(groupName),
                        TeamGroupDto.class)
                .getMappedResults();
        return new PageResponse<>(total, page, row, battleTeams);
    }

    private List<String> getPuzzlePokemonNames(List<String> pokemonNames) {
        List<String> puzzlePokemonNames = new ArrayList<>();
        for (String pokemonName : pokemonNames) {
            if (MULTI_FORM_POKEMON.contains(pokemonName)) {
                puzzlePokemonNames.add(pokemonName + "-*");
            } else {
                puzzlePokemonNames.add(pokemonName);
            }
        }
        return puzzlePokemonNames;
    }

    private String getTeamGroupCollection(String groupName) {
        if (groupName == null) {
            return TEAM_GROUP;
        }
        return String.format("%s_%s", TEAM_GROUP, groupName);
    }

    private String getTeamSetCollection(String groupName) {
        if (groupName == null) {
            return TEAM_SET;
        }
        return String.format("%s_%s", TEAM_SET, groupName);
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