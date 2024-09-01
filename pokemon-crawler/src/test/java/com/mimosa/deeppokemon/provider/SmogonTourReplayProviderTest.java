/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.crawler.SmogonTourWinPlayerExtractor;
import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.SmogonTourReplay;
import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmogonTourReplayProviderTest {
    @Value("classpath:api/2024WcopReplay.html")
    private Resource replayDocument;

    @Test
    void Wcop2024() throws IOException {
        String replayThreadUrl = "http1s://www.smogon" +
                ".com/forums/threads/the-world-cup-of-pok%C3%A9mon-2024-replays.3742226/";
        List<String> stageTitles = List.of("Qualifiers", "Qualifiers Round 2", "Round 1", "Quarterfinals", "Semifinals", "Finals");

        Document document = Jsoup.parse(replayDocument.getFile());
        SmogonTourReplayProvider smogonTourReplayProvider;

        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.doReturn(document).when(connection).get();
            SmogonTourWinPlayerExtractor winPlayerExtractor = Mockito.mock(SmogonTourWinPlayerExtractor.class);
            Mockito.doReturn(new TourPlayer("a", null, null)).when(winPlayerExtractor)
                    .getWinSmogonPlayer(Mockito.any(), Mockito.anyList());

            smogonTourReplayProvider = new SmogonTourReplayProvider("WCOP2024", replayThreadUrl,
                    "gen9ou", stageTitles, winPlayerExtractor);
            assertTrue(smogonTourReplayProvider.hasNext());
        }
        List<String> exceptStageTitles = List.of("Qualifiers", "Qualifiers Round 2", "Round 1", "Round 1 TB",
                "Quarterfinals", "Semifinals", "Semifinals TB", "Finals");
        Map<String, Boolean> stageMap = new HashMap<>();
        for(String exceptStageTitle : exceptStageTitles) {
            stageMap.put(exceptStageTitle, false);
        }
        while (smogonTourReplayProvider.hasNext()) {
            List<Replay> replays = smogonTourReplayProvider.next().replayList();
            for(Replay replay : replays) {
                SmogonTourReplay smogonTourReplay = (SmogonTourReplay) replay;
                stageMap.put(smogonTourReplay.getStage(), true);
                assertNotNull(smogonTourReplay.getTourName());
                assertNotNull(smogonTourReplay.getTourPlayers());
                assertNotNull(smogonTourReplay.getId());
                assertNotNull(smogonTourReplay.getWinPlayer());
                for (var player : smogonTourReplay.getTourPlayers()) {
                    assertNotNull(player.getName());
                    assertNotNull(player.getTourPlayerId());
                    assertEquals(player.getName(), player.getName().trim().toLowerCase());
                }
            }
            ++i;
        }
        assertEquals(exceptStageTitles.size(), stageMap.size());
        for (var entry : stageMap.entrySet()) {
            assertTrue(entry.getValue());
        }
    }

}