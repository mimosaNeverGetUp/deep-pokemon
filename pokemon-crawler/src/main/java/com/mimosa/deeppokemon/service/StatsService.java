/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.crawler.stat.MonthlyStatCrawler;
import com.mimosa.deeppokemon.crawler.stat.dto.MonthlyBattleStatDto;
import com.mimosa.deeppokemon.crawler.stat.dto.MonthlyPokemonStatDto;
import com.mimosa.deeppokemon.entity.stat.monthly.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {
    private static final Logger log = LoggerFactory.getLogger(StatsService.class);
    protected static final int COUNTER_KOED_INDEX = 1;
    protected static final int COUNTER_SWITCH_OUT_INDEX = 2;
    protected static final String GEN_9_OU = "gen9ou";
    private final MongoTemplate mongoTemplate;
    private final MonthlyStatCrawler monthlyStatCrawler;

    public StatsService(MongoTemplate mongoTemplate, MonthlyStatCrawler monthlyStatCrawler) {
        this.mongoTemplate = mongoTemplate;
        this.monthlyStatCrawler = monthlyStatCrawler;
    }

    public boolean craw(String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM");
        String statId = dateTimeFormatter.format(LocalDate.now()) + GEN_9_OU;
        MonthlyMetaStat metaStat = findMetaStat(statId);
        if (metaStat != null) {
            log.info("{} stat is already exist", statId);
            return true;
        }
        log.info("start craw {} stat", statId);
        try {
            MonthlyBattleStatDto statDto = monthlyStatCrawler.craw(GEN_9_OU);
            save(format, statDto);
        } catch (Exception e) {
            log.error("craw {} stat failed", statId, e);
            return false;
        }
        return true;
    }

    public void save(String format, MonthlyBattleStatDto monthlyBattleStatDto) {
        LocalDate date = monthlyBattleStatDto.date();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String statId = formatter.format(date) + format;
        MonthlyMetaStat monthlyMetaStat = new MonthlyMetaStat(statId, format, date, monthlyBattleStatDto.battles(),
                monthlyBattleStatDto.metagame().tags());
        mongoTemplate.save(monthlyMetaStat);

        List<MonthlyPokemonUsage> pokemonUsages = new ArrayList<>(monthlyBattleStatDto.pokemon().size());
        List<MonthlyPokemonMoveSet> pokemonMoveSets = new ArrayList<>(monthlyBattleStatDto.pokemon().size());
        for (Map.Entry<String, MonthlyPokemonStatDto> entry : monthlyBattleStatDto.pokemon().entrySet()) {
            String name = entry.getKey();
            MonthlyPokemonStatDto pokemonStatDto = entry.getValue();
            var usage = pokemonStatDto.usage();
            pokemonUsages.add(new MonthlyPokemonUsage(null, name, format, date, statId, pokemonStatDto.count(),
                    new Usage(usage.raw(), usage.real(), usage.weighted())));
            pokemonMoveSets.add(new MonthlyPokemonMoveSet(null, name, format, date, statId, pokemonStatDto.abilities(),
                    pokemonStatDto.items(), pokemonStatDto.spreads(), pokemonStatDto.moves(), pokemonStatDto.teammates()
                    , pokemonStatDto.happinesses(), convertCounter(pokemonStatDto.counters())));
        }
        mongoTemplate.insertAll(pokemonUsages);
        mongoTemplate.insertAll(pokemonMoveSets);
    }

    public MonthlyMetaStat findMetaStat(String statId) {
        return mongoTemplate.findById(statId, MonthlyMetaStat.class);
    }

    private List<Counter> convertCounter(Map<String, List<Double>> counters) {
        List<Counter> counterList = new ArrayList<>(counters.size());
        for (Map.Entry<String, List<Double>> entry : counters.entrySet()) {
            String name = entry.getKey();
            List<Double> value = entry.getValue();
            if (value.size() < 3) {
                log.warn("Counter {} has less than three values {}", name, value);
                continue;
            }
            counterList.add(new Counter(name, value.get(COUNTER_KOED_INDEX), value.get(COUNTER_SWITCH_OUT_INDEX)));
        }
        return counterList;
    }
}