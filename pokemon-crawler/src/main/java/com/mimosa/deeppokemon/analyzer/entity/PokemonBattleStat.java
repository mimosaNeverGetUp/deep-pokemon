/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

public class PokemonBattleStat {
    private final String name;
    private int switchCount;
    private int moveCount;
    private int healthValue;
    private int attackValue;

    public PokemonBattleStat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getSwitchCount() {
        return switchCount;
    }

    public void setSwitchCount(int switchCount) {
        this.switchCount = switchCount;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public int getHealthValue() {
        return healthValue;
    }

    public void setHealthValue(int healthValue) {
        this.healthValue = healthValue;
    }

    public int getAttackValue() {
        return attackValue;
    }

    public void setAttackValue(int attackValue) {
        this.attackValue = attackValue;
    }
}