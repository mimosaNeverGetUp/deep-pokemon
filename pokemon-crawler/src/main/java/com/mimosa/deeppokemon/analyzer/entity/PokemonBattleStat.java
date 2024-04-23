/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

public class PokemonBattleStat {
    private final String name;
    private int switchCount;

    public PokemonBattleStat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getSwitchCount() {
        return switchCount;
    }

    public void setSwitchCount(int switchCount) {
        this.switchCount = switchCount;
    }
}