/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.status;

import com.mimosa.deeppokemon.analyzer.entity.Status;

import java.math.BigDecimal;

public class PokemonStatus {
    private String pokemonName;
    private BigDecimal health;
    private Status status;
    private int lastActivateTurn;

    public PokemonStatus(String pokemonName) {
        this.pokemonName = pokemonName;
        this.health = BigDecimal.valueOf(100.0);
        this.lastActivateTurn = 0;
        this.status = null;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public BigDecimal getHealth() {
        return health;
    }

    public void setHealth(BigDecimal health) {
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

    public int getLastActivateTurn() {
        return lastActivateTurn;
    }

    public void setLastActivateTurn(int lastActivateTurn) {
        this.lastActivateTurn = lastActivateTurn;
    }
}