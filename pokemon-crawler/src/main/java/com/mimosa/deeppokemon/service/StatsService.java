/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.crawler.stat.MonthlyStatCrawler;
import com.mimosa.deeppokemon.crawler.stat.PokemonSetCrawler;
import com.mimosa.deeppokemon.crawler.stat.dto.MonthlyBattleStatDto;
import com.mimosa.deeppokemon.crawler.stat.dto.MonthlyPokemonStatDto;
import com.mimosa.deeppokemon.entity.stat.PokemonSet;
import com.mimosa.deeppokemon.entity.stat.monthly.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    protected static final String STAT_ID = "statId";
    private final MongoTemplate mongoTemplate;
    private final MonthlyStatCrawler monthlyStatCrawler;
    private final PokemonSetCrawler pokemonSetCrawler;

    public StatsService(MongoTemplate mongoTemplate, MonthlyStatCrawler monthlyStatCrawler, PokemonSetCrawler pokemonSetCrawler) {
        this.mongoTemplate = mongoTemplate;
        this.monthlyStatCrawler = monthlyStatCrawler;
        this.pokemonSetCrawler = pokemonSetCrawler;
    }

    public boolean crawStat(String format, String statId) {
        MonthlyMetaStat metaStat = findMetaStat(statId);
        if (metaStat != null) {
            log.info("stat {} is already exist", statId);
            return true;
        }
        log.info("start craw {} stat", statId);
        try {
            MonthlyBattleStatDto statDto = monthlyStatCrawler.craw(format);
            save(format, statDto);
        } catch (Exception e) {
            log.error("craw {} stat failed", statId, e);
            return false;
        }
        return true;
    }

    public synchronized boolean craw(String format) {
        boolean result = true;
        String statId = getLatestStatId(format);
        result = crawStat(format, statId);

        crawPokemonSet(format, statId);
        return result;
    }

    private void crawPokemonSet(String format, String statId) {
        Query query = new Query().addCriteria(Criteria.where(STAT_ID).is(statId));
        if (mongoTemplate.count(query, PokemonSet.class) > 0) {
            log.info("{} pokemon set is already exist", statId);
            return;
        }

        try {
            List<PokemonSet> pokemonSets = pokemonSetCrawler.craw(format);
            mongoTemplate.insertAll(pokemonSets);
        } catch (Exception e) {
            log.error("craw {} pokemon set failed", statId, e);
        }
    }

    public void save(String format, MonthlyBattleStatDto monthlyBattleStatDto) {
        LocalDate date = monthlyBattleStatDto.date();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String statId = formatter.format(date) + format;
        MonthlyMetaStat monthlyMetaStat = new MonthlyMetaStat(statId, format, date, monthlyBattleStatDto.battles(),
                monthlyBattleStatDto.metagame().tags());

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
        mongoTemplate.insert(monthlyMetaStat);
        mongoTemplate.insertAll(pokemonUsages);
        mongoTemplate.insertAll(pokemonMoveSets);
    }

    public String getLatestStatId(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        return formatter.format(LocalDate.now().minusMonths(1)) + format;
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