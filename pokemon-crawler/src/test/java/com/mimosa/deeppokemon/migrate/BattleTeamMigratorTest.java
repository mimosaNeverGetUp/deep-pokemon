/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.migrate;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.matcher.PokemonMatcher;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class BattleTeamMigratorTest {
    @Autowired
    private BattleTeamMigrator battleTeamMigrator;

    @Autowired
    private MongoTemplate mongoTemplate;

//    @Test
    void migrateBattleTeam() {
        battleTeamMigrator.migrateBattleTeam();
        List<BattleTeam> battleTeams = mongoTemplate.find(new Query().limit(10), BattleTeam.class);
        for (BattleTeam battleTeam : battleTeams) {
            Assertions.assertNotNull(battleTeam.teamId());
            Assertions.assertNotEquals(0, battleTeam.teamId().length);
            Assertions.assertNotNull(battleTeam.battleDate());
            Assertions.assertFalse(battleTeam.tagSet().isEmpty());
            List<Pokemon> pokemons = battleTeam.pokemons();
            Assertions.assertNotNull(pokemons);
            Assertions.assertTrue(pokemons.stream().allMatch(PokemonMatcher.POKEMON_MATCHER::matches));
        }

        List<Battle> battles = mongoTemplate.find(new Query().limit(10), Battle.class);
        assertTrue(battles.stream().allMatch(battle -> battle.getWinner() != null && battle.getTeams() != null
                && battle.getPlayers() != null && battle.getDate() != null));
    }
}