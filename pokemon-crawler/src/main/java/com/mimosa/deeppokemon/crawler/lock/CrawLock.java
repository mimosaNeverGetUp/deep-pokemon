/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.lock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class CrawLock {
    private static final Map<String, Semaphore> crawBattleLockMap = new ConcurrentHashMap<>();

    public static Semaphore getBattleLock(String id) {
        return crawBattleLockMap.computeIfAbsent(id, k -> new Semaphore(1));
    }

    public static boolean tryLock(String id) {
        return getBattleLock(id).tryAcquire();
    }

    public static void unlock(String id) {
        getBattleLock(id).release();
    }

    public static void unlock(Collection<String> ids) {
        for (String id : ids) {
            getBattleLock(id).release();
        }
    }
}