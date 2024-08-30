/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.SmogonTourReplay;
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
import java.util.List;

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

             smogonTourReplayProvider =
                    new SmogonTourReplayProvider("WCOP2024", replayThreadUrl, "gen9ou", stageTitles);
            assertTrue(smogonTourReplayProvider.hasNext());
        }
        int i = 0;
        List<String> exceptStageTitles = List.of("Qualifiers", "Qualifiers Round 2", "Round 1", "Round 1 TB",
                "Quarterfinals", "Semifinals", "Semifinals TB", "Finals");
        while (smogonTourReplayProvider.hasNext()) {
            String stage = exceptStageTitles.get(i);
            List<Replay> replays = smogonTourReplayProvider.next().replayList();
            for(Replay replay : replays) {
                SmogonTourReplay smogonTourReplay = (SmogonTourReplay) replay;
                assertEquals(stage, smogonTourReplay.getStage());
                assertNotNull(smogonTourReplay.getTourName());
                assertNotNull(smogonTourReplay.getTourPlayers());
                assertNotNull(smogonTourReplay.getId());
                for (var player : smogonTourReplay.getTourPlayers()) {
                    assertNotNull(player.getName());
                    assertNotNull(player.getId());
                }
            }
            ++i;
        }
    }
}