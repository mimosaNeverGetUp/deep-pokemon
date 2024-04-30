/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.status;

import com.mimosa.deeppokemon.analyzer.entity.Status;

public class PokemonStatus {
    private String pokemonName;
    private int health;
    private Status status;

    public PokemonStatus(String pokemonName) {
        this.pokemonName = pokemonName;
        this.status = null;
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

    public void setPokemonName(String pokemonName) {
        this.pokemonName = pokemonName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}