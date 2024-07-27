/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat.dto;

import java.util.LinkedHashMap;

public record MonthlyMetaGameStatDto(LinkedHashMap<String, Double> tags) {
}