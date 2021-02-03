package com.mimosa.deeppokemon.config;


import com.mimosa.deeppokemon.crawler.LadderBattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Player;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@Configuration
@EnableScheduling
public class ScheduledConfig {

    @Autowired
    LadderBattleCrawler battleCrawler;

    @Autowired
    BattleService battleSevice;

    @Autowired
    PlayerService playerService;

    private static Logger log = LoggerFactory.getLogger(ScheduledConfig.class);

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 1 * * ?")
    private void test() {
        List<Player> players = battleCrawler.crawLadeerName();
        List<Battle> battles = battleCrawler.crawLadderBattle();
        playerService.saveAll(players);
        battleSevice.savaAll(battles);
    }
}
