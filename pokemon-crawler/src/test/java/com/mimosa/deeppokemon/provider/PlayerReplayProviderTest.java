/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class PlayerReplayProviderTest {

    @Value("classpath:api/replaySearch.json")
    private Resource apiResponseResource;

    @ParameterizedTest
    @CsvSource(value = {"Separation,gen9ou"})
    void next(String name, String format) {
        long uploadTimeAfter = 1713289463;
        PlayerReplayProvider provider = new PlayerReplayProvider(name, format, uploadTimeAfter);
        PlayerReplayProvider spyProvider = Mockito.spy(provider);
        Replay replay = new Replay("1234", 1713289465, "gen9ou", 1759, new String[]{"Separation", "mimosa"}, false);
        ReplaySource source = new ReplaySource(Collections.singletonList("ladder"), Collections.singletonList(
                replay));
        Mockito.doReturn(Collections.singletonList(source)).when(spyProvider).queryReplayPage(1);
        Mockito.doReturn(Collections.emptyList()).when(spyProvider).queryReplayPage(2);

        while (spyProvider.hasNext()) {
            ReplaySource replaySource = spyProvider.next();
            assertReplaySource(replaySource);
        }

        Mockito.verify(spyProvider, Mockito.times(1)).next();
        Mockito.verify(spyProvider, Mockito.times(2)).queryReplayPage(Mockito.anyInt());
    }

    @ParameterizedTest
    @CsvSource(value = {"Separation,gen9ou"})
    void queryReplayPage(String name, String format) throws IOException{
        long uploadTimeAfter = 1713289463;
        int minRating = 1750;
        PlayerReplayProvider provider = new PlayerReplayProvider(name, format, uploadTimeAfter, minRating);
        try (var mockHttpUtil = Mockito.mockStatic(HttpUtil.class)) {
            mockHttpUtil.when(() -> HttpUtil.request(Mockito.any())).thenReturn(apiResponseResource
                    .getContentAsString(StandardCharsets.UTF_8));
            List<ReplaySource> replaySources = provider.queryReplayPage(1);
            Assertions.assertEquals(5, replaySources.size());
            replaySources.forEach(PlayerReplayProviderTest::assertReplaySource);
        }
    }

    private static void assertReplaySource(ReplaySource replaySource) {
        Assertions.assertNotNull(replaySource);
        Assertions.assertNotNull(replaySource.replayType());
        Assertions.assertFalse(replaySource.replayList().isEmpty());
        replaySource.replayList().forEach(PlayerReplayProviderTest::assertReplay);
    }

    private static void assertReplay(Replay replay) {
        Assertions.assertNotNull(replay.getId());
        Assertions.assertNotNull(replay.getFormat());
        Assertions.assertEquals(2, replay.getPlayers().length);
        Assertions.assertNotEquals(0, replay.getUploadTime());
        Assertions.assertNotEquals(0, replay.getRating());
    }
}