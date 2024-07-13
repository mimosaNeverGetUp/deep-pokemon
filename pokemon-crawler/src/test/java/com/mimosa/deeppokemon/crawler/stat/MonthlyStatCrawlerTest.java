/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.crawler.stat.dto.*;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyMetaStat;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyPokemonMoveSet;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyPokemonUsage;
import com.mimosa.deeppokemon.service.StatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class MonthlyStatCrawlerTest {

    @Autowired
    private MonthlyStatCrawler crawler;

    @Autowired
    private StatsService statsService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void craw() {
        MonthlyBattleStatDto statDto = crawler.craw("gen9ou");
        assertNotNull(statDto);
        assertNotNull(statDto.date());
        assertNotEquals(0, statDto.battles());
        Map<String, MonthlyPokemonStatDto> pokemon = statDto.pokemon();
        MonthlyMetaGameStatDto metagame = statDto.metagame();
        for (MonthlyPokemonStatDto pokemonStatDto : pokemon.values()) {
            assertNotNull(pokemonStatDto.abilities());
            assertNotNull(pokemonStatDto.viability());
            assertNotNull(pokemonStatDto.counters());
            assertNotNull(pokemonStatDto.happinesses());
            assertNotNull(pokemonStatDto.teammates());
            assertNotNull(pokemonStatDto.items());
            assertNotNull(pokemonStatDto.spreads());
            assertNotNull(pokemonStatDto.moves());
            Usage usage = pokemonStatDto.usage();
            assertNotNull(usage);
            assertNotEquals(0.0, usage.raw());
            assertNotEquals(0.0, usage.real());
            assertNotEquals(0.0, usage.weighted());
            LeadStat lead = pokemonStatDto.lead();
            assertNotNull(lead);
        }

        assertNotNull(pokemon);
        assertNotNull(metagame);

        LinkedHashMap<String, Double> tags = metagame.tags();
        assertNotNull(tags);
        assertFalse(tags.isEmpty());

        statsService.save("gen9ou", statDto);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String statId = formatter.format(statDto.date()) + "gen9ou";
        assertNotNull(mongoTemplate.findById(statId, MonthlyMetaStat.class));

        Query query = new BasicQuery("{}").addCriteria(Criteria.where("statId").is(statId));
        assertEquals(pokemon.size(), mongoTemplate.count(query, MonthlyPokemonUsage.class));
        assertEquals(pokemon.size(), mongoTemplate.count(query, MonthlyPokemonMoveSet.class));
    }
}