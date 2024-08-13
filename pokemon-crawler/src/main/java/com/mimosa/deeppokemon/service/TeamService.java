/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.google.common.collect.Lists;
import com.mimosa.deeppokemon.entity.*;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TeamService {
    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    protected static final int BATCH_SIZE = 1000;
    private final MongoTemplate mongoTemplate;

    public TeamService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @RegisterReflectionForBinding({TeamGroup.class, TeamSet.class, PokemonBuildSet.class})
    public void updateTeamSet(TeamGroupDetail teamGroupDetail) {
        List<Binary> needUpdateTeamGroup = new ArrayList<>();

        Query query = new Query()
                .with(Sort.by(Sort.Order.desc("latestBattleDate")))
                .cursorBatchSize(BATCH_SIZE);
        query.fields().include("_id", "replayNum", "uniquePlayerNum", "maxRating");
        Stream<TeamGroup> teamGroupStream = mongoTemplate.stream(query, TeamGroup.class, teamGroupDetail.teamGroupCollectionName());

        List<TeamGroup> batchTeamGroup = new ArrayList<>();
        teamGroupStream.forEach(teamGroup -> {
            batchTeamGroup.add(teamGroup);
            if (batchTeamGroup.size() >= BATCH_SIZE) {
                try {
                    needUpdateTeamGroup.addAll(queryNeedUpdateTeamGroup(batchTeamGroup,
                            teamGroupDetail.teamSetCollectionName(), teamGroupDetail.start()));
                } catch (Exception e) {
                    log.error("queryNeedUpdateTeamGroup exception", e);
                } finally {
                    batchTeamGroup.clear();
                }
            }
        });
        if (!batchTeamGroup.isEmpty()) {
            try {
                needUpdateTeamGroup.addAll(queryNeedUpdateTeamGroup(batchTeamGroup,
                        teamGroupDetail.teamSetCollectionName(), teamGroupDetail.start()));
            } catch (Exception e) {
                log.error("queryNeedUpdateTeamGroup exception", e);
            }
        }

        updateTeamSet(needUpdateTeamGroup, teamGroupDetail.teamGroupCollectionName(),
                teamGroupDetail.teamSetCollectionName());
        mongoTemplate.remove(new Query(Criteria.where("minReplayDate").lt(teamGroupDetail.start())), teamGroupDetail.teamSetCollectionName());
    }

    public Collection<Binary> queryNeedUpdateTeamGroup(List<TeamGroup> teamGroups, String teamSetCollectionName,
                                                       LocalDate minReplayDate) {
        List<Binary> teamIds = teamGroups.stream().map(teamGroup -> new Binary(teamGroup.id())).toList();
        List<TeamSet> teamSets = getTeamSets(teamIds, teamSetCollectionName);
        Map<Binary, TeamSet> teamSetMap = teamSets.stream().collect(Collectors.toMap(TeamSet::id,
                Function.identity()));

        List<Binary> needUpdateTeamGroup = new ArrayList<>();
        for (TeamGroup teamGroup : teamGroups) {
            Binary teamId = new Binary(teamGroup.id());
            TeamSet teamSet = teamSetMap.get(teamId);
            if (teamSet == null || teamSet.minReplayDate() == null) {
                needUpdateTeamGroup.add(new Binary(teamGroup.id()));
                continue;
            }

            if (teamSet.replayNum() < teamGroup.replayNum()
                    || teamSet.minReplayDate().isBefore(minReplayDate)) {
                needUpdateTeamGroup.add(new Binary(teamGroup.id()));
            }
        }
        return needUpdateTeamGroup;
    }

    public List<TeamSet> getTeamSets(List<Binary> teamIds, String teamSetCollectionName) {
        Query query = new Query(Criteria.where("_id").in(teamIds));
        return mongoTemplate.find(query, TeamSet.class, teamSetCollectionName);
    }

    public void updateTeamSet(List<Binary> teamIds, String teamGroupCollectionName, String insertCollectionName) {
        List<List<Binary>> partitionList = Lists.partition(teamIds, 100);
        for (List<Binary> partition : partitionList) {
            log.info("start update team set {}", partition.stream().map(binary -> new String(binary.getData()))
                    .toList());
            try {
                List<TeamSet> teamSets = new ArrayList<>();
                Query query = new Query(Criteria.where("_id").in(partition));
                List<TeamGroup> teamGroups = mongoTemplate.find(query, TeamGroup.class, teamGroupCollectionName);
                teamGroups.forEach(teamGroup -> teamSets.add(buildTeamSet(teamGroup)));
                mongoTemplate.remove(query, insertCollectionName);
                mongoTemplate.insert(teamSets, insertCollectionName);
            } catch (Exception e) {
                log.error("updateTeamSet exception", e);
            }
        }
    }

    public TeamSet buildTeamSet(TeamGroup teamGroup) {
        if (teamGroup.teams() == null || teamGroup.teams().isEmpty()) {
            return new TeamSet(new Binary(teamGroup.id()), teamGroup.tier(), 0, null, Collections.emptyList());
        }

        Map<String, Map<String, Integer>> moveMap = new HashMap<>();
        Map<String, Map<String, Integer>> itemsMap = new HashMap<>();
        Map<String, Map<String, Integer>> abilityMap = new HashMap<>();
        for (BattleTeam team : teamGroup.teams()) {
            countPokemonSet(team, moveMap, itemsMap, abilityMap);
        }

        List<PokemonBuildSet> pokemonBuildSets = new ArrayList<>();
        for (var entrySet : moveMap.entrySet()) {
            String pokemon = entrySet.getKey();
            pokemonBuildSets.add(new PokemonBuildSet(pokemon, descSortByValue(moveMap.get(pokemon)),
                    descSortByValue(abilityMap.get(pokemon)), descSortByValue(itemsMap.get(pokemon))));
        }

        LocalDate minReplayDate = teamGroup.teams().stream()
                        .map(BattleTeam::battleDate)
                        .filter(Objects::nonNull)
                        .min(LocalDate::compareTo)
                        .orElse(null);
        return new TeamSet(new Binary(teamGroup.id()), teamGroup.tier(), teamGroup.teams().size(), minReplayDate,
                pokemonBuildSets);
    }

    private static void countPokemonSet(BattleTeam team,
                                        Map<String, Map<String, Integer>> moveMap,
                                        Map<String, Map<String, Integer>> itemsMap,
                                        Map<String, Map<String, Integer>> abilityMap) {
        for (Pokemon pokemon : team.pokemons()) {
            if (!moveMap.containsKey(pokemon.getName())) {
                moveMap.put(pokemon.getName(), new HashMap<>());
                itemsMap.put(pokemon.getName(), new HashMap<>());
                abilityMap.put(pokemon.getName(), new HashMap<>());
            }

            if (pokemon.getItem() != null) {
                itemsMap.get(pokemon.getName()).merge(pokemon.getItem().trim(), 1, Integer::sum);
            }

            if (pokemon.getAbility() != null) {
                abilityMap.get(pokemon.getName()).merge(pokemon.getAbility().trim(), 1, Integer::sum);
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
}