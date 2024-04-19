/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CrawLock {
    private static Map<String, Lock> crawBattleLockMap =new HashMap<>();

    public static Lock getBattleLock(String id) {
        return crawBattleLockMap.computeIfAbsent(id, k -> new ReentrantLock());
    }
}