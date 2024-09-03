/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.LinkedHashMap;
import java.util.List;

public record MonthlyPokemonStatDto(LeadStat lead, Usage usage, int count, double weight, List<Integer> viability,
                                    LinkedHashMap<String, Double> abilities, LinkedHashMap<String, Double> items,
                                    LinkedHashMap<String, Double> spreads, LinkedHashMap<String, Double> moves,
                                    @JsonAlias({"teraTypes", "teraType", "teratypes", "Tera Types", "Tera Type",
                                            "teras", "tera"}) LinkedHashMap<String, Double> teraTypes,
                                    LinkedHashMap<String, Double> teammates, LinkedHashMap<String, Double> happinesses,
                                    LinkedHashMap<String, List<Double>> counters) {
}