/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.util;

import com.mimosa.deeppokemon.analyzer.entity.*;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.entity.Battle;

import java.math.BigDecimal;
import java.util.List;

public class BattleContextBuilder {
    private final List<PlayerStatus> playerStatuses;
    private int turn = 0;
    private Weather weather;
    private Field field;
    private Battle battle;

    public BattleContextBuilder() {
        playerStatuses = List.of(new PlayerStatus(), new PlayerStatus());
    }

    public BattleContextBuilder addPokemon(int playerNumber, String pokemonName, String nickName) {
        PlayerStatus playerStatus = playerStatuses.get(playerNumber - 1);
        playerStatus.setPokemonStatus(pokemonName, new PokemonStatus(pokemonName));
        playerStatus.setPokemonNickName(nickName, pokemonName);
        return this;
    }

    public BattleContext build() {
        BattleContext battleContext = new BattleContext(playerStatuses, null);
        battleContext.setTurn(turn);
        battleContext.setWeather(weather);
        battleContext.setField(field);
        battleContext.setBattle(battle);
        return battleContext;}

    public BattleContextBuilder addSide(int playerNumber, Side side) {
        playerStatuses.get(playerNumber - 1).addSide(side);
        return this;
    }

    public BattleContextBuilder setTurnStartPokemon(int playerNumber, String pokemonName) {
        setTurnStartPokemon(1, playerNumber, pokemonName);
        return this;
    }


    public BattleContextBuilder setTurnStartPokemon(int turn, int playerNumber, String pokemonName) {
        playerStatuses.get(playerNumber - 1).setTurnStartPokemonName(turn, pokemonName);
        return this;
    }

    public BattleContextBuilder setActivePokemonName(int playerNumber, String pokemonName) {
        playerStatuses.get(playerNumber - 1).setActivePokemonName(pokemonName);
        return this;
    }

    public BattleContextBuilder setHealth(int playerNumber, String pokemonName, BigDecimal health) {
        playerStatuses.get(playerNumber - 1).getPokemonStatus(pokemonName).setHealth(health);
        return this;
    }

    public BattleContextBuilder setTurn(int turn) {
        this.turn = turn;
        return this;
    }

    public BattleContextBuilder setStatus(int playerNumber, String pokemonName, Status status) {
        playerStatuses.get(playerNumber - 1).getPokemonStatus(pokemonName).setStatus(status);
        return this;
    }

    public BattleContextBuilder setWeather(Weather weather) {
        this.weather = weather;
        return this;
    }

    public BattleContextBuilder setFiled(Field field) {
        this.field = field;
        return this;
    }

    public BattleContextBuilder setLastActivateTurn(int playerNumber, String pokemon, int lastMoveTurn) {
        playerStatuses.get(playerNumber - 1).getPokemonStatus(pokemon).setLastActivateTurn(lastMoveTurn);
        return this;
    }

    public BattleContextBuilder setBuffOf(int playerNumber, String pokemonName, String buff, EventTarget buffOf) {
        playerStatuses.get(playerNumber - 1).getPokemonStatus(pokemonName).setBuffOf(buff, buffOf);
        return this;
    }

    public BattleContextBuilder addActivateStatus(int playerNumber, String pokemon, ActivateStatus activateStatus) {
        playerStatuses.get(playerNumber - 1).getPokemonStatus(pokemon).addActivateStatus(activateStatus);
        return this;
    }

    public BattleContextBuilder setBattle(Battle battle) {
        this.battle = battle;
        return this;
    }
}