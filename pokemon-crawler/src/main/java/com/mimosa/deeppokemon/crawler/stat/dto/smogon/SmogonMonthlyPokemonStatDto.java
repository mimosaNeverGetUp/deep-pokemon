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
                                          @JsonProperty("Items") LinkedHashMap<String, Double> items,
                                          @JsonProperty("Spreads") LinkedHashMap<String, Double> spreads,
                                          @JsonProperty("Moves") LinkedHashMap<String, Double> moves,
                                          @JsonProperty("Tera Types") LinkedHashMap<String, Double> teraTypes,
                                          @JsonProperty("Teammates") LinkedHashMap<String, Double> teammates,
                                          @JsonProperty("Happiness") LinkedHashMap<String, Double> happinesses) {
}