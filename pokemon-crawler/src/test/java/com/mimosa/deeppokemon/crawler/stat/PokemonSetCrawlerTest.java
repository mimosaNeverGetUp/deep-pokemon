/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.stat.PokemonSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class PokemonSetCrawlerTest {

    @Autowired
    PokemonSetCrawler setCrawler;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void craw() {
        List<PokemonSet> gen9ou = setCrawler.craw("gen9ou");
        assertNotNull(gen9ou);
        assertFalse(gen9ou.isEmpty());
        mongoTemplate.insertAll(gen9ou);
    }
}