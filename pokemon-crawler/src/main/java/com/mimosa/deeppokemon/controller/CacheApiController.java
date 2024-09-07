/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.service.CacheService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache")
public class CacheApiController {
    private final CacheService cacheService;

    public CacheApiController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostMapping("/battle/delete")
    public boolean deleteRankAndBattle() {
        cacheService.clearRank();
        cacheService.clearPlayerBattle();
        return true;
    }

    @PostMapping("/team/delete")
    public boolean deleteTeamGroup() {
        cacheService.clearTeam();
        return true;
    }

    @PostMapping("/stat/delete")
    public boolean deleteStat() {
        cacheService.clearMonthlyStat();
        return true;
    }

    @PostMapping("/tour/delete")
    public boolean deleteTour() {
        cacheService.clearTour();
        return true;
    }

    @PostMapping("/deleteAll")
    public boolean deleteAll() {
        cacheService.clearAll();
        return true;
    }
}