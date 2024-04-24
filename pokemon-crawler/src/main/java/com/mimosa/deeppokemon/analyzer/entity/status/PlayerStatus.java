/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.status;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatus {
    private final Map<String, String> pokemonNickNameMap;
    private final Map<String, PokemonStatus> pokemonStatusMap;
    private final Map<String, String> sideSetterMap;
    private String turnStartPokemonName;
    private String activePokemonName;

    public PlayerStatus() {
        this.pokemonNickNameMap = new HashMap<>();
        this.pokemonStatusMap = new HashMap<>();
        this.sideSetterMap = new HashMap<>();
        this.activePokemonName = null;
        this.turnStartPokemonName = null;
    }

    public String getPokemonName(String nickName) {
        return pokemonNickNameMap.get(nickName);
    }

    public void setPokemonNickNameMap(String nickName, String pokemonName) {
        pokemonNickNameMap.put(nickName, pokemonName);
    }

    public String getActivePokemonName() {
        return activePokemonName;
    }

    public void setActivePokemonName(String activePokemonName) {
        this.activePokemonName = activePokemonName;
    }

    public PokemonStatus getPokemonStatus(String pokemonName) {
        return pokemonStatusMap.get(pokemonName);
    }

    public void setPokemonStatus(String pokemonName, PokemonStatus pokemonStatus) {
        pokemonStatusMap.put(pokemonName,pokemonStatus);
    }

    public String getTurnStartPokemonName() {
        return turnStartPokemonName;
    }

    public void setTurnStartPokemonName(String turnStartPokemonName) {
        this.turnStartPokemonName = turnStartPokemonName;
    }

    public String getSideSetter(String sideName) {
        return sideSetterMap.get(sideName);
    }
}