/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat;

import com.mimosa.deeppokemon.entity.stat.PokemonAnalyze;
import com.mimosa.deeppokemon.service.AiService;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PokemonAnalyzeCrawlerTest {

    @Autowired
    private PokemonAnalyzeCrawler pokemonAnalyzeCrawler;

    @Value("classpath:api/stat/analyze_gen9ou.json")
    Resource setGen9ouResource;

    @MockBean
    AiService aiService;

    @Test
    void craw() throws IOException {
        try (var mockHttpUtil = Mockito.mockStatic(HttpUtil.class)) {
            mockHttpUtil.when(() -> HttpUtil.request(Mockito.any()))
                    .thenReturn(setGen9ouResource.getContentAsString(StandardCharsets.UTF_8));
            List<PokemonAnalyze> pokemonAnalyzes = pokemonAnalyzeCrawler.craw("gen9ou", null);
            assertNotNull(pokemonAnalyzes);
            assertFalse(pokemonAnalyzes.isEmpty());
            for (PokemonAnalyze pokemonAnalyze : pokemonAnalyzes) {
                assertNotNull(pokemonAnalyze.id());
                assertNotNull(pokemonAnalyze.name());
                assertNotNull(pokemonAnalyze.setAnalyzes());
                assertNotNull(pokemonAnalyze.setChineseAnalyzes());
                assertEquals("gen9ou", pokemonAnalyze.format());
            }
        }
    }
}