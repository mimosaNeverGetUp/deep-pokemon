/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat;

import com.mimosa.deeppokemon.entity.stat.MonthlyPokemonStat;
import com.mimosa.deeppokemon.service.StatsService;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;

@Component
public class MonthlyStatCrawler {
    private final StatsService statsService;

    public MonthlyStatCrawler(StatsService statsService) {
        this.statsService = statsService;
    }

    public List<MonthlyPokemonStat> craw(String format, YearMonth date) {

    }
}