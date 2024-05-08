/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.matcher;

import com.mimosa.deeppokemon.analyzer.entity.TurnStat;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class TurnStatMatcher extends TypeSafeMatcher<TurnStat> {
    public static TurnStatMatcher TURN_STAT_MATCHER = new TurnStatMatcher();

    @Override
    protected boolean matchesSafely(TurnStat turnStat) {
        if (turnStat.getTurn() == 0) {
            return false;
        }
        if (turnStat.getTurnPlayerStatList().stream().noneMatch(
                turnPlayerStat -> turnPlayerStat.getTotalHealth().intValue() != 0)) {
            return false;
        }

        return turnStat.getTurnPlayerStatList().stream().allMatch(TurnPlayerStatMatcher.TURN_PLAYER_STAT_MATCHER::matches);
    }

    @Override
    public void describeTo(Description description) {
    }
}