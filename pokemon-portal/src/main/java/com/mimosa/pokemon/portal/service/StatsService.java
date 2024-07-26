/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.service;

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String statId = formatter.format(LocalDate.now().minusMonths(1)) + format;
        Query query = new Query().addCriteria(Criteria.where("statId").is(statId)).with(Sort.by(Sort.Order.desc("usage.weighted")));
        long total = mongoTemplate.count(query, MonthlyPokemonUsage.class);
        MongodbUtils.withPageOperation(query, page, row);
        List<MonthlyPokemonUsage> pokemonUsages = mongoTemplate.find(query, MonthlyPokemonUsage.class);
        return new PageResponse<>(total, page, row, pokemonUsages);
    }

    public boolean ensureMonthlyStatsExist(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String statId = formatter.format(LocalDate.now().minusMonths(1)) + format;
        if (mongoTemplate.findById(statId, MonthlyMetaStat.class) != null) {
            return true;
        }
        log.info("try craw {} stats", statId);
        boolean result = crawlerApi.crawMonthlyStats(format);
        if (!result) {
            log.warn("craw monthly stats {} failed", statId);
        }
        return mongoTemplate.findById(statId, MonthlyMetaStat.class) != null;
    }

    public MonthlyMetaStat queryMeta(String format) {
        if (!ensureMonthlyStatsExist(format)) {
            throw new RuntimeException("Query monthly stats fail");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String statId = formatter.format(LocalDate.now().minusMonths(1)) + format;
        return mongoTemplate.findById(statId, MonthlyMetaStat.class);
    }

    public MonthlyPokemonMoveSet queryMoveSet(String format, String pokmeon) {
        if (!ensureMonthlyStatsExist(format)) {
            throw new RuntimeException("Query monthly stats fail");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String statId = formatter.format(LocalDate.now().minusMonths(1)) + format;
        Query query = new Query()
                .addCriteria(Criteria.where("statId").is(statId))
                .addCriteria(Criteria.where("name").is(pokmeon));

        return mongoTemplate.findOne(query, MonthlyPokemonMoveSet.class);
    }
}