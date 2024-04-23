/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatus {
    private final Map<String, String> pokemonNickNameMap;

    public PlayerStatus() {
        this.pokemonNickNameMap = new HashMap<>();
    }

    public String getPokemonName(String nickName) {
        return pokemonNickNameMap.get(nickName);
    }

    public void  setPokemonNickNameMap(String nickName, String pokemonName) {
        pokemonNickNameMap.put(nickName, pokemonName);
    }
}