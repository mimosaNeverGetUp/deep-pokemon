/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.matcher.BattleStatMatcher;
import com.mimosa.deeppokemon.matcher.PokemonMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;

import java.util.BitSet;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class BattleServiceTest {
    public static final String EXIST_BATTLE_ID = "gen9ou-1874088419";
    public static final String NOT_EXIST_BATTLE_ID = "test-12345";
    private static final String NOT_SAVE_BATTLE_ID = "smogtours-gen9ou-746547";
    private static final String NOT_LOG_BATTLE_ID = "gen9ou-2053586253";
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
        List<Battle> battles = battleService.find100BattleSortByDate().subList(0, 5);
        battles.forEach(battle -> battle.setAvageRating(1800.0F));
        List<BattleTeam> battleTeams = null;
        try {
            battleService.insertTeam(battles);
            battleTeams = mongoTemplate.findAll(BattleTeam.class);
            for (BattleTeam battleTeam : battleTeams) {
                Assertions.assertNotNull(battleTeam.teamId());
                Assertions.assertNotEquals(0, battleTeam.teamId().length);
                Assertions.assertNotEquals(0.0F, battleTeam.rating());
                Assertions.assertNotNull(battleTeam.battleDate());
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
        notExistBattle.setBattleID(NOT_EXIST_BATTLE_ID);
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
                new Pokemon("Zamazenta-*"),new Pokemon("Landorus-Therian"),new Pokemon("Slowking-Galar")));
        byte[] bytes = battleService.calTeamId(team);
        BitSet bitSet = BitSet.valueOf(bytes);
        Assertions.assertTrue(bitSet.get(1017));
        Assertions.assertTrue(bitSet.get(889));
        Assertions.assertTrue(bitSet.get(983));
        Assertions.assertTrue(bitSet.get(983));
        Assertions.assertTrue(bitSet.get(645));
        Assertions.assertTrue(bitSet.get(199));
        Assertions.assertFalse(bitSet.get(123));
    }
}