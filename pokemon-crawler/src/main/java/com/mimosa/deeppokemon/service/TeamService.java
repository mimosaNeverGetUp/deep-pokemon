/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.google.common.collect.Lists;
import com.mimosa.deeppokemon.entity.*;
import com.mimosa.deeppokemon.entity.tour.TourTeam;
import com.mimosa.deeppokemon.tagger.TeamTagger;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TeamService {
    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    protected static final int BATCH_SIZE = 1000;
    protected static final String REPLAY_NUM = "replayNum";
    protected static final String ID = "_id";
    protected static final String UNIQUE_PLAYER_NUM = "uniquePlayerNum";
    protected static final String MAX_RATING = "maxRating";
    protected static final String LATEST_BATTLE_DATE = "latestBattleDate";
    protected static final String TAG_SET = "tagSet";
    private final MongoTemplate mongoTemplate;
    private final TeamTagger teamTagger;

    public TeamService(MongoTemplate mongoTemplate, TeamTagger teamTagger) {
        this.mongoTemplate = mongoTemplate;
        this.teamTagger = teamTagger;
    }

    @RegisterReflectionForBinding({TeamGroup.class, BattleTeam.class, TourTeam.class, TeamSet.class,
            PokemonBuildSet.class})
    public void updateTeamSet(TeamGroupDetail teamGroupDetail) {
        List<Binary> needUpdateTeamGroup = new ArrayList<>();

        Query query = new Query()
                .with(Sort.by(Sort.Order.desc(LATEST_BATTLE_DATE)))
                .cursorBatchSize(BATCH_SIZE);
        query.fields().include(ID, REPLAY_NUM, UNIQUE_PLAYER_NUM, MAX_RATING);
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
        syncTeamSetAndTeamGroup(teamGroupDetail.teamSetCollectionName(), teamGroupDetail.teamGroupCollectionName());
    }

    private void syncTeamSetAndTeamGroup(String teamSetCollectionName, String teamGroupCollectionName) {
        Query query = new Query()
                .with(Sort.by(Sort.Order.desc(LATEST_BATTLE_DATE)))
                .cursorBatchSize(BATCH_SIZE);
        query.fields().include(ID, TAG_SET, REPLAY_NUM, UNIQUE_PLAYER_NUM, MAX_RATING);
        Stream<TeamGroup> teamGroupStream = mongoTemplate.stream(query, TeamGroup.class, teamGroupCollectionName);

        List<TeamGroup> batchTeamGroup = new ArrayList<>();
        teamGroupStream.forEach(teamGroup -> {
            batchTeamGroup.add(teamGroup);
            if (batchTeamGroup.size() >= BATCH_SIZE) {
                try {
                    syncTeamSetAndTeamGroup(batchTeamGroup, teamSetCollectionName, teamGroupCollectionName);
                } catch (Exception e) {
                    log.error("sync team group fail", e);
                } finally {
                    batchTeamGroup.clear();
                }
            }
        });

        if (!batchTeamGroup.isEmpty()) {
            syncTeamSetAndTeamGroup(batchTeamGroup, teamSetCollectionName, teamGroupCollectionName);
        }
    }

    private void syncTeamSetAndTeamGroup(List<TeamGroup> batchTeamGroup, String teamSetCollectionName,
                                         String teamGroupCollectionName) {
        Query query = new Query(Criteria.where(ID).in(batchTeamGroup.stream().map(TeamGroup::id).toList()));
        query.fields().include(ID, TAG_SET, REPLAY_NUM);
        List<TeamSet> teamSets = mongoTemplate.find(query, TeamSet.class, teamSetCollectionName);
        Map<Binary, TeamSet> teamSetMap = teamSets.stream().collect(Collectors.toMap(TeamSet::id, Function.identity()));
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, teamGroupCollectionName);
        boolean needUpdate = false;
        for (TeamGroup teamGroup : batchTeamGroup) {
            Binary teamId = new Binary(teamGroup.id());
            if (teamSetMap.containsKey(teamId)) {
                TeamSet teamSet = teamSetMap.get(teamId);
                if (!isTagSync(teamGroup, teamSet)) {
                    log.debug("start to update team group {} tag", new String(teamId.getData()));
                    needUpdate = true;
                    updateTeamGroupTag(bulkOperations, teamId, teamSet.tagSet());
                }
            }
        }
        if (needUpdate) {
            bulkOperations.execute();
        }
    }

    private void updateTeamGroupTag(BulkOperations bulkOperations, Binary teamId, Set<Tag> tags) {
        Query query = new Query(Criteria.where(ID).is(teamId));
        Update update = new Update().set(TAG_SET, tags);
        bulkOperations.updateOne(query, update);
    }

    private boolean isTagSync(TeamGroup teamGroup, TeamSet teamSet) {
        for (Tag tag : teamSet.tagSet()) {
            if (!teamGroup.tagSet().contains(tag)) {
                return false;
            }
        }
        return true;
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
            if (teamSet == null || teamSet.minReplayDate() == null || teamSet.tagSet() == null || teamSet.tagSet().isEmpty()) {
                needUpdateTeamGroup.add(new Binary(teamGroup.id()));
                continue;
            }

            if (teamSet.replayNum() < teamGroup.replayNum()
                    || (minReplayDate != null && teamSet.minReplayDate().isBefore(minReplayDate))) {
                needUpdateTeamGroup.add(new Binary(teamGroup.id()));
            }
        }
        return needUpdateTeamGroup;
    }

    public List<TeamSet> getTeamSets(List<Binary> teamIds, String teamSetCollectionName) {
        Query query = new Query(Criteria.where(ID).in(teamIds));
        return mongoTemplate.find(query, TeamSet.class, teamSetCollectionName);
    }

    public void updateTeamSet(List<Binary> teamIds, String teamGroupCollectionName, String insertCollectionName) {
        List<List<Binary>> partitionList = Lists.partition(teamIds, 100);
        for (List<Binary> partition : partitionList) {
            log.info("start update team set {}", partition.stream().map(binary -> new String(binary.getData()))
                    .toList());
            try {
                List<TeamSet> teamSets = new ArrayList<>();
                Query query = new Query(Criteria.where(ID).in(partition));
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
            return new TeamSet(new Binary(teamGroup.id()), teamGroup.tier(), 0, null,
                    Collections.emptySet(), Collections.emptyList());
        }

        Map<String, Map<String, Integer>> moveMap = new HashMap<>();
        Map<String, Map<String, Integer>> itemsMap = new HashMap<>();
        Map<String, Map<String, Integer>> abilityMap = new HashMap<>();
        Map<String, Map<String, Integer>> teraTypes = new HashMap<>();
        for (BattleTeam team : teamGroup.teams()) {
            countPokemonSet(team, moveMap, itemsMap, abilityMap, teraTypes);
        }

        List<PokemonBuildSet> pokemonBuildSets = new ArrayList<>();
        for (var entrySet : moveMap.entrySet()) {
            String pokemon = entrySet.getKey();
            pokemonBuildSets.add(new PokemonBuildSet(pokemon, descSortByValue(moveMap.get(pokemon)),
                    descSortByValue(abilityMap.get(pokemon)), descSortByValue(itemsMap.get(pokemon)),
                    descSortByValue(teraTypes.get(pokemon))));
        }

        LocalDateTime minReplayDate = teamGroup.teams().stream()
                .map(BattleTeam::getBattleDate)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        TeamSet teamSet = new TeamSet(new Binary(teamGroup.id()), teamGroup.tier(), teamGroup.teams().size(),
                minReplayDate == null ? null : minReplayDate.toLocalDate(), null, pokemonBuildSets);
        return tagTeamSet(teamSet);
    }

    private TeamSet tagTeamSet(TeamSet teamSet) {
        BattleTeam team = convertTeam(teamSet);
        teamTagger.tagTeam(team, teamSet);
        return teamSet.withTags(team.getTagSet());
    }

    public Set<Tag> tagTeam(String teamId, String collectionName) {
        TeamSet teamSet = mongoTemplate.findById(new Binary(Base64.getDecoder().decode(teamId)), TeamSet.class,
                collectionName);
        return tagTeamSet(teamSet).tagSet();
    }

    private BattleTeam convertTeam(TeamSet teamSet) {
        BattleTeam team = new BattleTeam();
        List<Pokemon> pokemons = new ArrayList<>();
        for (var pokemonSet : teamSet.pokemons()) {
            pokemons.add(new Pokemon(pokemonSet.name()));
        }
        team.setPokemons(pokemons);
        team.setTagSet(new HashSet<>());
        return team;
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
}