/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Document(collection = "battle_stat")
public final class BattleStat {
    @MongoId
    private final String battleId;
    private final List<PlayerStat> playerStatList;
    private final List<TurnStat> turnStats;

    @JsonCreator
    public BattleStat(@JsonProperty("battleId") String battleId,
                      @JsonProperty("playerStatList") List<PlayerStat> playerStatList,
                      @JsonProperty("turnStats") List<TurnStat> turnStats) {
        this.battleId = battleId;
        this.playerStatList = playerStatList;
        this.turnStats = turnStats;
    }

    @JsonProperty("battleId")
    public String battleId() {
        return battleId;
    }

    @JsonProperty("playerStatList")
    public List<PlayerStat> playerStatList() {
        return playerStatList;
    }

    @JsonProperty("turnStats")
    public List<TurnStat> turnStats() {
        return turnStats;
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
        return  Objects.equals(battleId, that.battleId)
                && Objects.equals(turnStats, that.turnStats)
                && Objects.equals(playerStatList, that.playerStatList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(battleId, playerStatList, turnStats);
    }

    @Override
    public String toString() {
        return "BattleStat[" +
                "playerStatList=" + playerStatList + ", " +
                "turnStats=" + turnStats + ']';
    }
}