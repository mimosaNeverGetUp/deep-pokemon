/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.matcher;

import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.util.StringUtils;

public class PokemonStatMatcher extends TypeSafeMatcher<PokemonBattleStat> {
    public static PokemonStatMatcher PLAYER_STAT_MATCHER = new PokemonStatMatcher();

    @Override
    protected boolean matchesSafely(PokemonBattleStat pokemonBattleStat) {
        return StringUtils.hasText(pokemonBattleStat.getName());
    }

    @Override
    public void describeTo(Description description) {
    }
}