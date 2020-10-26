package com.mimosa.deeppokemon;
import static org.junit.Assert.*;

import com.mimosa.deeppokemon.crawler.LadderBattleCrawler;
import com.mimosa.deeppokemon.crawler.RegexTeamCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Player;
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
        log.info(String.format("craw start: format:%s pageLimit:%d rankLimit:%d eloLimit:%d gxeLimit:%f dateLimit:%tF",
                battleCrawler.getFormat(), battleCrawler.getPageLimit(), battleCrawler.getRankMoreThan(),
                battleCrawler.getMinElo(), battleCrawler.getMinGxe(), battleCrawler.getDateAfter()));
        List<Player> players = battleCrawler.crawLadeerName();
        playerService.saveAll(players);
        List<Battle> battles = battleCrawler.crawLadderBattle();
        battleSevice.savaAll(battles);
    }

    @Test
    public void name() {
    }
}
