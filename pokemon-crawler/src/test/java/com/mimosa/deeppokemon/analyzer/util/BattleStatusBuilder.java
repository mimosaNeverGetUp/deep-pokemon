/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.util;

import com.mimosa.deeppokemon.analyzer.entity.Field;
import com.mimosa.deeppokemon.analyzer.entity.Side;
import com.mimosa.deeppokemon.analyzer.entity.Status;
import com.mimosa.deeppokemon.analyzer.entity.Weather;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;

import java.util.List;

public class BattleStatusBuilder {
    private final List<PlayerStatus> playerStatuses;
    private int turn = 0;
    private Weather weather;
    private Field field;

    public BattleStatusBuilder() {
        playerStatuses = List.of(new PlayerStatus(), new PlayerStatus());
    }

    public BattleStatusBuilder addPokemon(int playerNumber, String pokemonName, String nickName) {
        PlayerStatus playerStatus = playerStatuses.get(playerNumber - 1);
        playerStatus.setPokemonStatus(pokemonName, new PokemonStatus(pokemonName));
        playerStatus.setPokemonNickNameMap(nickName, pokemonName);
        return this;
    }

    public BattleStatus build() {
        BattleStatus battleStatus = new BattleStatus(playerStatuses);
        battleStatus.setTurn(turn);
        battleStatus.setWeather(weather);
        battleStatus.setField(field);
        return battleStatus;}

    public BattleStatusBuilder addSide(int playerNumber, Side side) {
        playerStatuses.get(playerNumber - 1).addSide(side);
        return this;
    }

    public BattleStatusBuilder setTurnStartPokemon(int playerNumber, String pokemonName) {
        setTurnStartPokemon(1, playerNumber, pokemonName);
        return this;
    }


    public BattleStatusBuilder setTurnStartPokemon(int turn, int playerNumber, String pokemonName) {
        playerStatuses.get(playerNumber - 1).setTurnStartPokemonName(turn, pokemonName);
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

    public BattleStatusBuilder setTurn(int turn) {
        this.turn = turn;
        return this;
    }

    public BattleStatusBuilder setStatus(int playerNumber, String pokemonName, Status status) {
        playerStatuses.get(playerNumber - 1).getPokemonStatus(pokemonName).setStatus(status);
        return this;
    }

    public BattleStatusBuilder setWeather(Weather weather) {
        this.weather = weather;
        return this;
    }

    public BattleStatusBuilder setFiled(Field field) {
        this.field = field;
        return this;
    }

    public BattleStatusBuilder setLastMoveTurn(int playerNumber, String pokemon, int lastMoveTurn) {
        playerStatuses.get(playerNumber - 1).getPokemonStatus(pokemon).setLastMoveTurn(lastMoveTurn);
        return this;
    }
}