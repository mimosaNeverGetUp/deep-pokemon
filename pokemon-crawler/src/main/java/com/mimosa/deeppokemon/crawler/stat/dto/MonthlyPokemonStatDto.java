/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat.dto;

import java.util.LinkedHashMap;
import java.util.List;

public record MonthlyPokemonStatDto(LeadStat lead, Usage usage, int count, double weight, List<Integer> viability,
                                    LinkedHashMap<String, Double> abilities, LinkedHashMap<String, Double> items,
                                    LinkedHashMap<String, Double> spreads, LinkedHashMap<String, Double> moves,
                                    LinkedHashMap<String, Double> teammates, LinkedHashMap<String, Double> happinesses,
                                    LinkedHashMap<String, List<Double>> counters) {
}