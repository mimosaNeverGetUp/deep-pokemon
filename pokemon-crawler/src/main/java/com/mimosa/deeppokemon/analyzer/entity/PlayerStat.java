/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayerStat {
    private int playerNumber;
    private String playerName;

    private int switchCount;
    private int switchDamage;
    private int moveCount;
    private Map<String, PokemonBattleStat> pokemonBattleStats;

    public PlayerStat() {
        this.pokemonBattleStats = new HashMap<>();
        this.switchCount = 0;
        this.switchDamage = 0;
        this.moveCount = 0;
    }

    public PlayerStat(int playerNumber, String playerName) {
        this.playerNumber = playerNumber;
        this.playerName = playerName;
        this.pokemonBattleStats = new HashMap<>();
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public PokemonBattleStat getPokemonBattleStat(String pokemonName) {
        return pokemonBattleStats.get(pokemonName);
    }

    public Collection<PokemonBattleStat> getPokemonBattleStats() {
        return pokemonBattleStats.values();
    }

    public void addPokemonBattleStat(PokemonBattleStat pokemonBattleStat) {
        pokemonBattleStats.put(pokemonBattleStat.getName(), pokemonBattleStat);
    }

    public int getSwitchCount() {
        return switchCount;
    }

    public void setSwitchCount(int switchCount) {
        this.switchCount = switchCount;
    }

    public void setPokemonBattleStats(Map<String, PokemonBattleStat> pokemonBattleStats) {
        this.pokemonBattleStats = pokemonBattleStats;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public int getSwitchDamage() {
        return switchDamage;
    }

    public void setSwitchDamage(int switchDamage) {
        this.switchDamage = switchDamage;
    }
}