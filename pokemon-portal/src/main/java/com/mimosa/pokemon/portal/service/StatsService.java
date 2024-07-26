/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.service;

import com.mimosa.deeppokemon.entity.stat.PokemonSet;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyMetaStat;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyPokemonMoveSet;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyPokemonUsage;
import com.mimosa.pokemon.portal.entity.PageResponse;
import com.mimosa.pokemon.portal.service.microservice.CrawlerApi;
import com.mimosa.pokemon.portal.util.MongodbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsService {
    private static final Logger log = LoggerFactory.getLogger(StatsService.class);
    protected static final String STAT_ID = "statId";
    protected static final String YYYY_MM = "yyyyMM";
    protected static final String NAME = "name";
    private final MongoTemplate mongoTemplate;
    private final CrawlerApi crawlerApi;

    public StatsService(MongoTemplate mongoTemplate, CrawlerApi crawlerApi) {
        this.mongoTemplate = mongoTemplate;
        this.crawlerApi = crawlerApi;
    }

    public PageResponse<MonthlyPokemonUsage> queryUsage(String format, int page, int row) {
        if (!ensureMonthlyStatsExist(format)) {
            log.error("Query monthly stats fail");
            throw new RuntimeException("Query monthly stats fail");
        }

        String statId = getLatestStatId(format);
        Query query = new Query().addCriteria(Criteria.where(STAT_ID).is(statId)).with(Sort.by(Sort.Order.desc("usage.weighted")));
        long total = mongoTemplate.count(query, MonthlyPokemonUsage.class);
        MongodbUtils.withPageOperation(query, page, row);
        List<MonthlyPokemonUsage> pokemonUsages = mongoTemplate.find(query, MonthlyPokemonUsage.class);
        return new PageResponse<>(total, page, row, pokemonUsages);
    }

    public boolean ensureMonthlyStatsExist(String format) {
        String statId = getLatestStatId(format);
        if (isStatsExist(statId)) {
            return true;
        }
        log.info("try craw {} stats", statId);
        boolean result = crawlerApi.crawMonthlyStats(format);
        if (!result) {
            log.warn("craw monthly stats {} failed", statId);
        }
        return isStatsExist(statId);
    }

    private boolean isStatsExist(String statId) {
        Query query = new Query().addCriteria(Criteria.where(STAT_ID).is(statId));
        return mongoTemplate.findById(statId, MonthlyMetaStat.class) != null
                && mongoTemplate.count(query, PokemonSet.class) > 0;
    }

    public MonthlyMetaStat queryMeta(String format) {
        if (!ensureMonthlyStatsExist(format)) {
            throw new RuntimeException("Query monthly stats fail");
        }

        String statId = getLatestStatId(format);
        return mongoTemplate.findById(statId, MonthlyMetaStat.class);
    }

    public MonthlyPokemonMoveSet queryMoveSet(String format, String pokmeon) {
        if (!ensureMonthlyStatsExist(format)) {
            throw new RuntimeException("Query monthly stats fail");
        }

        String statId = getLatestStatId(format);
        Query query = new Query()
                .addCriteria(Criteria.where(STAT_ID).is(statId))
                .addCriteria(Criteria.where(NAME).is(pokmeon));

        return mongoTemplate.findOne(query, MonthlyPokemonMoveSet.class);
    }

    private static String getLatestStatId(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM);
        return formatter.format(LocalDate.now().minusMonths(1)) + format;
    }

    public PokemonSet queryPokemonSet(String format, String pokemon) {
        ensureMonthlyStatsExist(format);
        String statId = getLatestStatId(format);
        Query query = new Query()
                .addCriteria(Criteria.where(STAT_ID).is(statId))
                .addCriteria(Criteria.where(NAME).is(pokemon));
        return mongoTemplate.findOne(query, PokemonSet.class);
    }
}