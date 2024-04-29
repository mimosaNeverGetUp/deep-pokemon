/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.util;

import com.mimosa.deeppokemon.analyzer.entity.Side;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;

import java.util.List;

public class BattleStatusBuilder {
    private final List<PlayerStatus> playerStatuses;

    public BattleStatusBuilder() {
        playerStatuses = List.of(new PlayerStatus(), new PlayerStatus());
    }

    public BattleStatusBuilder addPokemon(int playerNumber, String pokemonName, String nickName) {
        PlayerStatus playerStatus = playerStatuses.get(playerNumber - 1);
        playerStatus.setPokemonStatus(pokemonName, new PokemonStatus(pokemonName));
        playerStatus.setPokemonNickNameMap(nickName, pokemonName);
        return this;
    }

    public BattleStatus build() {return new BattleStatus(playerStatuses);}

    public BattleStatusBuilder addSide(int playerNumber, Side side) {
        playerStatuses.get(playerNumber - 1).addSide(side);
        return this;
    }

    public BattleStatusBuilder setTurnStartPokemon(int playerNumber, String pokemonName) {
        playerStatuses.get(playerNumber - 1).setTurnStartPokemonName(pokemonName);
        return this;
    }

    public BattleStatusBuilder setActivePokemonName(int playerNumber, String pokemonName) {
        playerStatuses.get(playerNumber - 1).setActivePokemonName(pokemonName);
        return this;
    }

    public BattleStatusBuilder setHealth(int playerNumber, String pokemonName, int health) {
        playerStatuses.get(playerNumber - 1).getPokemonStatus(pokemonName).setHealth(health);
        return this;
    }
}