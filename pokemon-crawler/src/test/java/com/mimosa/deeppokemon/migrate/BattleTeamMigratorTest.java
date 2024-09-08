/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.migrate;

import com.google.common.base.Strings;
import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.matcher.PokemonMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;

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
            Assertions.assertNotNull(battleTeam.getTeamId());
            Assertions.assertNotNull(battleTeam.getFeatureIds());
            Assertions.assertEquals(6, battleTeam.getFeatureIds().size());
            String teamId = new String(battleTeam.getTeamId());
            Assertions.assertEquals(24, teamId.length());
            assertNotEquals(Strings.repeat("0", 24), teamId);
            Assertions.assertNotEquals(0, battleTeam.getTeamId().length);
            Assertions.assertNotNull(battleTeam.getBattleDate());
            Assertions.assertNotNull(battleTeam.getTier());
            Assertions.assertFalse(battleTeam.getTagSet().isEmpty());
            List<Pokemon> pokemons = battleTeam.getPokemons();
            Assertions.assertNotNull(pokemons);
            Assertions.assertTrue(pokemons.stream().allMatch(PokemonMatcher.POKEMON_MATCHER::matches));
        }
    }
}