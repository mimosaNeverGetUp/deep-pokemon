/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat.dto;

import java.util.Map;

public record PokemonAnalyzeDto(boolean outdated, Map<String, PokemonSetAnalyzeDto> sets) {
}