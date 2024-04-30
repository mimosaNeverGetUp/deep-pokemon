/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.status;

import com.mimosa.deeppokemon.analyzer.entity.Weather;

import java.util.List;

public class BattleStatus {
    private List<PlayerStatus> playerStatusList;
    private int turn;
    private Weather weather;

    public BattleStatus(List<PlayerStatus> playerStatusList) {
        this.playerStatusList = playerStatusList;
        this.turn = 0;
        this.weather = null;
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
}