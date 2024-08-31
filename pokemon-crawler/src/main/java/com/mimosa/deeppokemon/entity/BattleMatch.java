/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record BattleMatch(String firstPlayer, String secondPlayer) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattleMatch that = (BattleMatch) o;
        boolean b1 = Objects.equals(firstPlayer, that.firstPlayer) && Objects.equals(secondPlayer,
                that.secondPlayer);
        boolean b2 = Objects.equals(firstPlayer, that.secondPlayer) && Objects.equals(secondPlayer,
                that.firstPlayer);
        return b1 || b2;
    }

    @Override
    public int hashCode() {
        List<String> players = new ArrayList<>();
        players.add(firstPlayer);
        players.add(secondPlayer);
        Collections.sort(players);
        return players.hashCode();
    }
}