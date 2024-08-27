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

package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Tag;
import com.mimosa.deeppokemon.entity.Team;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class TeamAttackDefenceTagProviderTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TeamAttackDefenceTagProvider teamAttackDefenceTagProvider;

    @Test
    void tag() {
        List<BattleTeam> battleTeams = mongoTemplate.find(new Query().limit(20), BattleTeam.class);
        for (BattleTeam battleTeam : battleTeams) {
            Team team = new Team(battleTeam.getPokemons());
            team.setPlayerName(battleTeam.getPlayerName());
            team.setTier(battleTeam.getTier());
            teamAttackDefenceTagProvider.tag(team, null);
            Assertions.assertFalse(team.getTagSet() == null || team.getTagSet().isEmpty());

            System.out.println("tag: " + team.getTagSet());
            for (Pokemon pokemon : team.getPokemons()) {
                System.out.println(pokemon.getName());
            }
        }
    }

    @Test
    void tag_specify() {
        assertTag(Tag.BALANCE, "Hydrapple", "Chansey", "Moltres", "Great Tusk", "Enamorus", "Toxapex");
        assertTag(Tag.STAFF, "Dondozo", "Blissey", "Clodsire", "Gliscor", "Corviknight", "Toxapex");
        assertTag(Tag.BALANCE, "Gliscor", "Garganacl", "Weezing-Galar", "Kingambit", "Dragapult", "Alomomola");
        assertTag(Tag.ATTACK, "Kyurem", "Iron Valiant", "Iron Moth", "Kingambit", "Samurott-Hisui", "Landorus-Therian");
        assertTag(Tag.BALANCE_ATTACK, "Cinderace", "Kingambit", "Landorus-Therian", "Kyurem", "Slowking-Galar", "Dragapult");
    }

    public void assertTag(Tag tag, String... pokemons) {
        Team team = build(pokemons);
        teamAttackDefenceTagProvider.tag(team, null);
        Assertions.assertTrue(team.getTagSet().contains(tag));
    }

    public Team build(String... names) {
        Team team = new Team();
        List<Pokemon> pokemons = new ArrayList<>();
        for (String name : names) {
            Pokemon pokemon = new Pokemon();
            pokemon.setName(name);
            pokemons.add(pokemon);
        }
        team.setPokemons(pokemons);
        team.setTagSet(new HashSet<>());
        return team;
    }
}