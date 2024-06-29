/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpUtil {
    private static final PoolingHttpClientConnectionManager connectionManager;

    private static final CloseableHttpClient client;

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    protected static final int MAX_RETRIES = 3;

    protected static final int WAIT_TIMEOUT = 5;

    static {
        connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                        .setSslContext(SSLContexts.createSystemDefault())
                        .setTlsVersions(TLS.V_1_3)
                        .build())
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(Timeout.ofSeconds(WAIT_TIMEOUT))
                        .build())
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                .setConnPoolPolicy(PoolReusePolicy.LIFO)
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setSocketTimeout(Timeout.ofSeconds(WAIT_TIMEOUT))
                        .setConnectTimeout(Timeout.ofSeconds(WAIT_TIMEOUT))
                        .setTimeToLive(TimeValue.ofMinutes(10))
                        .build())
                .build();

        client = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(StandardCookieSpec.STRICT)
                        .build())
                .setRetryStrategy(new HttpRequestRetryStrategy(MAX_RETRIES, TimeValue.of(1, TimeUnit.SECONDS)))
                .build();
    }

    private HttpUtil() {}

    public static String request(ClassicHttpRequest request) {
        try {
            return client.execute(request, response -> {
                String body = EntityUtils.toString(response.getEntity());
                EntityUtils.consume(response.getEntity());
                return body;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T request(ClassicHttpRequest request, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(request(request), tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class HttpRequestRetryStrategy extends DefaultHttpRequestRetryStrategy {
        protected static final List<Class<? extends IOException>> NO_RETRY_EXCEPTION = Arrays.asList(
                UnknownHostException.class, ConnectException.class, ConnectionClosedException.class, NoRouteToHostException.class, SSLException.class);

        public HttpRequestRetryStrategy(int maxRetries, TimeValue defaultRetryInterval) {
            super(maxRetries, defaultRetryInterval, NO_RETRY_EXCEPTION, Arrays.asList(429, 503));
        }
    }
}