/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import java.util.Objects;

public class PokemonBattleStat {
    private String name;
    private int switchCount;
    private int moveCount;
    private int killCount;
    private int healthValue;
    private int attackValue;

    public PokemonBattleStat() {

    }

    public PokemonBattleStat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getKillCount() {
        return killCount;
    }

    public void setKillCount(int killCount) {
        this.killCount = killCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokemonBattleStat that = (PokemonBattleStat) o;
        return switchCount == that.switchCount && moveCount == that.moveCount && killCount == that.killCount && healthValue == that.healthValue && attackValue == that.attackValue && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, switchCount, moveCount, killCount, healthValue, attackValue);
    }
}