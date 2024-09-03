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
import org.mockito.invocation.InvocationOnMock;
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

    @Value("classpath:api/2024WcopQualifiers.html")
    private Resource qualifiersResource;

    @Value("classpath:api/2024WcopQualifiersPage11.html")
    private Resource qualifiersPage11Resource;

    @Value("classpath:api/2024WcopRound1.html")
    private Resource round1Resource;

    @Value("classpath:api/OltForum.html")
    private Resource oltForumsResource;

    @Value("classpath:api/OltXIRound1.html")
    private Resource oltRound1Resource;

    @Value("classpath:api/2024WcopRound1Page17.html")
    private Resource round1Page17Resource;


    @Test
    void getSemiWinSmogonPlayer() throws IOException {
        String tourForumsUrl = "http1s://www.smogon.com/forums/forums/world-cup-of-pokemon.234/";
        Document forumDoc = Jsoup.parse(forumsResource.getFile());
        Document semiFinalDoc = Jsoup.parse(semifinalsResource.getFile());
        Document semiFinalPage4Doc = Jsoup.parse(semifinalsPage4Resource.getFile());
        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.when(connection.get()).thenReturn(forumDoc, semiFinalDoc, semiFinalPage4Doc);

            SmogonTourWinPlayerExtractor extractor = new SmogonTourWinPlayerExtractor(tourForumsUrl, "The World Cup of Pokémon 2024",
                    List.of("Semifinals"));

            assertWinner(extractor, "Semifinals", "driplegend", "thefranklin", "thefranklin");
            assertWinner(extractor, "Semifinals", "lily", "kythr", "kythr");
            assertWinner(extractor, "Semifinals", "highv0ltag3", "s1nn0hc0nfirm3d", "highv0ltag3");
            assertWinner(extractor, "Semifinals", "twixtry", "zioziotrip", "twixtry");
            assertWinner(extractor, "Semifinals", "eeveeto", "oldspicemike", "eeveeto");
            assertWinner(extractor, "Semifinals", "ima", "zs", "zs");
            assertWinner(extractor, "Semifinals", "shiloh", "pais", "shiloh");
            assertWinner(extractor, "Semifinals", "pzz", "kebab mlml", "kebab mlml");
            assertWinner(extractor, "Semifinals", "niko", "vert", "vert");
            assertWinner(extractor, "Semifinals TB", "twixtry", "tace", "tace");
            assertWinner(extractor, "Semifinals TB", "luthier", "larry", "luthier");
        }
    }

    @Test
    void getQualifiersWinSmogonPlayer() throws IOException {
        String tourForumsUrl = "http1s://www.smogon.com/forums/forums/world-cup-of-pokemon.234/";
        Document forumDoc = Jsoup.parse(forumsResource.getFile());
        Document qualifiersDoc = Jsoup.parse(qualifiersResource.getFile());
        Document qualifiersPage11Doc = Jsoup.parse(qualifiersPage11Resource.getFile());
        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.when(connection.get()).thenReturn(forumDoc, qualifiersDoc, qualifiersPage11Doc);

            SmogonTourWinPlayerExtractor extractor = new SmogonTourWinPlayerExtractor(tourForumsUrl, "The World Cup of Pokémon 2024",
                    List.of("Qualifiers"));

            assertWinner(extractor, "Qualifiers Round 2", "liones", "calambrito", "liones");
            assertWinner(extractor, "Qualifiers Round 2", "feen", "crinchy costanza", "crinchy costanza");
            assertWinner(extractor, "Qualifiers Round 2", "ferenia", "keshba54", "keshba54");
            assertWinner(extractor, "Qualifiers Round 2", "gtcha", "l lawliet", "gtcha");
            assertWinner(extractor, "Qualifiers", "yves stone", "cscl", "yves stone");
            assertWinner(extractor, "Qualifiers", "zoyotte", "acr1", "acr1");
            assertWinner(extractor, "Qualifiers", "leo", "lokifan", "leo");
            assertWinner(extractor, "Qualifiers", "acr1", "devin", "devin");
            assertWinner(extractor, "Qualifiers", "haxlolo", "chaos23333", "chaos23333");
            assertWinner(extractor, "Qualifiers", "devin", "darkman64", "darkman64");
            assertWinner(extractor, "Qualifiers", "mimikyu stardust", "sieeeffmon", "mimikyu stardust");
            assertWinner(extractor, "Qualifiers", "dhrabb", "pan.", "dhrabb");
            assertWinner(extractor, "Qualifiers", "potatochan", "glfgno7", "potatochan");
            assertWinner(extractor, "Qualifiers", "mncmt", "skc44", "skc44");
            assertWinner(extractor, "Qualifiers", "chaos23333", "mako", "chaos23333");
            assertWinner(extractor, "Qualifiers", "bbeeaa", "ravenna", "bbeeaa");
            assertWinner(extractor, "Qualifiers", "mashin sentai", "lityl", "mashin sentai");
        }
    }

    @Test
    void getRound1WinSmogonPlayer() throws IOException {
        String tourForumsUrl = "http1s://www.smogon.com/forums/forums/world-cup-of-pokemon.234/";
        Document forumDoc = Jsoup.parse(forumsResource.getFile());
        Document r1Doc = Jsoup.parse(round1Resource.getFile());
        Document r1Page17Doc = Jsoup.parse(round1Page17Resource.getFile());
        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.when(connection.get()).thenReturn(forumDoc, r1Doc, r1Page17Doc);

            SmogonTourWinPlayerExtractor extractor = new SmogonTourWinPlayerExtractor(tourForumsUrl, "The World Cup of Pokémon 2024",
                    List.of("Round 1"));

            assertWinner(extractor, "Round 1 TB", "raptor", "gxe", "raptor");
            assertWinner(extractor, "Round 1 TB", "raptor", "lax", "raptor");
            assertWinner(extractor, "Round 1 TB", "aesf", "lax", "lax");
            assertWinner(extractor, "Round 1", "raceding", "uxilon", "raceding");
            assertWinner(extractor, "Round 1", "uxilon", "mimikyu stardust", "uxilon");
            assertWinner(extractor, "Round 1", "mimikyu stardust", "thiago nunes", "mimikyu stardust");
            assertWinner(extractor, "Round 1", "trogba trogba", "ak", "trogba trogba");
            assertWinner(extractor, "Round 1", "trosko", "lily", "lily");
            assertWinner(extractor, "Round 1", "lily", "hellom", "hellom");
            assertWinner(extractor, "Round 1", "mister mclovin", "maverick shooters", "maverick shooters");
            assertWinner(extractor, "Round 1", "hi.naming is hard", "dhrabb", "hi.naming is hard");
            assertWinner(extractor, "Round 1", "hi.naming is hard", "yves stone", "yves stone");
            assertWinner(extractor, "Round 1", "soulwind", "ima", "soulwind");
            assertWinner(extractor, "Round 1", "fant'sy beast", "kaif", "fant'sy beast");
            assertWinner(extractor, "Round 1", "ahsan-219", "3d", "ahsan-219");
            assertWinner(extractor, "Round 1", "drifting", "zioziotrip", "zioziotrip");
            assertWinner(extractor, "Round 1", "soulwind", "insult", "insult");
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

    @Test
    void getOltRound1WinSmogonPlayer() throws IOException {
        String tourForumsUrl = "https1://www.smogon.com/forums/forums/official-ladder-tournament.465/";
        Document forumDoc = Jsoup.parse(oltForumsResource.getFile());
        Document oltRound1Doc = Jsoup.parse(oltRound1Resource.getFile());
        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.when(connection.get()).thenReturn(forumDoc, oltRound1Doc);

            SmogonTourWinPlayerExtractor extractor = new SmogonTourWinPlayerExtractor(tourForumsUrl, "Smogon's Official Ladder Tournament XI",
                    List.of("Round 1"));

            assertWinner(extractor, "Round 1", "twixtry", "empo", "empo");
            assertWinner(extractor, "Round 1", "supagmoney", "vert", "vert");
            assertWinner(extractor, "Round 1", "mako", "3d", "3d");
            assertWinner(extractor, "Round 1", "xavgb", "rewer", "xavgb");
            assertWinner(extractor, "Round 1", "santu", "xdrudi.exe", "santu");
            assertWinner(extractor, "Round 1", "oldspicemike", "yovan33321", "oldspicemike");
            assertWinner(extractor, "Round 1", "alhen", "spookyz", "spookyz");
            assertWinner(extractor, "Round 1", "tace", "crying", "crying");
            assertWinner(extractor, "Round 1", "ahsan-219", "ewin", "ahsan-219");
            assertWinner(extractor, "Round 1", "welli0u", "storm zone", "welli0u");
            assertWinner(extractor, "Round 1", "insult", "dasmer", "dasmer");
            assertWinner(extractor, "Round 1", "tdnt", "mimilimi", "mimilimi");
            assertWinner(extractor, "Round 1", "emforbes", "bbeeaa", "emforbes");
            assertWinner(extractor, "Round 1", "chansey and lulu", "soulwind", "soulwind");
        }
    }

}