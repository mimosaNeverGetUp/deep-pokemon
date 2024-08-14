/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.status;

import com.mimosa.deeppokemon.analyzer.entity.Field;
import com.mimosa.deeppokemon.analyzer.entity.Weather;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Pokemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BattleContext {
    private static final Logger log = LoggerFactory.getLogger(BattleContext.class);

    private List<PlayerStatus> playerStatusList;
    private int turn;
    private Weather weather;
    private Field field;
    private Battle battle;

    public BattleContext(List<PlayerStatus> playerStatusList, Battle battle) {
        this.playerStatusList = playerStatusList;
        this.turn = 0;
        this.weather = null;
        this.field = null;
        this.battle = battle;
    }

    public List<PlayerStatus> getPlayerStatusList() {
        return playerStatusList;
    }

    public void setPlayerStatusList(List<PlayerStatus> playerStatusList) {
        this.playerStatusList = playerStatusList;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public void changePokemonName(int playerNumber, String nameBefore, String changeName) {
        playerStatusList.get(playerNumber - 1).changePokemonName(nameBefore, changeName);
    }

    public void setPokemonItem(int playerNumber, String pokemonName, String item) {
        if (battle == null || battle.getTeams() == null || battle.getTeams().length < playerNumber
                || battle.getTeams()[playerNumber - 1] == null) {
            log.warn("battle {} is invalid", battle);
            return;
        }

        Pokemon pokemon = battle.getTeams()[playerNumber - 1].getPokemon(pokemonName);
        if (pokemon == null) {
            log.warn("pokemon {} is not found in battle", pokemonName);
            return;
        }

        if (pokemon.getItem() == null) {
            pokemon.setItem(item);
        }
    }

    public void setPokemonTeraType(int playerNumber, String pokemonName, String teraType) {
        if (battle == null || battle.getTeams() == null || battle.getTeams().length < playerNumber
                || battle.getTeams()[playerNumber - 1] == null) {
            log.warn("battle {} is invalid", battle);
            return;
        }

        Pokemon pokemon = battle.getTeams()[playerNumber - 1].getPokemon(pokemonName);
        if (pokemon == null) {
            log.warn("pokemon {} is not found in battle", pokemonName);
            return;
        }
        pokemon.setTeraType(teraType);
    }
}