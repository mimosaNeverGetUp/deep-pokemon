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
import static org.junit.Assert.*;

import com.mimosa.deeppokemon.crawler.LadderBattleCrawler;
import com.mimosa.deeppokemon.crawler.RegexTeamCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Player;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.PlayerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LadderBattleCrawlerTest {
    //@Qualifier("crawler")
    @Autowired
    LadderBattleCrawler battleCrawler;

    @Autowired
    BattleService battleSevice;

    @Autowired
    PlayerService playerService;

    @Autowired
    RegexTeamCrawler regexTeamCrawler;
    private static Logger log = LoggerFactory.getLogger(LadderBattleCrawlerTest.class);

    @Test
    public void crawLadderBattle(){
        LadderBattleCrawler crawler = new LadderBattleCrawler();
        ArrayList<Player> players = crawler.crawLadeerName();
        assertNotNull(players);
        for (Player player : players) {
            System.out.println(player);
        }
    }

    @Test
    public void crawPlayerBattle() {
        String playerName = "CCLM";
        LinkedList<Battle> battles = battleCrawler.crawPlayerBattle(playerName);
        assertNotNull(battles);
        for (Battle battle : battles) {
            System.out.println(battle);
        }
    }

    @Test
    public void crawLadder() {
        assertNotNull(battleCrawler.getDateAfter());
        ArrayList<Player> players = battleCrawler.crawLadeerName();
        assertNotNull(players);
        for (Player player : players) {
            System.out.println("top 10:"+player);
        }
        LinkedList<Battle> battles = battleCrawler.crawLadderBattle();
        assertNotNull(battles);
        System.out.println(battles.size());
        for (Battle battle : battles) {
            System.out.println("battles"+battle);
        }
    }

    @Test
    public void battleDao() {
        Player mimosa = new Player(LocalDate.now(), "mimosa", 1800, 100, 81.0f, "gen6ou");
        playerService.save(mimosa);
        String url = "https://replay.pokemonshowdown.com/ou-248041959";
        Battle battle = regexTeamCrawler.craw(url);
        battleSevice.save(battle);
    }

    @Test
    public void craw() {
        List<Player> players = battleCrawler.crawLadeerName();
        List<Battle> battles = battleCrawler.crawLadderBattle();
//        playerService.saveAll(players);
//        battleSevice.savaAll(battles);
        System.out.println(battles.size());
        for (Battle battle : battles) {
            for (Team team:battle.getTeams()) {
                for (Pokemon pokemon : team.getPokemons()) {
                    System.out.println(pokemon.getName());
                }
                System.out.println(team.getTagSet());
            }
        }

    }

    @Test
    public void name() {
    }
}
