/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TurnPlayerStat {
    private int totalHealth;
    private Map<String, TurnPokemonStat> turnPokemonStatMap;

    public TurnPlayerStat() {
        turnPokemonStatMap = new HashMap<>();
    }

    public void addTurnPokemonStat(TurnPokemonStat turnPokemonStat) {
        turnPokemonStatMap.put(turnPokemonStat.getPokemonName(), turnPokemonStat);
    }

    public TurnPokemonStat getTurnPokemonStat(String pokemonName) {
        return turnPokemonStatMap.get(pokemonName);
    }

    public Map<String, TurnPokemonStat> getTurnPokemonStatMap() {
        return turnPokemonStatMap;
    }

    public void setTurnPokemonStatMap(Map<String, TurnPokemonStat> turnPokemonStatMap) {
        this.turnPokemonStatMap = turnPokemonStatMap;
    }

    public int getTotalHealth() {
        return totalHealth;
    }

    public void setTotalHealth(int totalHealth) {
        this.totalHealth = totalHealth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TurnPlayerStat that = (TurnPlayerStat) o;
        return totalHealth == that.totalHealth && Objects.equals(turnPokemonStatMap, that.turnPokemonStatMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalHealth, turnPokemonStatMap);
    }
}