/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.*;

public class PlayerStat {
    private int playerNumber;
    private String playerName;

    private int switchCount;
    private BigDecimal switchDamage;
    private int moveCount;
    private Map<String, PokemonBattleStat> pokemonBattleStats;
    private List<BattleHighLight> highLights;

    public PlayerStat() {
    }

    public PlayerStat(int playerNumber, String playerName) {
        this.playerNumber = playerNumber;
        this.playerName = playerName;
        this.switchCount = 0;
        this.switchDamage = BigDecimal.valueOf(0.0);
        this.moveCount = 0;
        this.pokemonBattleStats = new HashMap<>();
        this.highLights = new ArrayList<>();
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

    @JsonProperty("pokemonBattleStats")
    public Map<String, PokemonBattleStat> getPokemonBattleStatsMap() {
        return pokemonBattleStats;
    }

    @JsonProperty("pokemonBattleStats")
    public void setPokemonBattleStatsMap(Map<String, PokemonBattleStat> pokemonBattleStats) {
        this.pokemonBattleStats = pokemonBattleStats;
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

    public BigDecimal getSwitchDamage() {
        return switchDamage;
    }

    public void setSwitchDamage(BigDecimal switchDamage) {
        this.switchDamage = switchDamage;
    }

    public List<BattleHighLight> getHighLights() {
        return highLights;
    }

    public void setHighLights(List<BattleHighLight> highLights) {
        this.highLights = highLights;
    }

    public void addHighLight(BattleHighLight highLight) {
        highLights.add(highLight);
    }


    public void changePokemonName(String nameBefore, String changeName) {
        PokemonBattleStat pokemonBattleStat = pokemonBattleStats.remove(nameBefore);
        pokemonBattleStat.setName(changeName);
        pokemonBattleStats.put(changeName, pokemonBattleStat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerStat that = (PlayerStat) o;
        return playerNumber == that.playerNumber && switchCount == that.switchCount
                && switchDamage.compareTo(that.switchDamage) == 0 && moveCount == that.moveCount
                && Objects.equals(playerName, that.playerName)
                && Objects.equals(pokemonBattleStats, that.pokemonBattleStats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerNumber, playerName, switchCount, switchDamage, moveCount, pokemonBattleStats);
    }
}