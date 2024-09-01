/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OltTourReplayProviderTest {
    @Value("classpath:api/2023OltReplay.html")
    private Resource replayDocument;

    @Test
    void test() throws IOException {
        String replayThreadUrl = "http1s://www.smogon" +
                ".com/forums/threads/the-world-cup-of-pok%C3%A9mon-2024-replays.3742226/";
        Set<String> stageTitles = Set.of("Round 1", "Round 2", "Round 3", "Round 4", "Round 5", "Top 16",
                "Quarterfinals", "Semifinals", "Finals");

        Document document = Jsoup.parse(replayDocument.getFile());
        OltTourReplayProvider oltTourReplayProvider;
        try (var mockJsoup = Mockito.mockStatic(Jsoup.class)) {
            Connection connection = Mockito.mock(Connection.class);
            mockJsoup.when(() -> Jsoup.connect(Mockito.any())).thenReturn(connection);
            Mockito.doAnswer(InvocationOnMock::getMock).when(connection).timeout(Mockito.anyInt());
            Mockito.doReturn(document).when(connection).get();

            oltTourReplayProvider =
                    new OltTourReplayProvider("WCOP2024", replayThreadUrl, "gen9ou", stageTitles);
            assertTrue(oltTourReplayProvider.hasNext());
        }
    }

}