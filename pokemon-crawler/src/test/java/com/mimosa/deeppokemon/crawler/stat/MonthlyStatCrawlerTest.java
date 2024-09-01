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
import com.mimosa.deeppokemon.utils.HttpProxy;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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

    @SpyBean
    private HttpProxy httpProxy;

    @Value("classpath:api/stat/gen9ou.json")
    Resource monthlyBattleStatResource;

    @Test
    void craw() throws IOException {
        Mockito.doReturn(monthlyBattleStatResource.getContentAsString(StandardCharsets.UTF_8)).when(httpProxy).get(Mockito.any());
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
        MonthlyBattleStatDto mock = Mockito.spy(statDto);
        Mockito.doReturn(LocalDate.of(2022, 8, 9)).when(mock).date();
        statsService.save("gen9ou", mock);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String statId = formatter.format(mock.date()) + "gen9ou";
        Query query = new Query().addCriteria(Criteria.where("statId").is(statId));

        try {
            assertNotNull(mongoTemplate.findById(statId, MonthlyMetaStat.class));

            assertEquals(pokemon.size(), mongoTemplate.count(query, MonthlyPokemonUsage.class));
            assertEquals(pokemon.size(), mongoTemplate.count(query, MonthlyPokemonMoveSet.class));
        } finally {
            mongoTemplate.remove(new Query(Criteria.where("_id").is(statId)), MonthlyMetaStat.class);
            mongoTemplate.remove(query, MonthlyPokemonUsage.class);
            mongoTemplate.remove(query, MonthlyPokemonMoveSet.class);
        }
    }

}