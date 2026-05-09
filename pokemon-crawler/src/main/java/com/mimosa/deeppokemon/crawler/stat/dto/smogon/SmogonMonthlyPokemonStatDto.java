/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat.dto.smogon;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

public record SmogonMonthlyPokemonStatDto(
                                          Double usage, @JsonProperty("Raw count") Integer rawCount,
                                          @JsonProperty("Abilities") LinkedHashMap<String, BigDecimal> abilities,
                                          @JsonProperty("Items") LinkedHashMap<String, BigDecimal> items,
                                          @JsonProperty("Spreads") LinkedHashMap<String, BigDecimal> spreads,
                                          @JsonProperty("Moves") LinkedHashMap<String, BigDecimal> moves,
                                          @JsonProperty("Tera Types") LinkedHashMap<String, BigDecimal> teraTypes,
                                          @JsonProperty("Happiness") LinkedHashMap<String, BigDecimal> happinesses) {
}