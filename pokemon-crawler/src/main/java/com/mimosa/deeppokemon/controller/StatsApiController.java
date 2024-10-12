/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.service.StatsService;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/stats")
public class StatsApiController {
    private final StatsService statsService;

    public StatsApiController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/craw")
    public boolean crawMonthlyStats(@RequestParam("format") @Pattern(regexp = "^[A-Za-z0-9]*$") String format) {
        return statsService.craw(format);
    }

    @GetMapping("/analyzes/craw")
    public Map<String, String> crawAnalyze(@RequestParam("format") String format,
                                           @RequestParam(value = "pokemon", required = false) String specifyPokemonName,
                                           @RequestParam(value = "overwrite", required = false, defaultValue = "false") boolean overwrite) {
        Map<String, String> map = new HashMap<>();
        map.put("insertSize", Integer.toString(statsService.crawPokemonAnalyzes(format, specifyPokemonName, overwrite)));
        map.put("result", "success");
        return map;
    }
}