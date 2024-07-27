/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class HttpProxy {
    private static final Logger log = LoggerFactory.getLogger(HttpProxy.class);
    private static final String SCRAPER_API = "https://api.scraperapi.com";
    private static final String PARAM_API_KEY = "api_key";
    private static final String URL = "url";

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Value("${SCRAPER_KEY:}")
    private String apiKey;

    @Value("${PROXY_ENABLE:false}")
    private boolean enableProxy;

    public String get(String url) {
        try {
            ClassicHttpRequest request;
            if (!enableProxy || apiKey == null) {
                log.info("proxy is disable or api key is null, try request directly");
                URIBuilder uriBuilder = new URIBuilder(url);
                request = ClassicRequestBuilder.get(uriBuilder.build()).build();
            } else {
                URI uri = new URIBuilder(SCRAPER_API)
                        .addParameter(PARAM_API_KEY, apiKey)
                        .addParameter(URL, url)
                        .build();
                request = ClassicRequestBuilder.get(uri).build();
            }

            return HttpUtil.request(request);
        } catch (URISyntaxException e) {
            throw new ServerErrorException(e.getLocalizedMessage(), e);
        }
    }

    public <T> T get(String url, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(get(url), tClass);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException(e.getLocalizedMessage(), e);
        }
    }

    public <T> T get(String url, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(get(url), typeReference);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException(e.getLocalizedMessage(), e);
        }
    }
}