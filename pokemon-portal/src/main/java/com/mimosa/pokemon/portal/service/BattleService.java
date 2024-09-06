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
import com.mimosa.deeppokemon.entity.stat.*;
import com.mimosa.deeppokemon.entity.tour.TourBattle;
import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import com.mimosa.deeppokemon.entity.tour.TourPlayerRecord;
import com.mimosa.deeppokemon.entity.tour.TourTeam;
import com.mimosa.pokemon.portal.dto.BattleDto;
import com.mimosa.pokemon.portal.dto.BattleTeamDto;
import com.mimosa.pokemon.portal.dto.TeamGroupDto;
import com.mimosa.pokemon.portal.entity.PageResponse;
import com.mimosa.pokemon.portal.util.CollectionUtils;
import com.mimosa.pokemon.portal.util.MongodbUtils;
import org.bson.types.Binary;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import java.time.LocalDate;
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
            , "latestBattleDate", "maxPlayerWinDif", "maxPlayerWinRate"));
    protected static final Set<String> MULTI_FORM_POKEMON = new HashSet<>(List.of("Urshifu", "Zamazenta"
            , "Greninja", "Dudunsparce"));
    protected static final String TEAM_SET = "team_set";
    protected static final String SET = "set";
    protected static final String TEAM_GROUP = "team_group";
    protected static final String TOUR_BATTLE = "tour_battle";
    protected static final String TOUR_TEAM = "tour_team";
    protected static final String TEAMS = "teams";
    protected static final String TOUR_ID = "tourId";
    protected static final String STAGE = "stage";
    protected static final String WIN_SMOGON_PLAYER_NAME = "winSmogonPlayerName";
    protected static final String SMOGON_PLAYER = "smogonPlayer";
    protected static final String SMOGON_PLAYER_NAME = "smogonPlayer.name";
    protected static final String TEAM_ID = "teamId";
    private final MongoTemplate mongoTemplate;

    public BattleService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
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
                battleStats.stream().collect(Collectors.groupingBy(BattleTeam::getBattleId));
        for (BattleDto battleDto : battles) {
            battleDto.setTeams(battleTeamsMap.get(battleDto.getId()));
        }

        return new PageResponse<>(count, page, row, battles);
    }

    @Cacheable("tourPlayerBattle")
    public PageResponse<BattleDto> listTourBattle(String playerName, int page, int row) {
        Criteria criteria = Criteria.where(SMOGON_PLAYER_NAME).in(playerName);
        long count = mongoTemplate.count(new Query(criteria), TourBattle.class);
        if (count == 0) {
            return new PageResponse<>(count, page, row, Collections.emptyList());
        }

        MatchOperation matchOperation = Aggregation.match(criteria);
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, DATE);
        ProjectionOperation projectionOperation = Aggregation.project(ID, TYPE, AVAGE_RATING, WINNER, DATE,
                TOUR_ID, STAGE, WIN_SMOGON_PLAYER_NAME, SMOGON_PLAYER);
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from(TOUR_TEAM)
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

        List<BattleDto> battles = mongoTemplate.aggregate(aggregation, TOUR_BATTLE, BattleDto.class).getMappedResults();
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
    @RegisterReflectionForBinding({TeamGroupDto.class, BattleTeam.class, BattleDto.class, TourPlayer.class,
            TourPlayerRecord.class})
    public PageResponse<TeamGroupDto> teamGroup(int page, int row, List<String> tags, List<String> pokemonNames,
                                                List<String> playerNames, String sort, String groupName) {
        if (!VALIDATE_TEAM_GROUP_SORT.contains(sort)) {
            throw new IllegalArgumentException("Invalid sort value: " + sort);
        }

        Criteria criteria = new Criteria();

        if (CollectionUtils.hasNotNullObject(playerNames)) {
            criteria.and(TEAMS).elemMatch(new Criteria("player.name").in(playerNames));
        }

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

    public TeamGroupDto searchTeam(Binary teamId) {
        List<BattleTeam> teamList = new ArrayList<>();
        Query ladderTeamQuery = new Query(Criteria.where(TEAM_ID).is(teamId)).with(Sort.by(Sort.Order.desc("rating")));
        ladderTeamQuery.limit(50);
        teamList.addAll(mongoTemplate.find(ladderTeamQuery, BattleTeam.class));
        Query tourTeamQuery = new Query(Criteria.where(TEAM_ID).is(teamId));
        tourTeamQuery.limit(50);
        teamList.addAll(mongoTemplate.find(tourTeamQuery, TourTeam.class));

        if (teamList.isEmpty()) {
            return null;
        }
        TeamSet teamSet = buildTeamSet(teamList);
        return new TeamGroupDto(teamSet.id(), teamSet.tier(), null, teamList.size(), null,
                null, null, teamList.get(0).getPokemons(), null, null,
                convert(teamList), teamSet);
    }

    public TeamSet buildTeamSet(List<BattleTeam> teams) {
        if (teams == null || teams.isEmpty()) {
            return null;
        }
        Map<String, Map<String, Integer>> moveMap = new HashMap<>();
        Map<String, Map<String, Integer>> itemsMap = new HashMap<>();
        Map<String, Map<String, Integer>> abilityMap = new HashMap<>();
        Map<String, Map<String, Integer>> teraTypes = new HashMap<>();
        for (BattleTeam team : teams) {
            countPokemonSet(team, moveMap, itemsMap, abilityMap, teraTypes);
        }

        List<PokemonBuildSet> pokemonBuildSets = new ArrayList<>();
        for (var entrySet : moveMap.entrySet()) {
            String pokemon = entrySet.getKey();
            pokemonBuildSets.add(new PokemonBuildSet(pokemon, descSortByValue(moveMap.get(pokemon)),
                    descSortByValue(abilityMap.get(pokemon)), descSortByValue(itemsMap.get(pokemon)),
                    descSortByValue(teraTypes.get(pokemon))));
        }

        LocalDate minReplayDate = teams.stream()
                .map(BattleTeam::getBattleDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);
        return new TeamSet(new Binary(teams.get(0).getTeamId()), teams.get(0).getTier(), teams.size(), minReplayDate,
                null, pokemonBuildSets);
    }

    private static void countPokemonSet(BattleTeam team,
                                        Map<String, Map<String, Integer>> moveMap,
                                        Map<String, Map<String, Integer>> itemsMap,
                                        Map<String, Map<String, Integer>> abilityMap,
                                        Map<String, Map<String, Integer>> teraTypes) {
        for (Pokemon pokemon : team.getPokemons()) {
            if (!moveMap.containsKey(pokemon.getName())) {
                moveMap.put(pokemon.getName(), new HashMap<>());
                itemsMap.put(pokemon.getName(), new HashMap<>());
                abilityMap.put(pokemon.getName(), new HashMap<>());
                teraTypes.put(pokemon.getName(), new HashMap<>());
            }

            if (pokemon.getItem() != null) {
                itemsMap.get(pokemon.getName()).merge(pokemon.getItem().trim(), 1, Integer::sum);
            }

            if (pokemon.getAbility() != null) {
                abilityMap.get(pokemon.getName()).merge(pokemon.getAbility().trim(), 1, Integer::sum);
            }

            if (pokemon.getTeraType() != null) {
                teraTypes.get(pokemon.getName()).merge(pokemon.getTeraType().trim(), 1, Integer::sum);
            }

            for (String move : pokemon.getMoves()) {
                moveMap.get(pokemon.getName()).merge(move.trim(), 1, Integer::sum);
            }
        }
    }

    private <K, V extends Comparable<? super V>> List<K> descSortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        List<K> result = new ArrayList<>();
        for (Map.Entry<K, V> entry : list) {
            result.add(entry.getKey());
        }

        return result;
    }

    private List<BattleTeamDto> convert(List<BattleTeam> battleTeams) {
        List<BattleTeamDto> battleTeamDtos = new ArrayList<>();
        for (BattleTeam battleTeam : battleTeams) {
            battleTeamDtos.add(new BattleTeamDto(battleTeam.getBattleId(), battleTeam.getBattleDate(), null
                    , battleTeam.getPlayerName(), null, null, null, null));
        }
        return battleTeamDtos;
    }

    @RegisterReflectionForBinding({BattleStat.class, PlayerStat.class, PokemonBattleStat.class, TurnStat.class,
            TurnPlayerStat.class, TurnPokemonStat.class})
    public BattleStat battleStat(String battleId) {
        BattleStat battleStat = mongoTemplate.findById(battleId, BattleStat.class);
        if (battleStat == null) {
            throw new ServerErrorException("stat is not exist", null);
        }
        return battleStat;
    }
}