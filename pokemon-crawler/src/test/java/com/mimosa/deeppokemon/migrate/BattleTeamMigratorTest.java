/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.migrate;

import com.google.common.base.Strings;
import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.matcher.PokemonMatcher;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
//@ContextConfiguration(classes = MongodbTestConfig.class)
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
            String teamId = new String(battleTeam.teamId());
            Assertions.assertEquals(24, teamId.length());
            assertNotEquals(Strings.repeat("0", 24), teamId);
            Assertions.assertNotEquals(0, battleTeam.teamId().length);
            Assertions.assertNotNull(battleTeam.battleDate());
            Assertions.assertNotNull(battleTeam.tier());
            Assertions.assertFalse(battleTeam.tagSet().isEmpty());
            List<Pokemon> pokemons = battleTeam.pokemons();
            Assertions.assertNotNull(pokemons);
            Assertions.assertTrue(pokemons.stream().allMatch(PokemonMatcher.POKEMON_MATCHER::matches));
        }
    }
}