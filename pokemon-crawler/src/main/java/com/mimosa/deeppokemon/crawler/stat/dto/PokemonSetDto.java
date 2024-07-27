/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat.dto;

import java.util.List;

public record PokemonSetDto(List<Object> moves, Object ability, Object item, Object nature, Object teratypes,
                            Object evs) {
}