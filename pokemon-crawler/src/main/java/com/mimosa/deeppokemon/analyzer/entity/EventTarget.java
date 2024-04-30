/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

import java.util.Objects;

public record EventTarget(int playerNumber, String targetName, String nickName) {
    public EventTarget withTargetName(String targetName) {
        return new EventTarget(playerNumber, targetName, nickName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventTarget that = (EventTarget) o;
        return playerNumber == that.playerNumber && Objects.equals(targetName, that.targetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerNumber, targetName);
    }

    @Override
    public String toString() {
        return String.format("p%d %s", playerNumber, targetName);
    }
}