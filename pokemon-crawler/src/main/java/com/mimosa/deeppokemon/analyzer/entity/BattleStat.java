/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Document(collection = "battle_stat")
public final class BattleStat {
    private final List<PlayerStat> playerStatList;
    private final List<TurnStat> turnStats;

    @JsonCreator
    public BattleStat(@JsonProperty("playerStatList") List<PlayerStat> playerStatList,
                      @JsonProperty("turnStats") List<TurnStat> turnStats) {
        this.playerStatList = playerStatList;
        this.turnStats = turnStats;
    }

    public BattleStat(List<PlayerStat> playerStatList) {
        this(playerStatList, new ArrayList<>());
    }

    public void changePokemonName(int playerNumber, String blurPokemonName, String switchPokemonName) {
        playerStatList.get(playerNumber - 1).changePokemonName(blurPokemonName, switchPokemonName);
        for (TurnStat turnStat : turnStats) {
            TurnPlayerStat turnPlayerStat = turnStat.getTurnPlayerStatList().get(playerNumber - 1);
            Map<String, TurnPokemonStat> turnPokemonStatMap = turnPlayerStat.getTurnPokemonStatMap();
            turnPokemonStatMap.put(switchPokemonName, turnPlayerStat.getTurnPokemonStat(blurPokemonName));
            turnPokemonStatMap.remove(blurPokemonName);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattleStat that = (BattleStat) o;
        return Objects.equals(turnStats, that.turnStats) && Objects.equals(playerStatList, that.playerStatList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerStatList, turnStats);
    }

    public List<PlayerStat> playerStatList() {
        return playerStatList;
    }

    public List<TurnStat> turnStats() {
        return turnStats;
    }

    @Override
    public String toString() {
        return "BattleStat[" +
                "playerStatList=" + playerStatList + ", " +
                "turnStats=" + turnStats + ']';
    }
}