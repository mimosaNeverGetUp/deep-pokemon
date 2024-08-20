/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.*;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.matcher.BattleStatMatcher;
import com.mimosa.deeppokemon.matcher.PokemonMatcher;
import org.bson.types.Binary;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class BattleServiceTest {
    public static final String EXIST_BATTLE_ID = "gen9ou-2171069120";
    public static final String NOT_EXIST_BATTLE_ID = "test-12345";
    private static final String NOT_SAVE_BATTLE_ID = "smogtours-gen9ou-746547";
    private static final String NOT_LOG_BATTLE_ID = "gen9ou-2171080820";
    protected static final String TEAM_GROUP_LAST_99_Y = "team_group_last_99_y";
    protected static final String TEAM_SET_LAST_99_Y = "team_set_last_99_y";

    @Autowired
    private BattleService battleService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void insert() {
        List<Battle> battles = battleService.find100BattleSortByDate();
        Battle existBattle = battles.get(0);

        Battle notExistBattle = battles.get(1);
        notExistBattle.setBattleID(NOT_EXIST_BATTLE_ID);
        try {
            List<Battle> insertBattle = battleService.insert(List.of(existBattle, notExistBattle));
            Assertions.assertEquals(1, insertBattle.size());
            Assertions.assertEquals(NOT_EXIST_BATTLE_ID, insertBattle.get(0).getBattleID());
        } finally {
            mongoTemplate.remove(notExistBattle);
        }
    }


    @Test
    void insertTeam() {
        Battle battle = mongoTemplate.findOne(new Query(), Battle.class);
        BattleTeam teamSample = mongoTemplate.findOne(new Query(), BattleTeam.class);
        Team team = new Team(teamSample.pokemons());
        team.setTagSet(teamSample.tagSet());
        team.setTier(teamSample.tier());
        team.setPlayerName(teamSample.playerName());
        battle.setBattleID(NOT_EXIST_BATTLE_ID);
        battle.setAvageRating(1800.0F);
        battle.setTeams(new Team[]{team, team});
        List<BattleTeam> battleTeams = null;
        try {
            battleService.insertTeam(Collections.singletonList(battle));
            Query query = new Query();
            query.addCriteria(Criteria.where("battleId").is(NOT_EXIST_BATTLE_ID));
            battleTeams = mongoTemplate.find(query, BattleTeam.class);
            Assertions.assertEquals(2, battleTeams.size());
            for (BattleTeam battleTeam : battleTeams) {
                Assertions.assertNotNull(battleTeam.teamId());
                Assertions.assertNotEquals(0, battleTeam.teamId().length);
                Assertions.assertNotEquals(0.0F, battleTeam.rating());
                Assertions.assertNotNull(battleTeam.battleDate());
                Assertions.assertNotNull(battleTeam.tier());
                Assertions.assertFalse(battleTeam.tagSet().isEmpty());
                List<Pokemon> pokemons = battleTeam.pokemons();
                Assertions.assertNotNull(pokemons);
                Assertions.assertTrue(pokemons.stream().allMatch(PokemonMatcher.POKEMON_MATCHER::matches));
            }
        } finally {
            if (battleTeams != null) {
                for (BattleTeam battleTeam : battleTeams) {
                    mongoTemplate.remove(battleTeam);
                }
            }
        }
    }

    @Test
    void getBattleStat_NotExistBattle() {
        BattleStat battleStat = null;
        try {
            battleStat = battleService.getBattleStat(NOT_SAVE_BATTLE_ID);
            MatcherAssert.assertThat(battleStat, BattleStatMatcher.BATTLE_STAT_MATCHER);
        } finally {
            if (battleStat != null) {
                mongoTemplate.remove(battleStat);
                mongoTemplate.remove(new Query(Criteria.where("_id").is(NOT_SAVE_BATTLE_ID)), "battle");
                mongoTemplate.remove(new Query(Criteria.where("battleId").is(NOT_SAVE_BATTLE_ID)), "battle_team");
            }
        }
    }

    @Test
    void getBattleStat_ExistBattle_NoBattleLog() {
        BattleStat battleStat = null;
        try {
            battleStat = battleService.getBattleStat(NOT_LOG_BATTLE_ID);
            MatcherAssert.assertThat(battleStat, BattleStatMatcher.BATTLE_STAT_MATCHER);
            Assertions.assertNotNull(mongoTemplate.findById(NOT_LOG_BATTLE_ID, Battle.class).getLog());
        } finally {
            if (battleStat != null) {
                mongoTemplate.remove(battleStat);
            }
            mongoTemplate.remove(new Query(Criteria.where("battleId").is(NOT_LOG_BATTLE_ID)), "battle_team");
        }
    }

    @Test
    void getAllBattleIds() {
        Assertions.assertTrue(battleService.getAllBattleIds().contains(EXIST_BATTLE_ID));

        Battle notExistBattle = battleService.findBattle(EXIST_BATTLE_ID);
        BattleTeam teamSample = mongoTemplate.findOne(new Query(), BattleTeam.class);
        Team team = new Team(teamSample.pokemons());
        team.setTagSet(teamSample.tagSet());
        team.setTier(teamSample.tier());
        team.setPlayerName(teamSample.playerName());
        notExistBattle.setBattleID(NOT_EXIST_BATTLE_ID);
        notExistBattle.setTeams(new Team[]{team, team});
        try {
            battleService.save(List.of(notExistBattle), false);
            Assertions.assertTrue(battleService.getAllBattleIds().contains(NOT_EXIST_BATTLE_ID));
        } finally {
            mongoTemplate.remove(notExistBattle);
            mongoTemplate.remove(new Query(Criteria.where("battleId").is(NOT_EXIST_BATTLE_ID)), "battle_team");
        }
    }

    @Test
    void calTeamId() {
        Team team = new Team();
        team.setPokemons(List.of(new Pokemon("Ogerpon-Wellspring"), new Pokemon("Kingambit"), new Pokemon("Great Tusk"),
                new Pokemon("Zamazenta-*"), new Pokemon("Landorus-Therian"), new Pokemon("Slowking-Galar")));
        byte[] bytes = battleService.calTeamId(team.getPokemons());

        String teamId = new String(bytes);
        Assertions.assertEquals("019906450889098309841017", teamId);
    }

    @Test
    void updateTeam() {
        battleService.updateTeam(new TeamGroupDetail(LocalDate.now().minusYears(99), LocalDate.now(),
                TEAM_GROUP_LAST_99_Y, TEAM_SET_LAST_99_Y));
        TeamGroup teamGroup = mongoTemplate.findOne(new Query(), TeamGroup.class, TEAM_GROUP_LAST_99_Y);
        Assertions.assertNotNull(teamGroup);
        Assertions.assertNotNull(teamGroup.pokemons());
        Assertions.assertNotNull(teamGroup.tagSet());
        Assertions.assertNotNull(teamGroup.teams());
        Assertions.assertNotNull(teamGroup.tier());
        Assertions.assertNotNull(teamGroup.latestBattleDate());
        Assertions.assertNotNull(teamGroup.id());
        Assertions.assertNotNull(teamGroup.id());
        Assertions.assertFalse(teamGroup.teams().isEmpty());
        Assertions.assertNotEquals(0, teamGroup.replayNum());
        Assertions.assertNotEquals(0, teamGroup.uniquePlayerNum());

        Assertions.assertFalse(teamGroup.pokemons().isEmpty());
        Assertions.assertFalse(teamGroup.tagSet().isEmpty());

        Assertions.assertEquals(mongoTemplate.count(new Query(), TeamGroup.class, TEAM_GROUP_LAST_99_Y), mongoTemplate.count(new Query(),
                TeamSet.class, TEAM_SET_LAST_99_Y));
        TeamSet teamSet = mongoTemplate.findById(new Binary(teamGroup.id()), TeamSet.class, TEAM_SET_LAST_99_Y);
        Assertions.assertNotNull(teamSet);
        Assertions.assertNotNull(teamSet.pokemons());
        Assertions.assertNotNull(teamSet.tier());
        Assertions.assertNotNull(teamSet.minReplayDate());
        Assertions.assertNotEquals(0, teamSet.replayNum());
        Assertions.assertFalse(teamSet.pokemons().isEmpty());
        boolean foundMove = false;
        for (PokemonBuildSet pokemonBuildSet : teamSet.pokemons()) {
            Assertions.assertNotNull(pokemonBuildSet.name());
            if (!pokemonBuildSet.moves().isEmpty()) {
                foundMove = true;
            }
        }
        Assertions.assertTrue(foundMove);
        Set<Tag> tagSet = teamSet.tagSet();
        Assertions.assertNotNull(tagSet);
        Assertions.assertFalse(tagSet.isEmpty());
        for (var tag : tagSet) {
            Assertions.assertTrue(teamGroup.tagSet().contains(tag));
        }
    }
}