/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class WeatherEventAnalyzerTest {
    @Autowired
    private WeatherEventAnalyzer weatherEventAnalyzer;

    @Test
    void analyze() {
        String rainDance = "RainDance";
        String pelipper = "Pelipper";
        BattleEvent weatherStartEvent = new BattleEvent("weather", List.of(rainDance, "[from] ability: Drizzle"
                , "[of] p1a: " + pelipper), null, null);
        BattleEvent weatherKeepEvent = new BattleEvent("weather", List.of(rainDance, "upkeep"), null, null);
        BattleEvent weatherEndEvent = new BattleEvent("weather", List.of("none"), null, null);

        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(1, pelipper, pelipper)
                .build();
        Assertions.assertTrue(weatherEventAnalyzer.supportAnalyze(weatherStartEvent));
        weatherEventAnalyzer.analyze(weatherStartEvent, null, battleStatus);
        Assertions.assertNotNull(battleStatus.getWeather());
        Assertions.assertEquals(rainDance, battleStatus.getWeather().name());
        Assertions.assertEquals(1, battleStatus.getWeather().ofTarget().playerNumber());
        Assertions.assertEquals(pelipper, battleStatus.getWeather().ofTarget().targetName());

        weatherEventAnalyzer.analyze(weatherKeepEvent, null, battleStatus);
        Assertions.assertNotNull(battleStatus.getWeather());
        Assertions.assertEquals(rainDance, battleStatus.getWeather().name());
        Assertions.assertEquals(1, battleStatus.getWeather().ofTarget().playerNumber());
        Assertions.assertEquals(pelipper, battleStatus.getWeather().ofTarget().targetName());
        weatherEventAnalyzer.analyze(weatherEndEvent, null, battleStatus);
        Assertions.assertNull(battleStatus.getWeather());
    }
}