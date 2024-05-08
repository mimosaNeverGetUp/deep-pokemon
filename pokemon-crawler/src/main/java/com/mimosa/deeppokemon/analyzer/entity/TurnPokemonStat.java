/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class TurnPokemonStat {
    private String pokemonName;
    private BigDecimal health;

    public TurnPokemonStat() {

    }

    public TurnPokemonStat(String pokemonName) {
        this.pokemonName = pokemonName;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public void setPokemonName(String pokemonName) {
        this.pokemonName = pokemonName;
    }

    public BigDecimal getHealth() {
        return health;
    }

    public void setHealth(BigDecimal health) {
        this.health = health;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TurnPokemonStat that = (TurnPokemonStat) o;
        return health.compareTo(that.health) == 0 && Objects.equals(pokemonName, that.pokemonName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pokemonName, health);
    }
}