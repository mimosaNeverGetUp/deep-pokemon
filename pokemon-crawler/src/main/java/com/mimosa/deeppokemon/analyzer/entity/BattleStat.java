/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = "battle_stat")
public record BattleStat(List<PlayerStat> playerStatList, List<TurnStat> turnStats) {
    public BattleStat(List<PlayerStat> playerStatList) {
        this(playerStatList, new ArrayList<>());
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
}