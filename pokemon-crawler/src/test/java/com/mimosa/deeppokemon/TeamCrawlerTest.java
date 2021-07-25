package com.mimosa.deeppokemon;
import static org.junit.Assert.*;

import com.mimosa.deeppokemon.crawler.LadderBattleCrawler;
import com.mimosa.deeppokemon.crawler.RegexTeamCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.deeppokemon.service.BattleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TeamCrawlerTest {
    @Autowired
    private RegexTeamCrawler crawler;

    @Autowired
    BattleService battleSevice;

    @Test
    public void Crawler(){
        String url = "https://replay.pokemonshowdown.com/gen8ou-1332551230";
        Battle battle = crawler.craw(url);
        Team[] teams = battle.getTeams();
        assertNotNull(teams);
    }
}
