/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import java.util.HashMap;
import java.util.Map;

public class TurnPlayerStat {
    private int totalHealth;
    private final Map<String, TurnPokemonStat> turnPokemonStatMap;

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

    public int getTotalHealth() {
        return totalHealth;
    }

    public void setTotalHealth(int totalHealth) {
        this.totalHealth = totalHealth;
    }
}