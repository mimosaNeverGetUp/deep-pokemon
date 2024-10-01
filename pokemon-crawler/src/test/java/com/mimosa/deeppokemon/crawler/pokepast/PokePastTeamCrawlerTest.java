/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.pokepast;

import com.mimosa.deeppokemon.entity.pokepast.PokePastTeam;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PokePastTeamCrawlerTest {

    @Autowired
    private PokePastTeamCrawler pokePastTeamCrawler;

    @Value("classpath:api/pokepast/fb0e434de8244b90")
    Resource pokepastDoc;

    @Value("classpath:api/pokepast/6a30608cd62cbb2e")
    Resource pokepastDoc_NickName;


    @Value("classpath:api/pokepast/f30e82f269cf37ad")
    Resource pokepastDoc_EmptyPokemon;


    @Test
    void craw() throws IOException {
        Document pokepastDocument = Jsoup.parse(pokepastDoc.getFile());
        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.when(connection.get()).thenReturn(pokepastDocument);
            PokePastTeam pokePastTeam = pokePastTeamCrawler.craw("https://pokepast.es/fb0e434de8244b90");
            assertNotNull(pokePastTeam);
            assertEquals("fb0e434de8244b90", pokePastTeam.id());
            assertNotNull(pokePastTeam.teamId());
            assertEquals("https://pokepast.es/fb0e434de8244b90", pokePastTeam.url());
            assertNull(pokePastTeam.format());
            assertNull(pokePastTeam.author());
            assertNotNull(pokePastTeam.pokemonSets());
            assertEquals(6, pokePastTeam.pokemonSets().size());
        }
    }

    @Test
    void crawNickNameTeam() throws IOException {
        Document pokepastDocument = Jsoup.parse(pokepastDoc_NickName.getFile());
        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.when(connection.get()).thenReturn(pokepastDocument);
            PokePastTeam pokePastTeam = pokePastTeamCrawler.craw("https://pokepast.es/6a30608cd62cbb2e");
            assertNotNull(pokePastTeam);
            assertEquals("6a30608cd62cbb2e", pokePastTeam.id());
            assertNotNull(pokePastTeam.teamId());
            assertEquals("https://pokepast.es/6a30608cd62cbb2e", pokePastTeam.url());
            assertEquals("gen9ou", pokePastTeam.format());
            assertEquals("Srn", pokePastTeam.author());
            assertNotNull(pokePastTeam.pokemonSets());
            assertEquals(6, pokePastTeam.pokemonSets().size());
        }
    }


    @Test
    void crawTeamWithEmptyPokemon() throws IOException {
        Document pokepastDocument = Jsoup.parse(pokepastDoc_EmptyPokemon.getFile());
        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.when(connection.get()).thenReturn(pokepastDocument);
            PokePastTeam pokePastTeam = pokePastTeamCrawler.craw("https://pokepast.es/f30e82f269cf37ad");
            assertNotNull(pokePastTeam);
            assertEquals("f30e82f269cf37ad", pokePastTeam.id());
            assertNotNull(pokePastTeam.teamId());
            assertNull(pokePastTeam.format());
            assertNull(pokePastTeam.author());
            assertEquals("https://pokepast.es/f30e82f269cf37ad", pokePastTeam.url());
            assertNotNull(pokePastTeam.pokemonSets());
            assertEquals(6, pokePastTeam.pokemonSets().size());
        }
    }
}