package com.mimosa.deeppokemon;

import com.mimosa.deeppokemon.config.ScheduledConfig;
import com.mimosa.deeppokemon.crawler.LadderBattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Player;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class PokemoCrawlerApplication {
    private static Logger log = LoggerFactory.getLogger(ScheduledConfig.class);
    public static void main(String[] args) {
        SpringApplication.run(PokemoCrawlerApplication.class, args);

    }
    @Bean
    LadderBattleCrawler crawler(){
        return new LadderBattleCrawler("gen8ou", 1,
                50, 1600, LocalDate.now().minusMonths(1), 60.0f);
    }
}
