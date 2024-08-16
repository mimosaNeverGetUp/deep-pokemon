/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.deeppokemon.entity.stat.PokemonSet;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyMetaStat;
import com.mimosa.pokemon.portal.dto.MonthlyPokemonMoveSetDto;
import com.mimosa.pokemon.portal.dto.MonthlyPokemonUsageDto;
import com.mimosa.pokemon.portal.entity.PageResponse;
import com.mimosa.pokemon.portal.service.StatsService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/stats")
public class StatsApiController {
    private final StatsService statsService;

    public StatsApiController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("{format}/usage")
    public PageResponse<MonthlyPokemonUsageDto> usages(@PathVariable("format") @Pattern(regexp = "^[A-Za-z0-9]*$") String format,
                                                       @Min(0) int page, @Min(1) @Max(100) int row) {
        return statsService.queryUsage(format, page, row);
    }

    @GetMapping("{format}/meta")
    public MonthlyMetaStat meta(@PathVariable("format") @Pattern(regexp = "^[A-Za-z0-9]*$") String format) {
        return statsService.queryMeta(format);
    }

    @GetMapping("{format}/moveset/{pokemon}")
    public MonthlyPokemonMoveSetDto moveset(@PathVariable("format") @Pattern(regexp = "^[A-Za-z0-9]*$") String format,
                                            @PathVariable("pokemon") String pokmeon) {
        return statsService.queryMoveSet(format, pokmeon);
    }

    @GetMapping("{format}/set/{pokemon}")
    public PokemonSet set(@PathVariable("format") @Pattern(regexp = "^[A-Za-z0-9]*$") String format,
                          @PathVariable("pokemon") String pokmeon) {
        return statsService.queryPokemonSet(format, pokmeon);
    }
}