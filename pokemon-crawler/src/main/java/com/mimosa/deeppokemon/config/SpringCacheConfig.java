/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@EnableCaching
@Configuration
public class SpringCacheConfig implements CachingConfigurer {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(3)))
                .transactionAware()
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new NoOperationCacheErrorHandler();
    }

    public static class NoOperationCacheErrorHandler implements CacheErrorHandler {
        private static final Logger log = LoggerFactory.getLogger(NoOperationCacheErrorHandler.class);

        @Override
        public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
            log.error("cache {} put error {}", cache.getName(), e.getMessage());
        }

        @Override
        public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
            log.error("cache {} evict error {}", cache.getName(), e.getMessage());
        }

        @Override
        public void handleCacheClearError(RuntimeException e, Cache cache) {
            log.error("cache {} clear error {}", cache.getName(), e.getMessage());
        }

        @Override
        public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
            log.error("cache {} handle error {}", cache.getName(), e.getMessage());
        }
    }
}