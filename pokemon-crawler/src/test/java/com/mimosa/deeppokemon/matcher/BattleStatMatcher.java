/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.matcher;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class BattleStatMatcher extends TypeSafeMatcher<BattleStat> {
    @Override
    protected boolean matchesSafely(BattleStat battleStat) {
        return false;
    }

    @Override
    public void describeTo(Description description) {

    }
}