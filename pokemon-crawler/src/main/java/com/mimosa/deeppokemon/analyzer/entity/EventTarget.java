/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

public record EventTarget(int plyayerNumber, String pokemonName, String nickPokemonName) {
    public EventTarget withPokemonName(String pokemonName) {
        return new EventTarget(plyayerNumber, pokemonName, nickPokemonName);
    }
}