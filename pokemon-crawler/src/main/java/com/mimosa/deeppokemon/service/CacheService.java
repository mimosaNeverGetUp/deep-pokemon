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

    @CacheEvict(value = {"teamGroup"}, allEntries = true)
    public void clearTeam() {
        // execute annotation
    }

    @CacheEvict(value = {"rank", "playerRank", "playerBattle", "teamGroup", "monthlyUsage", "monthlyMeta",
            "monthlyMoveSet", "monthlyPokemonSet", "tours"}, allEntries = true)
    public void clearAll() {
        // execute annotation
    }

    @CacheEvict(value = {"monthlyUsage", "monthlyMeta", "monthlyMoveSet", "monthlyPokemonSet"}, allEntries =
            true)
    public void clearMonthlyStat() {
        // execute annotation
    }

    @CacheEvict(value = {"tours"}, allEntries = true)
    public void clearTour() {
        // execute annotation
    }
}