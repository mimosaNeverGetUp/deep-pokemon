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
import org.springframework.web.server.ServerErrorException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsService {
    private static final Logger log = LoggerFactory.getLogger(StatsService.class);
    protected static final String STAT_ID = "statId";
    protected static final String YYYY_MM = "yyyyMM";
    protected static final String NAME = "name";
    protected static final String FORMAT = "format";
    protected static final String DATE = "date";
    private final MongoTemplate mongoTemplate;
    private final CrawlerApi crawlerApi;

    public StatsService(MongoTemplate mongoTemplate, CrawlerApi crawlerApi) {
        this.mongoTemplate = mongoTemplate;
        this.crawlerApi = crawlerApi;
    }

    public PageResponse<MonthlyPokemonUsage> queryUsage(String format, int page, int row) {
        String statId = getLatestStatId(format);
        Query query = new Query().addCriteria(Criteria.where(STAT_ID).is(statId)).with(Sort.by(Sort.Order.desc("usage.weighted")));
        long total = mongoTemplate.count(query, MonthlyPokemonUsage.class);
        MongodbUtils.withPageOperation(query, page, row);
        List<MonthlyPokemonUsage> pokemonUsages = mongoTemplate.find(query, MonthlyPokemonUsage.class);
        return new PageResponse<>(total, page, row, pokemonUsages);
    }

    public boolean tryCrawLatestStat(String format) {
        return crawlerApi.crawMonthlyStats(format);
    }

    private boolean isStatsExist(String statId) {
        Query query = new Query().addCriteria(Criteria.where(STAT_ID).is(statId));
        return mongoTemplate.findById(statId, MonthlyMetaStat.class) != null
                && mongoTemplate.count(query, PokemonSet.class) > 0;
    }

    public MonthlyMetaStat queryMeta(String format) {
        String statId = getLatestStatId(format);
        return mongoTemplate.findById(statId, MonthlyMetaStat.class);
    }

    public MonthlyPokemonMoveSet queryMoveSet(String format, String pokmeon) {
        String statId = getLatestStatId(format);
        Query query = new Query()
                .addCriteria(Criteria.where(STAT_ID).is(statId))
                .addCriteria(Criteria.where(NAME).is(pokmeon));

        return mongoTemplate.findOne(query, MonthlyPokemonMoveSet.class);
    }

    public String getLatestStatId(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM);
        String latestId = formatter.format(LocalDate.now().minusMonths(1)) + format;
        if (isStatsExist(latestId)) {
            return latestId;
        }

        if (isLatestStatUpdate()) {
            log.info("try craw latest stat {}", latestId);
            boolean crawResult = tryCrawLatestStat(format);
            if (crawResult || isStatsExist(latestId)) {
                return latestId;
            }
        }

        log.warn("try craw latest stat {} failed, use last stat in db", latestId);
        // use last stat in db
        Query query = new Query()
                .addCriteria(Criteria.where(FORMAT).is(format))
                .with(Sort.by(Sort.Order.desc(DATE)));
        MonthlyMetaStat latestStat = mongoTemplate.findOne(query, MonthlyMetaStat.class);

        if (latestStat == null) {
            throw new ServerErrorException("can not get latest stat", null);
        }
        return latestStat.id();
    }

    private boolean isLatestStatUpdate() {
        return LocalDate.now().getDayOfMonth() != 1;
    }

    public PokemonSet queryPokemonSet(String format, String pokemon) {
        String statId = getLatestStatId(format);
        Query query = new Query()
                .addCriteria(Criteria.where(STAT_ID).is(statId))
                .addCriteria(Criteria.where(NAME).is(pokemon));
        return mongoTemplate.findOne(query, PokemonSet.class);
    }
}