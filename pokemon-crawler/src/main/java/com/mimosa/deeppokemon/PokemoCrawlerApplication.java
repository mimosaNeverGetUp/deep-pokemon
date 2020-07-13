package com.mimosa.deeppokemon;

import com.mimosa.deeppokemon.crawler.LadderBattleCrawler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class PokemoCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PokemoCrawlerApplication.class, args);
    }
    @Bean
    LadderBattleCrawler crawler(){
        return new LadderBattleCrawler("gen8ou", 1,
                50, 1600, LocalDate.now().minusMonths(1), 60.0f);
    }
}
