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

package com.mimosa.deeppokemon;

import com.mimosa.deeppokemon.crawler.LadderCrawler;
import com.mimosa.deeppokemon.entity.*;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.LadderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@ContextConfiguration(classes = LadderCrawlerTest.TestConfig.class)
public class LadderCrawlerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean(name = "testLadderCrawler")
        public LadderCrawler ladderCrawler() {

            return new LadderCrawler("gen9ou", 1,
                    3, 1600, LocalDate.now().minusMonths(1), 60.0f);
        }
    }

    @Autowired
    @Qualifier("testLadderCrawler")
    LadderCrawler ladderCrawler;

    @MockBean
    BattleService battleSevice;

    @MockBean
    LadderService ladderService;

    @Test
    public void crawLadderRank() throws IOException {
        Ladder ladder = ladderCrawler.crawLadderRank();
        assertNotNull(ladder);
        assertNotNull(ladder.getLadderRankList());

        for (LadderRank ladderRank : ladder.getLadderRankList()) {
            assertNotNull(ladderRank.getName());
            assertNotEquals(0, ladderRank.getRank());
            assertNotEquals(0, ladderRank.getElo());
            assertNotEquals(0, ladderRank.getGxe());
        }
    }


    @Test
    public void crawLadderBattle() throws IOException {
        assertNotNull(ladderCrawler.getDateAfter());
        List<Battle> battles = ladderCrawler.crawLadder();
        assertNotNull(battles);
        for (Battle battle : battles) {
            assertBattleNotNull(battle);
        }
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