/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.status;

import com.mimosa.deeppokemon.analyzer.entity.Field;
import com.mimosa.deeppokemon.analyzer.entity.Weather;

import java.util.List;

public class BattleStatus {
    private List<PlayerStatus> playerStatusList;
    private int turn;
    private Weather weather;
    private Field field;

    public BattleStatus(List<PlayerStatus> playerStatusList) {
        this.playerStatusList = playerStatusList;
        this.turn = 0;
        this.weather = null;
        this.field = null;
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

    public void changePokemonName(int playerNumber, String nameBefore, String changeName) {
        playerStatusList.get(playerNumber - 1).changePokemonName(nameBefore, changeName);
    }
}