/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class SmogonTourWinPlayerExtractorTest {
    @Value("classpath:api/WcopForum.html")
    private Resource forumsResource;

    @Value("classpath:api/2024WcopSemifinals.html")
    private Resource semifinalsResource;

    @Value("classpath:api/2024WcopSemifinalsPage4.html")
    private Resource semifinalsPage4Resource;

    @Test
    void getWinSmogonPlayer() throws IOException {
        String tourForumsUrl = "http1s://www.smogon.com/forums/forums/world-cup-of-pokemon.234/";
        Document forumDoc = Jsoup.parse(forumsResource.getFile());
        Document semiFinalDoc = Jsoup.parse(semifinalsResource.getFile());
        Document semiFinalPage4Doc = Jsoup.parse(semifinalsPage4Resource.getFile());
        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            Mockito.when(connection.get()).thenReturn(forumDoc, semiFinalDoc, semiFinalPage4Doc);

            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            SmogonTourWinPlayerExtractor extractor = new SmogonTourWinPlayerExtractor(tourForumsUrl, "The World Cup of Pokémon 2024",
                    List.of("Semifinals"));


            assertWinner(extractor, "Semifinals", "DripLegend", "TheFranklin", "TheFranklin");
            assertWinner(extractor, "Semifinals", "Lily", "kythr", "kythr");
            assertWinner(extractor, "Semifinals", "Highv0ltag3", "S1nn0hC0nfirm3d", "Highv0ltag3");
            assertWinner(extractor, "Semifinals", "Twixtry", "zioziotrip", "Twixtry");
            assertWinner(extractor, "Semifinals", "Eeveeto", "oldspicemike", "Eeveeto");
            assertWinner(extractor, "Semifinals", "ima", "zS", "zS");
            assertWinner(extractor, "Semifinals", "shiloh", "Pais", "shiloh");
            assertWinner(extractor, "Semifinals", "PZZ", "Kebab mlml", "Kebab mlml");
            assertWinner(extractor, "Semifinals", "Niko", "Vert", "Vert");
            assertWinner(extractor, "Semifinals TB", "Twixtry", "Tace", "Tace");
            assertWinner(extractor, "Semifinals TB", "Luthier", "Larry", "Luthier");
        }
    }


    void assertWinner(SmogonTourWinPlayerExtractor extractor, String stage, String firstPlayer, String secondPlayer,
                      String winner) {
        List<TourPlayer> tourPlayers = List.of(new TourPlayer(firstPlayer, null, null),
                new TourPlayer(secondPlayer, null, null));
        TourPlayer winSmogonPlayer = extractor.getWinSmogonPlayer(stage, tourPlayers);
        Assertions.assertNotNull(winSmogonPlayer);
        Assertions.assertEquals(winner, winSmogonPlayer.getName());
    }
}