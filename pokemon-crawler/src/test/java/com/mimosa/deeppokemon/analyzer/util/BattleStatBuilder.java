/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.util;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.PlayerStat;
import com.mimosa.deeppokemon.analyzer.entity.PokemonBattleStat;

import java.util.ArrayList;
import java.util.List;

public class BattleStatBuilder {
    List<PlayerStat> playerStats;

    public BattleStatBuilder() {
        playerStats = List.of(new PlayerStat(1, ""), new PlayerStat(2, ""));
    }

    public BattleStatBuilder addPokemonStat(int playerNumber, String pokemonName) {
        playerStats.get(playerNumber - 1).addPokemonBattleStat(new PokemonBattleStat(pokemonName));
        return this;
    }

    public BattleStat build() {
        return new BattleStat(null, playerStats, new ArrayList<>());}
}