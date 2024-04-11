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
import com.mimosa.pokemon.portal.dto.BattleTeamDto;
import com.mimosa.pokemon.portal.dto.PokemonStatDto;
import com.mimosa.pokemon.portal.entity.PageResponse;
import com.mimosa.pokemon.portal.entity.stat.PokemonMoveStat;
import com.mimosa.pokemon.portal.entity.stat.PokemonUsageStat;
import com.mimosa.pokemon.portal.util.CollectionUtils;
import com.mimosa.pokemon.portal.util.MongodbUtils;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BattleService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Cacheable("playerBattle")
    public PageResponse<Battle> listBattleByName(String playerName, int page, int row) {
        Query query =
                new BasicQuery(String.format("{ 'teams.playerName' : \"%s\" }", playerName)).
                        with(Sort.by(Sort.Order.desc("date")));
        long totalRecord = mongoTemplate.count(query, Battle.class);
        MongodbUtils.buildPageFacetAggregationOperation(query, page, row);

        List<Battle> battles = mongoTemplate.find(query, Battle.class, "battle");
        return new PageResponse<>(totalRecord, page, row, battles);
    }

    public List<Team> listTeamByLadderRank(List<LadderRank> ladderRanks) {
        ArrayList<Pokemon> emptyPokemons = new ArrayList<>(6);
        Pokemon emptyPokemon = new Pokemon("null");

        List<Team> teamList = new ArrayList<>();
        for (int i = 0; i < 6; ++i) {
            emptyPokemons.add(emptyPokemon);
        }
        for (LadderRank ladderRank : ladderRanks) {
            String queryString = String.format("{ 'teams.playerName' : \"%s\",'teams.tier' : \"[Gen 9] OU\" }", ladderRank.getName());
            System.out.println(queryString);
            Query query = new BasicQuery(queryString)
                    .with(Sort.by(Sort.Order.desc("date")))
                    .limit(2);
            List<Battle> battles = mongoTemplate.find(query, Battle.class, "battle");
            for (Battle battle : battles) {
                Team[] teams = battle.getTeams();
                for (Team team : teams) {
                    if (ladderRank.getName().equals(team.getPlayerName())) {
                        teamList.add(team);
                        break;
                    }
                }
            }
            Team emptyTeam = new Team();
            emptyTeam.setPokemons(emptyPokemons);
            emptyTeam.setPlayerName(ladderRank.getName());
            for (int j = 0; j < 2 - battles.size(); ++j) {
                teamList.add(emptyTeam);
            }
        }
        return teamList;
    }

    public List<Team> listTeamByPlayerList(List<Player> list) {
        ArrayList<Pokemon> emptyPokemons = new ArrayList<>(6);
        Pokemon emptyPokemon = new Pokemon("null");

        List<Team> teamList = new ArrayList<>();
        for (int i = 0; i < 6; ++i) {
            emptyPokemons.add(emptyPokemon);
        }
        for (Player player : list) {
            String queryString = String.format("{ 'teams.playerName' : \"%s\",'teams.tier' : \"[Gen 9] OU\" }", player.getName());
            System.out.println(queryString);
            Query query = new BasicQuery(queryString)
                    .with(Sort.by(Sort.Order.desc("date")))
                    .limit(2);
            List<Battle> battles = mongoTemplate.find(query, Battle.class, "battle");
            for (Battle battle : battles) {
                Team[] teams = battle.getTeams();
                for (Team team : teams) {
                    if (player.getName().equals(team.getPlayerName())) {
                        teamList.add(team);
                        break;
                    }
                }
            }
            Team emptyTeam = new Team();
            emptyTeam.setPokemons(emptyPokemons);
            emptyTeam.setPlayerName(player.getName());
            for (int j = 0; j < 2 - battles.size(); ++j) {
                teamList.add(emptyTeam);
            }
        }
        return teamList;
    }

    public List<PokemonStatDto> queryPokemonStat(LocalDate dayAfter,
                                                 LocalDate dayBefore) {
        Query query = new BasicQuery("{}").with(Sort.by(Sort.Order.desc("date")));
        Criteria criteria = Criteria.where("date").gte(dayAfter).lte(dayBefore);
        query.addCriteria(criteria);
        // 统计宝可梦使用率、招式使用率
        List<PokemonUsageStat> pokemonUsageStats = aggregationPokemonUsageStatistics(dayAfter, dayBefore);
        List<PokemonMoveStat> pokemonMoveStats = aggregationPokemonMoveStat(dayAfter, dayBefore);

        List<PokemonStatDto> pokemonStatDtos = new ArrayList<>();
        Map<String, List<PokemonMoveStat>> pokemonMoveStatMap = pokemonMoveStats.stream()
                .collect(Collectors.groupingBy(PokemonMoveStat::getName));

        // 转换为dto
        for (PokemonUsageStat pokemonUsageStat : pokemonUsageStats) {
            if (pokemonMoveStatMap.containsKey(pokemonUsageStat.getName())) {
                pokemonStatDtos.add(new PokemonStatDto(pokemonUsageStat.getName(), pokemonUsageStat,
                        pokemonMoveStatMap.get(pokemonUsageStat.getName()).get(0)));
            } else {
                PokemonMoveStat emptyMoveStat = new PokemonMoveStat();
                pokemonStatDtos.add(new PokemonStatDto(pokemonUsageStat.getName(), pokemonUsageStat,
                        emptyMoveStat));
            }

        }
        return pokemonStatDtos;
    }

    /**
     * 聚合统计宝可梦招式使用率
     */
    public List<PokemonMoveStat> aggregationPokemonMoveStat(LocalDate dayAfter, LocalDate dayBefore) {
        Criteria dateCondition = Criteria.where("date").gte(dayAfter).lte(dayBefore);
        Criteria fullPlayerCondition = Criteria.where("teams.0.playerName").ne("")
                .and("teams.1.playerName").ne("");
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(dateCondition),
                // 过率脏数据
                Aggregation.match(fullPlayerCondition),
                // moves数组拆为多条记录
                Aggregation.unwind("teams"),
                Aggregation.unwind("teams.pokemons"),
                Aggregation.unwind("teams.pokemons.moves"),

                // 统计宝可梦使用次数
                Aggregation.group("teams.pokemons.name")
                        .count().as("use")
                        .push("teams.pokemons.moves").as("movelist"),
                Aggregation.unwind("movelist"),
                // moves拆分聚合为多条记录
                Aggregation.group("_id", "movelist")
                        .count().as("moveUse")
                        .first("use").as("pokemonUse"),
                // 统计各招式使用率
                Aggregation.group("_id._id")
                        .first("pokemonUse").as("use")
                        .push(new BasicDBObject("name", "$_id.movelist")
                                .append("usePercent", new BasicDBObject("$multiply",
                                        new Object[]{
                                                new BasicDBObject("$divide", new Object[]{"$moveUse", "$pokemonUse"}),
                                                100
                                        })
                                )
                        ).as("moveUsage"),
                Aggregation.project("use", "moveUsage")
                        .and("_id").as("name").andExclude("_id"),
                Aggregation.sort(Sort.Direction.DESC, "use")
        );
        AggregationResults<PokemonMoveStat> aggregationResults =
                mongoTemplate.aggregate(aggregation, "battle", PokemonMoveStat.class);
        List<PokemonMoveStat> pokemonMoveStats = aggregationResults.getMappedResults();

        // 使用率排序
        pokemonMoveStats.forEach(stat -> stat.getMoveUsage()
                .sort(Comparator.comparingDouble(PokemonMoveStat.PokemonMoveUsageStat::getUsePercent).reversed()));
        return pokemonMoveStats;
    }

    /**
     * 聚合统计宝可梦使用率
     */
    public List<PokemonUsageStat> aggregationPokemonUsageStatistics(LocalDate dayAfter, LocalDate dayBefore) {
        Criteria dateCondition = Criteria.where("date").gte(dayAfter).lte(dayBefore);
        Criteria fullPlayerCondition = Criteria.where("teams.0.playerName").ne("")
                .and("teams.1.playerName").ne("");
        Query countQuery = new BasicQuery("{}");
        countQuery.addCriteria(dateCondition);
        countQuery.addCriteria(fullPlayerCondition);
        long totalGame = mongoTemplate.count(countQuery, "battle");
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(dateCondition),
                // 过率脏数据
                Aggregation.match(fullPlayerCondition),
                // 数组拆为多条记录
                Aggregation.unwind("teams"),
                Aggregation.unwind("teams.pokemons"),
                // 根据winner设置win字段
                Aggregation.project("teams", "winner")
                        .and("win").applyCondition(
                                ConditionalOperators.Cond.newBuilder()
                                        .when(Document.parse("{ $eq: [ \"$winner\", \"$teams.playerName\" ] }"))
                                        .then(1)
                                        .otherwise(0)
                        ),
                // 统计使用率胜率
                Aggregation.group("teams.pokemons.name")
                        .count().as("use")
                        .sum("win").as("win"),

                // 字段映射，排序
                Aggregation.project("use", "win").and("_id").as("name").andExclude("_id"),
                Aggregation.sort(Sort.Direction.DESC, "use")
        );
        AggregationResults<PokemonUsageStat> aggregationResults =
                mongoTemplate.aggregate(aggregation, "battle", PokemonUsageStat.class);
        List<PokemonUsageStat> pokemonUsageStats = aggregationResults.getMappedResults();
        pokemonUsageStats.forEach(stat -> {
            stat.setTotalGame(totalGame);
            stat.setUsePercent((double) stat.getUse() / totalGame);
            stat.setWinPercent((double) stat.getWin() / stat.getUse());
        });
        return pokemonUsageStats;
    }

    @Cacheable("team")
    public PageResponse<BattleTeamDto> team(int page, int row, List<String> tags, List<String> pokemonNames, String dayAfter,
                                            String dayBefore) {
        Aggregation queryAggregation = buildTeamQueryAggregation(tags, pokemonNames, dayAfter, dayBefore);
        MongodbUtils.addPageFacetOperation(queryAggregation, page, row);

        AggregationResults<Document> result = mongoTemplate.aggregate(queryAggregation, "battle",
                Document.class);
        return MongodbUtils.parsePageAggregationResult(result, page, row, BattleTeamDto.class);
    }

    @NotNull
    private static Aggregation buildTeamQueryAggregation(List<String> tags, List<String> pokemonNames, String dayAfter,
                                                         String dayBefore) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        aggregationOperations.add(Aggregation.sort(Sort.by(Sort.Order.desc("date"))));
        aggregationOperations.add(Aggregation.unwind("teams"));

        //动态设置条件
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (StringUtils.hasText(dayAfter)) {
            LocalDate after = LocalDate.parse(dayAfter, format);
            aggregationOperations.add(Aggregation.match(Criteria.where("date").gte(after)));
        }
        if (StringUtils.hasText(dayBefore)) {
            LocalDate before = LocalDate.parse(dayBefore, format);
            aggregationOperations.add(Aggregation.match(Criteria.where("date").gte(before)));
        }

        // 队伍过滤
        List<Criteria> teamCriterias = new ArrayList<>();
        if (CollectionUtils.hasNotNullObject(tags)) {
            teamCriterias.add(Criteria.where("teams.tagSet").in(tags));
        }
        if (CollectionUtils.hasNotNullObject(pokemonNames)) {
            teamCriterias.add(Criteria.where("teams.pokemons").elemMatch(Criteria.where("name").in(pokemonNames)));
        }
        if (!teamCriterias.isEmpty()) {
            aggregationOperations.add(
                    Aggregation.match(new Criteria().andOperator(teamCriterias)));
        }

        Field[] projectFields = new Field[]{Fields.field("team","teams")};
        aggregationOperations.add(Aggregation.project(Fields.from(projectFields))
                .and("_id").as("battleId").andExclude("_id"));

        return Aggregation.newAggregation(aggregationOperations);
    }
}