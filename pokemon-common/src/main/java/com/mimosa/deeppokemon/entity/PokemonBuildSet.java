/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import java.io.Serializable;
import java.util.List;

public record PokemonBuildSet(String name, List<String> moves, List<String> abilities, List<String> items,
                              List<String> teraTypes) implements Serializable {
}