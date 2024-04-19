/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpUtil {
    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    public static String request(HttpUriRequest request) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpResponse response = httpclient.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            log.error("request fail", e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T request(HttpUriRequest request, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(request(request), tClass);
        } catch (JsonProcessingException e) {
            log.error("parse response to target class fail", e);
            throw new RuntimeException(e);
        }
    }
}