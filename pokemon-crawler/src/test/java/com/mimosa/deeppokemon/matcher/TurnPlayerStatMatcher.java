/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.matcher;

import com.mimosa.deeppokemon.analyzer.entity.TurnPlayerStat;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class TurnPlayerStatMatcher extends TypeSafeMatcher<TurnPlayerStat> {
    public static TurnPlayerStatMatcher TURN_PLAYER_STAT_MATCHER = new TurnPlayerStatMatcher();

    @Override
    protected boolean matchesSafely(TurnPlayerStat turnPlayerStat) {
        if (turnPlayerStat.getTurnPokemonStatMap().isEmpty()) {
            return false;
        }

        return turnPlayerStat.getTurnPokemonStatMap().values().stream()
                .allMatch(TurnPokemonStatMatcher.TURN_STAT_MATCHER::matches);
    }

    @Override
    public void describeTo(Description description) {
    }
}