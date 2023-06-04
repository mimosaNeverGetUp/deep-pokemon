/*
 * The MIT License
 *
 * Copyright (c) [2023]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Team;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("unit-test")
class RegexTeamCrawlerTest {

    @Autowired
    RegexTeamCrawler regexTeamCrawler;

    @Test
    void craw() {
        String url = "https://replay.pokemonshowdown.com/ou-248041959";
        Battle battle = regexTeamCrawler.craw(url);
        assertBattleNotNull(battle);
    }

    private void assertBattleNotNull(Battle battle) {
        assertNotNull(battle.getBattleID());
        assertNotNull(battle.getDate());
        assertNotNull(battle.getWinner());
        assertNotNull(battle.getTeams());
        assertTrue(battle.getTeams().length > 0);
        for (Team team : battle.getTeams()) {
            assertTeamNotNull(team);
            for (Pokemon pokemon : team.getPokemons()) {
                assertPokemonNotNull(pokemon);
            }
        }
    }

    private void assertTeamNotNull(Team team) {
        assertNotNull(team.getPlayerName());
        assertNotNull(team.getTier());
        assertNotNull(team.getPokemons());
        assertTrue(team.getPokemons().size() > 0);
    }

    private void assertPokemonNotNull(Pokemon pokemon) {
        assertNotNull(pokemon.getName());
    }
}