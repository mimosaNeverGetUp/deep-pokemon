/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.matcher;

import com.mimosa.deeppokemon.entity.stat.TurnPokemonStat;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.util.StringUtils;

public class TurnPokemonStatMatcher extends TypeSafeMatcher<TurnPokemonStat> {
    public static final TurnPokemonStatMatcher TURN_STAT_MATCHER = new TurnPokemonStatMatcher();

    @Override
    protected boolean matchesSafely(TurnPokemonStat turnPokemonStat) {
        return StringUtils.hasText(turnPokemonStat.getPokemonName());
    }

    @Override
    public void describeTo(Description description) {

    }
}