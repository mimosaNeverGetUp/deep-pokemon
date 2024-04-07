/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private static final Logger log = LoggerFactory.getLogger(RedisService.class);
    public static final String TEST_KEY = "test";

    private StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        if (!isAvailable()) {
            log.error("redis is not available");
        }
    }

    public boolean isAvailable() {
        if (redisTemplate == null) {
            return false;
        }
        try {
            redisTemplate.opsForValue().set(TEST_KEY, "test");
        } catch (Exception e) {
            log.error("redis get value fail", e);
            return false;
        }
        return true;
    }
}
