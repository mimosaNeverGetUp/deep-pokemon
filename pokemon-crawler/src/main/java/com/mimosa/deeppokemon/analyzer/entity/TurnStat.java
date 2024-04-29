/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import java.util.ArrayList;
import java.util.List;

public class TurnStat {
    private int turn;
    private List<TurnPlayerStat> turnPlayerStatList;

    public TurnStat(int turn) {
        this(turn, new ArrayList<>());
    }

    public TurnStat(List<TurnPlayerStat> turnPlayerStatList) {
        this(0, turnPlayerStatList);
    }

    public TurnStat(int turn, List<TurnPlayerStat> turnPlayerStatList) {
        this.turn = turn;
        this.turnPlayerStatList = turnPlayerStatList;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<TurnPlayerStat> getTurnPlayerStatList() {
        return turnPlayerStatList;
    }

    public void setTurnPlayerStatList(List<TurnPlayerStat> turnPlayerStatList) {
        this.turnPlayerStatList = turnPlayerStatList;
    }
}