/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "battle_stat")
public record BattleStat(List<PlayerStat> playerStatList, List<TurnStat> turnStats) {
    public BattleStat(List<PlayerStat> playerStatList) {
        this(playerStatList, new ArrayList<>());
    }
}