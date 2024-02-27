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
import com.mimosa.pokemon.portal.dto.PokemonStatDto;
import com.mimosa.pokemon.portal.entity.stat.PokemonMoveStat;
import com.mimosa.pokemon.portal.entity.stat.PokemonUsageStat;
import com.mongodb.BasicDBObject;
import javafx.util.Pair;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BattleService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Battle> listBattleByName(String playerName, int page) {
        int num_perPage = 15;
        Query query = new BasicQuery(String.format("{ 'teams.playerName' : \"%s\" }", playerName))
                .with(Sort.by(Sort.Order.desc("date"))).limit(num_perPage).skip((page - 1) * num_perPage);
        return mongoTemplate.find(query, Battle.class, "battle");
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
                                                 LocalDate dayBefore) throws Exception {
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

    public List<Pair<Team, String>> Team(int page, String tag, String pokemonName, String dayAfter, String dayBefore) {
        int num_perPage = 20;
        ArrayList<Team> teamList = new ArrayList<>();
        List<Pair<Team, String>> teams = new ArrayList<>();

        List<AggregationOperation> operations = new ArrayList<>();
        //设置页数条件
        operations.add(Aggregation.sort(Sort.by(Sort.Order.desc("date"))));
        //动态设置条件
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (!StringUtils.isEmpty(dayAfter)) {
            LocalDate after = LocalDate.parse(dayAfter, format);
            operations.add(Aggregation.match(Criteria.where("date").gte(after)));
        }
        if (!StringUtils.isEmpty(dayBefore)) {
            LocalDate before = LocalDate.parse(dayBefore, format);
            operations.add(Aggregation.match(Criteria.where("date").lte(before)));
        }
        if (!StringUtils.isEmpty(tag)) {
            operations.add(Aggregation.match(Criteria.where("teams.tagSet").is(tag)));
        }
        if (!StringUtils.isEmpty(pokemonName)) {
            operations.add(Aggregation.match(Criteria.where("teams.pokemons.name").is(pokemonName)));
        }
        operations.add(Aggregation.skip((long) (page - 1) * num_perPage));
        operations.add(Aggregation.limit(num_perPage));
        Aggregation aggregation = Aggregation.newAggregation(operations);

        List<Battle> battles = mongoTemplate.aggregate(aggregation, "battle", Battle.class).getMappedResults();
        for (Battle battle : battles) {
            for (Team team : battle.getTeams()) {
                if (team == null) {
                    continue;
                }
                //需要进一步过滤battle里不符合条件或者重复的队伍
                boolean canAdd = true;
                for (int i = 0; i < teamList.size(); ++i) {
                    //检查重复
                    if (team.equals(teamList.get(i))) {
                        canAdd = false;
                        break;
                    }
                }
                if (!StringUtils.isEmpty(pokemonName)) {
                    boolean hasSpecifyPokemon = false;
                    for (Pokemon pokemon : team.getPokemons()) {
                        if (pokemonName.equals(pokemon.getName())) {
                            hasSpecifyPokemon = true;
                        }
                    }
                    if (!hasSpecifyPokemon) {
                        canAdd = false;
                    }
                }
                if (!StringUtils.isEmpty(tag)) {
                    if (!team.getTagSet().contains(Tag.valueOf(tag))) {
                        canAdd = false;
                    }
                }
                if (canAdd) {
                    teamList.add(team);
                    Pair<Team, String> pair = new Pair<>(team, battle.getBattleID());
                    teams.add(pair);
                }
            }
        }
        return teams;

    }
}
