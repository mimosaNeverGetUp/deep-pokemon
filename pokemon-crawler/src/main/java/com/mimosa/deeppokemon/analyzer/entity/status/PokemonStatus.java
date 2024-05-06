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
    private int lastMoveTurn;

    public PokemonStatus(String pokemonName) {
        this.pokemonName = pokemonName;
        this.health = 100;
        this.lastMoveTurn = 0;
        this.status = null;
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

    public int getLastMoveTurn() {
        return lastMoveTurn;
    }

    public void setLastMoveTurn(int lastMoveTurn) {
        this.lastMoveTurn = lastMoveTurn;
    }
}