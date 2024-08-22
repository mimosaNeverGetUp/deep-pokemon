/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    @CacheEvict(value = {"rank", "playerRank"}, allEntries = true)
    public void clearRank() {
        // execute annotation
    }

    @CacheEvict(value = "playerBattle", allEntries = true)
    public void clearPlayerBattle() {
        // execute annotation
    }

    @CacheEvict(value = "teamGroup", allEntries = true)
    public void clearTeamGroup() {
        // execute annotation
    }

    @CacheEvict(value = {"rank", "playerRank", "playerBattle", "teamGroup"}, allEntries = true)
    public void clearAll() {
        // execute annotation
    }
}