package com.mimosa.deeppokemon.config;

import com.mimosa.deeppokemon.crawler.LadderBattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Player;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component  // @Compoent 注解将 MyCommandLineRunner 注册为Spring容器中的一个 Bean。
@Order(100) //@Order注解，表示这个启动任务的执行优先级，在一个项目中，启动任务可能有多个，所以需要有一个排序。@Order 注解中，数字越小，优先级越大，默认情况下，优先级的值为 Integer.MAX_VALUE，表示优先级最低。
public class MyCommandLineRunner implements CommandLineRunner {
    @Autowired
    LadderBattleCrawler battleCrawler;

    @Autowired
    BattleService battleSevice;

    @Autowired
    PlayerService playerService;

    private static Logger log = LoggerFactory.getLogger(ScheduledConfig.class);
    @Override
    public void run(String... args) throws Exception {
        log.info(String.format("craw start: format:%s pageLimit:%d rankLimit:%d eloLimit:%d gxeLimit:%f dateLimit:%tF",
                battleCrawler.getFormat(), battleCrawler.getPageLimit(), battleCrawler.getRankMoreThan(),
                battleCrawler.getMinElo(), battleCrawler.getMinGxe(), battleCrawler.getDateAfter()));
        List<Player> players = battleCrawler.crawLadeerName();
        playerService.saveAll(players);
        List<Battle> battles = battleCrawler.crawLadderBattle();
        battleSevice.savaAll(battles);
    }
}
