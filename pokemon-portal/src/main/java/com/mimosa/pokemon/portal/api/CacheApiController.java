/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.pokemon.portal.service.CacheService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache")
public class CacheApiController {
    private final CacheService cacheService;

    public CacheApiController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/load")
    public boolean loadHeatData() {
        return cacheService.loadHeatData();
    }
}