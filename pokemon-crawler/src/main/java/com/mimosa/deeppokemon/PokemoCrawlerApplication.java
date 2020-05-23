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
                30, 1800, LocalDate.now().minusMonths(1), 80.0f);
    }
}
