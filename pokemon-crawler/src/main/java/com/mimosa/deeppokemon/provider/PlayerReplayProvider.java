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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class PlayerReplayProvider implements ReplayProvider {
    private static final Logger logger = LoggerFactory.getLogger(PlayerReplayProvider.class);

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String PLAYER_REPLAY_QUERY_URL = "https://replay.pokemonshowdown.com/api/replays/search";

    public static final String LADDER = "ladder";

    private static final RequestConfig CONFIG = RequestConfig.custom().setConnectTimeout(3 * 1000).//创建连接的最长时间，单位是毫秒
            setConnectionRequestTimeout(3 * 1000).//设置获取连接的最长时间，单位毫秒
            setSocketTimeout(3 * 1000)//设置数据传输的最长时间，单位毫秒
            .build();

    private final String name;

    private final String format;

    private final long uploadTimeAfter;

    private int bufferPage;

    private final Queue<ReplaySource> replaySourceBuffer;

    public PlayerReplayProvider(String name, String format, long uploadTimeAfter) {
        this.name = name;
        this.format = format;
        bufferPage = 1;
        this.uploadTimeAfter = uploadTimeAfter;
        replaySourceBuffer = new LinkedList<>();
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

    public List<ReplaySource> queryReplayPage(int page) {
        try {
            URI uri = new URIBuilder(PLAYER_REPLAY_QUERY_URL)
                    .addParameter("page", String.valueOf(page))
                    .addParameter("username", name)
                    .addParameter("format", format)
                    .build();
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setConfig(CONFIG);
            logger.info("query player {} replay: {}", name, uri.toString());

            String replayJsonStr = convertResponseToJson(HttpUtil.request(httpGet));
            List<Replay> replays = OBJECT_MAPPER.readValue(replayJsonStr, new TypeReference<>() {
            });
            return replays.stream()
                    .filter(replay -> replay.uploadTime() > uploadTimeAfter)
                    .map(replay -> new ReplaySource(LADDER, Collections.singletonList(replay)))
                    .collect(Collectors.toList());
        } catch (URISyntaxException e) {
            logger.error("build query replay uri occur error", e);
            throw new RuntimeException("build query replay uri occur error", e);
        } catch (JsonProcessingException e) {
            logger.error("parse api response fail", e);
            throw new RuntimeException("parse api response fail", e);
        }
    }

    private String convertResponseToJson(String response) {
        return response.substring(1);
    }
}