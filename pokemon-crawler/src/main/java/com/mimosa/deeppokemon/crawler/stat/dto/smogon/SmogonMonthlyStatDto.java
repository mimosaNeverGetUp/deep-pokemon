/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat.dto.smogon;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record SmogonMonthlyStatDto(SmogonMonthlyStatInfoDto info,
                                   @JsonProperty("data") Map<String, SmogonMonthlyPokemonStatDto> pokemon) {
}