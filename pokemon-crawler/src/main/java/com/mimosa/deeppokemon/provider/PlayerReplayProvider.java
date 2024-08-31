/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.web.server.ServerErrorException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PlayerReplayProvider implements ReplayProvider {
    private static final Logger logger = LoggerFactory.getLogger(PlayerReplayProvider.class);

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String PLAYER_REPLAY_QUERY_URL = "https://replay.pokemonshowdown.com/api/replays/search";

    public static final String LADDER = "ladder";

    private final String name;

    private final String format;

    private final long uploadTimeAfter;

    private final int minRating;

    private int bufferPage;

    private final Queue<ReplaySource> replaySourceBuffer;

    public PlayerReplayProvider(String name, String format, long uploadTimeAfter, int minRating) {
        this.name = name;
        this.format = format;
        bufferPage = 1;
        this.uploadTimeAfter = uploadTimeAfter;
        this.minRating = minRating;
        replaySourceBuffer = new LinkedList<>();
    }

    public PlayerReplayProvider(String name, String format, long uploadTimeAfter) {
        this(name, format, uploadTimeAfter, 0);
    }

    @Override
    public ReplaySource next() {
        extractIfEmpty();
        return replaySourceBuffer.poll();
    }

    @Override
    public boolean hasNext() {
        extractIfEmpty();
        return !replaySourceBuffer.isEmpty();
    }

    private void extractIfEmpty() {
        if (replaySourceBuffer.isEmpty()) {
            replaySourceBuffer.addAll(queryReplayPage(bufferPage));
            ++bufferPage;
        }
    }

    @RegisterReflectionForBinding(Replay.class)
    public List<ReplaySource> queryReplayPage(int page) {
        try {
            URI uri = new URIBuilder(PLAYER_REPLAY_QUERY_URL)
                    .addParameter("page", String.valueOf(page))
                    .addParameter("username", name)
                    .addParameter("format", format)
                    .build();
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(uri).build();
            logger.info("query player {} replay: {}", name, uri);

            String replayJsonStr = convertResponseToJson(HttpUtil.request(httpGet));
            List<Replay> replays = OBJECT_MAPPER.readValue(replayJsonStr, new TypeReference<>() {
            });
            return replays.stream()
                    .filter(replay -> replay.getUploadTime() > uploadTimeAfter)
                    .filter(replay -> replay.getRating() >= minRating)
                    .map(replay -> new ReplaySource(Collections.singletonList(LADDER), Collections.singletonList(replay)))
                    .toList();
        } catch (URISyntaxException e) {
            throw new ServerErrorException("build query replay uri occur error", e);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("parse api response fail", e);
        }
    }

    private String convertResponseToJson(String response) {
        return response.substring(1);
    }
}