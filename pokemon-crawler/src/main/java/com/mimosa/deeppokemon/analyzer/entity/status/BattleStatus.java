/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.status;

import java.util.List;

public class BattleStatus {
    private List<PlayerStatus> playerStatusList;
    private int turn;

    public BattleStatus(List<PlayerStatus> playerStatusList) {
        this.playerStatusList = playerStatusList;
        this.turn = 0;
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
}