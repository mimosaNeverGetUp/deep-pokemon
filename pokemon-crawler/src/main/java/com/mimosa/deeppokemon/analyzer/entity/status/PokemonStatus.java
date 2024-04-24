/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.status;

public class PokemonStatus {
    private String pokemonName;
    private int health;

    public PokemonStatus(String pokemonName) {
        this.pokemonName = pokemonName;
        this.health = 100;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}