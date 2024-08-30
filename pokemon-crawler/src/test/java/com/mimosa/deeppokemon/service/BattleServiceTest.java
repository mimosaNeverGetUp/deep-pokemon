/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.crawler.BattleReplayExtractor;
import com.mimosa.deeppokemon.entity.*;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.tour.TourTeam;
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
    protected static final String TEAM_GROUP_TOUR_WCOP_2024 = "team_group_tour_wcop_2024";
    protected static final String TEAM_SET_TOUR_WCOP_2024 = "team_set_tour_wcop_2024";

    @Autowired
    private BattleService battleService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BattleReplayExtractor battleReplayExtractor;

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
        BattleTeam team1 = new BattleTeam();
        team1.setPokemons(teamSample.getPokemons());
        team1.setTagSet(teamSample.getTagSet());
        team1.setTier(teamSample.getTier());
        team1.setRating(1900F);
        team1.setPlayerName(teamSample.getPlayerName());

        BattleTeam team2 = new BattleTeam();
        team2.setPokemons(teamSample.getPokemons());
        team2.setTagSet(teamSample.getTagSet());
        team2.setTier(teamSample.getTier());
        team2.setRating(1900F);
        team2.setPlayerName(teamSample.getPlayerName());
        battle.setBattleID(NOT_EXIST_BATTLE_ID);
        battle.setAvageRating(1800.0F);
        battle.setBattleTeams(List.of(team1, team2));
        List<BattleTeam> battleTeams = null;
        try {
            battleService.insertTeam(Collections.singletonList(battle));
            Query query = new Query();
            query.addCriteria(Criteria.where("battleId").is(NOT_EXIST_BATTLE_ID));
            battleTeams = mongoTemplate.find(query, BattleTeam.class);
            Assertions.assertEquals(2, battleTeams.size());
            for (BattleTeam battleTeam : battleTeams) {
                Assertions.assertNotNull(battleTeam.getTeamId());
                Assertions.assertNotEquals(0, battleTeam.getTeamId().length);
                Assertions.assertEquals(1900F, battleTeam.getRating());
                Assertions.assertNotNull(battleTeam.getBattleDate());
                Assertions.assertNotNull(battleTeam.getTier());
                Assertions.assertFalse(battleTeam.getTagSet().isEmpty());
                List<Pokemon> pokemons = battleTeam.getPokemons();
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
        BattleTeam team = new BattleTeam();
        team.setPokemons(teamSample.getPokemons());
        team.setTagSet(teamSample.getTagSet());
        team.setTier(teamSample.getTier());
        team.setPlayerName(teamSample.getPlayerName());
        notExistBattle.setBattleID(NOT_EXIST_BATTLE_ID);
        notExistBattle.setBattleTeams(List.of(team, team));
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
        byte[] bytes = battleService.calTeamId(List.of(new Pokemon("Ogerpon-Wellspring"), new Pokemon("Kingambit"), new Pokemon("Great Tusk"),
                new Pokemon("Zamazenta-*"), new Pokemon("Landorus-Therian"), new Pokemon("Slowking-Galar")));

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
        Assertions.assertNotNull(teamGroup.maxRating());

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

    @Test
    void updateTourTeam() {
        battleService.updateTeam(new TourTeamGroupDetail(TEAM_GROUP_TOUR_WCOP_2024,
                TEAM_SET_TOUR_WCOP_2024, "The World Cup of Pokémon 2024", "gen9ou"));
        TeamGroup teamGroup = mongoTemplate.findOne(new Query(), TeamGroup.class, TEAM_GROUP_TOUR_WCOP_2024);
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
        Assertions.assertNotNull(teamGroup.maxPlayerWinDif());
        Assertions.assertNotNull(teamGroup.maxPlayerWinRate());
        Assertions.assertNotEquals(0, teamGroup.maxPlayerWinDif());
        Assertions.assertNotEquals(0F, teamGroup.maxPlayerWinRate());

        Assertions.assertFalse(teamGroup.pokemons().isEmpty());
        Assertions.assertFalse(teamGroup.tagSet().isEmpty());

        Assertions.assertEquals(mongoTemplate.count(new Query(), TeamGroup.class, TEAM_GROUP_TOUR_WCOP_2024), mongoTemplate.count(new Query(),
                TeamSet.class, TEAM_SET_TOUR_WCOP_2024));
        TeamSet teamSet = mongoTemplate.findById(new Binary(teamGroup.id()), TeamSet.class, TEAM_SET_TOUR_WCOP_2024);
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

        for (BattleTeam battleTeam : teamGroup.teams()) {
            Assertions.assertInstanceOf(TourTeam.class, battleTeam);
            TourTeam tourTeam = (TourTeam) battleTeam;
            Assertions.assertNotNull(tourTeam.getStage());
            Assertions.assertNotNull(tourTeam.getTourId());
            Assertions.assertNotNull(tourTeam.getPlayer());
            Assertions.assertNotNull(tourTeam.getPlayer().getName());
        }
    }
}