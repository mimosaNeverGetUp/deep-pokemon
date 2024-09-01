/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat;

import com.mimosa.deeppokemon.entity.stat.PokemonSet;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PokemonSetCrawlerTest {

    @Autowired
    PokemonSetCrawler setCrawler;

    @Autowired
    MongoTemplate mongoTemplate;

    @Value("classpath:api/stat/set_gen9ou.json")
    Resource setGen9ouResource;

    @Test
    void craw() throws IOException {

        try (var mockHttpUtil = Mockito.mockStatic(HttpUtil.class)) {
            mockHttpUtil.when(() -> HttpUtil.request(Mockito.any()))
                    .thenReturn(setGen9ouResource.getContentAsString(StandardCharsets.UTF_8));
            List<PokemonSet> gen9ou = setCrawler.craw("gen9ou");
            assertNotNull(gen9ou);
            assertFalse(gen9ou.isEmpty());
        }
    }
}