/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.service;

import com.mimosa.deeppokemon.entity.stat.PokemonAnalyze;
import com.mimosa.deeppokemon.entity.stat.PokemonSet;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyMetaStat;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyPokemonUsage;
import com.mimosa.deeppokemon.entity.stat.monthly.Usage;
import com.mimosa.pokemon.portal.dto.MonthlyPokemonMoveSetDto;
import com.mimosa.pokemon.portal.dto.MonthlyPokemonUsageDto;
import com.mimosa.pokemon.portal.entity.PageResponse;
import com.mimosa.pokemon.portal.service.microservice.CrawlerApi;
import com.mimosa.pokemon.portal.util.MongodbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatsService {
    private static final Logger log = LoggerFactory.getLogger(StatsService.class);
    protected static final String STAT_ID = "statId";
    protected static final String YYYY_MM = "yyyyMM";
    protected static final String NAME = "name";
    protected static final String FORMAT = "format";
    protected static final String DATE = "date";
    protected static final String USAGE = "usage";
    protected static final String MONTHLY_STAT_POKEMON_USAGE = "monthly_stat_pokemon_usage";
    protected static final String MONTHLY_STAT_POKEMON_MOVESET = "monthly_stat_pokemon_moveset";
    protected static final String USAGE_WEIGHTED = "usage.weighted";
    protected static final String ITEMS = "items";
    protected static final String MOVES = "moves";
    protected static final String ABILITIES = "abilities";
    protected static final String TERA_TYPES = "teraTypes";
    protected static final String MEGA = "-Mega";
    private final MongoTemplate mongoTemplate;
    private final CrawlerApi crawlerApi;

    public StatsService(MongoTemplate mongoTemplate, CrawlerApi crawlerApi) {
        this.mongoTemplate = mongoTemplate;
        this.crawlerApi = crawlerApi;
    }

    @Cacheable("monthlyUsage")
    public PageResponse<MonthlyPokemonUsageDto> queryUsage(String format, int page, int row) {
        String statId = getLatestStatId(format);
        Query query = new Query().addCriteria(Criteria.where(STAT_ID).is(statId)).with(Sort.by(Sort.Order.desc(USAGE_WEIGHTED)));
        long total = mongoTemplate.count(query, MonthlyPokemonUsage.class);
        MongodbUtils.withPageOperation(query, page, row);
        List<MonthlyPokemonUsageDto> pokemonUsages = mongoTemplate.find(query, MonthlyPokemonUsageDto.class, MONTHLY_STAT_POKEMON_USAGE);
        fillRankAndLastMonthUsage(pokemonUsages, page, row);
        return new PageResponse<>(total, page, row, pokemonUsages);
    }

    private void fillRankAndLastMonthUsage(List<MonthlyPokemonUsageDto> pokemonUsages, int page, int row) {
        if (pokemonUsages.isEmpty()) {
            return;
        }
        fillRank(pokemonUsages, page, row);

        LocalDate statDate = pokemonUsages.get(0).getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM);
        String lastMonthStatId = formatter.format(statDate.minusMonths(1)) + pokemonUsages.get(0).getFormat();
        Query query = new Query().addCriteria(Criteria.where(STAT_ID).is(lastMonthStatId))
                .with(Sort.by(Sort.Order.desc(USAGE_WEIGHTED)));
        query.fields().include(USAGE, NAME);
        List<MonthlyPokemonUsageDto> lastMonthPokemonUsages = mongoTemplate.find(query, MonthlyPokemonUsageDto.class,
                MONTHLY_STAT_POKEMON_USAGE);
        if (lastMonthPokemonUsages.isEmpty()) {
            return;
        }

        Map<String, MonthlyPokemonUsageDto> lastMonthUsageMap = lastMonthPokemonUsages.stream()
                .collect(Collectors.toMap(MonthlyPokemonUsageDto::getName, Function.identity()));
        fillRank(lastMonthPokemonUsages, 0, 0);
        for (MonthlyPokemonUsageDto pokemonUsageDto : pokemonUsages) {
            if (lastMonthUsageMap.containsKey(pokemonUsageDto.getName())) {
                pokemonUsageDto.setLastMonthUsage(lastMonthUsageMap.get(pokemonUsageDto.getName()));
            } else {
                // last month is zero usage
                MonthlyPokemonUsageDto lastMonthPokemonUsage = new MonthlyPokemonUsageDto();
                lastMonthPokemonUsage.setRank(0);
                lastMonthPokemonUsage.setUsage(new Usage(0, 0, 0));
                pokemonUsageDto.setLastMonthUsage(lastMonthPokemonUsage);
            }
        }
    }

    private void fillRank(List<MonthlyPokemonUsageDto> pokemonUsages, int page, int row) {
        int rank = 1 + page * row;
        for (MonthlyPokemonUsageDto pokemonUsageDto : pokemonUsages) {
            pokemonUsageDto.setRank(rank);
            ++rank;
        }
    }

    public boolean tryCrawLatestStat(String format) {
        return crawlerApi.crawMonthlyStats(format);
    }

    private boolean isStatsExist(String statId) {
        Query query = new Query().addCriteria(Criteria.where(STAT_ID).is(statId));
        return mongoTemplate.findById(statId, MonthlyMetaStat.class) != null
                && mongoTemplate.count(query, PokemonSet.class) > 0;
    }

    @Cacheable("monthlyMeta")
    public MonthlyMetaStat queryMeta(String format) {
        String statId = getLatestStatId(format);
        return mongoTemplate.findById(statId, MonthlyMetaStat.class);
    }

    @Cacheable("monthlyMoveSet")
    public MonthlyPokemonMoveSetDto queryMoveSet(String format, String pokmeon) {
        String statId = getLatestStatId(format);
        Query query = new Query()
                .addCriteria(Criteria.where(STAT_ID).is(statId))
                .addCriteria(Criteria.where(NAME).is(pokmeon));

        MonthlyPokemonMoveSetDto moveSetDto = mongoTemplate.findOne(query, MonthlyPokemonMoveSetDto.class, MONTHLY_STAT_POKEMON_MOVESET);
        fillLastMonthMoveSet(moveSetDto);
        return moveSetDto;
    }

    private void fillLastMonthMoveSet(MonthlyPokemonMoveSetDto moveSetDto) {
        if (moveSetDto == null) {
            return;
        }
        LocalDate statDate = moveSetDto.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM);
        String lastMonthStatId = formatter.format(statDate.minusMonths(1)) + moveSetDto.getFormat();
        Query query = new Query().addCriteria(Criteria.where(STAT_ID).is(lastMonthStatId)
                .and(NAME).is(moveSetDto.getName()));
        query.fields().include(ITEMS, MOVES, ABILITIES, TERA_TYPES);
        moveSetDto.setLastMonthMoveSet(mongoTemplate.findOne(query, MonthlyPokemonMoveSetDto.class, MONTHLY_STAT_POKEMON_MOVESET));
    }

    public String getLatestStatId(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM);
        String latestId = formatter.format(LocalDate.now().minusMonths(1)) + format;
        if (isStatsExist(latestId)) {
            return latestId;
        }

        if (isLatestStatUpdate()) {
            log.info("try craw latest stat {}", latestId);
            try {
                boolean crawResult = tryCrawLatestStat(format);
                if (crawResult || isStatsExist(latestId)) {
                    return latestId;
                }
            } catch (Exception e) {
                log.warn("craw latest stat {} fail", latestId);
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

    @Cacheable("monthlyPokemonSet")
    public PokemonSet queryPokemonSet(String format, String pokemon) {
        String statId = getLatestStatId(format);
        Query query = new Query()
                .addCriteria(Criteria.where(STAT_ID).is(statId))
                .addCriteria(Criteria.where(NAME).is(pokemon));
        PokemonSet pokemonSet = mongoTemplate.findOne(query, PokemonSet.class);
        if (pokemonSet == null && pokemon.contains(MEGA)) {
            return queryPokemonSet(format, pokemon.substring(0, pokemon.indexOf(MEGA)));
        }
        return pokemonSet;
    }

    public PokemonAnalyze queryPokemonAnalysis(String format, String pokemon) {
        String id = String.join("_", format, pokemon);
        PokemonAnalyze pokemonAnalyze = mongoTemplate.findById(id, PokemonAnalyze.class);
        if (pokemonAnalyze == null && pokemon.contains(MEGA)) {
            return queryPokemonAnalysis(format, pokemon.substring(0, pokemon.indexOf(MEGA)));
        } else if (pokemonAnalyze == null) {
            throw new ServerErrorException("pokemon analysis is empty", null);
        }

        return pokemonAnalyze;
    }
}