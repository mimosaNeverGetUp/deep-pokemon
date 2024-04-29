/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import java.util.ArrayList;
import java.util.List;

public record BattleStat(List<PlayerStat> playerStatList, List<TurnStat> turnStats) {
    public BattleStat(List<PlayerStat> playerStatList) {
        this(playerStatList, new ArrayList<>());
    }
}