/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TurnStat {
    private int turn;
    private List<TurnPlayerStat> turnPlayerStatList;

    public TurnStat(int turn) {
        this(turn, new ArrayList<>());
    }

    public TurnStat(List<TurnPlayerStat> turnPlayerStatList) {
        this(0, turnPlayerStatList);
    }

    @JsonCreator
    @PersistenceCreator
    public TurnStat(@JsonProperty("turn") int turn, @JsonProperty("turnPlayerStatList") List<TurnPlayerStat> turnPlayerStatList) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TurnStat turnStat = (TurnStat) o;
        return turn == turnStat.turn && Objects.equals(turnPlayerStatList, turnStat.turnPlayerStatList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, turnPlayerStatList);
    }
}