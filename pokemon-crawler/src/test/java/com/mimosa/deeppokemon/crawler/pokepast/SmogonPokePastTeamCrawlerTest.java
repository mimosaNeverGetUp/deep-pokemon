/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
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
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmogonPokePastTeamCrawlerTest {

    @Autowired
    private PokePastTeamCrawler pokePastTeamCrawler;

    @Value("classpath:api/smogonTeamThread.html")
    private Resource smogonTeamThreadResource;

    @Value("classpath:api/pokepast/fb0e434de8244b90")
    Resource pokepastDoc;

    @Test
    void craw() throws IOException {
        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        Mockito.doReturn(false).when(mongoTemplate).exists(Mockito.any(), Mockito.any(Class.class));
        SmogonPokePastTeamCrawler smogonPokePastTeamCrawler = new SmogonPokePastTeamCrawler(pokePastTeamCrawler, mongoTemplate);
        Document threadDoc = Jsoup.parse(smogonTeamThreadResource.getFile());
        Document pastesDoc = Jsoup.parse(pokepastDoc.getFile());
        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.when(connection.get()).thenReturn(threadDoc, pastesDoc);
            List<PokePastTeam> pokePastTeams = smogonPokePastTeamCrawler.craw("https://www.smogon.com/forums/threads/jolteons-rise-to-glory-peaked-1-2065-elo-and-88-5-gxe.3767669/"
                    , false);
            assertEquals(1, pokePastTeams.size());
        }
    }
}