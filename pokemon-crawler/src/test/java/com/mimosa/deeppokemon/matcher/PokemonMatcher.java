/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.matcher;

import com.mimosa.deeppokemon.entity.Pokemon;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class PokemonMatcher extends TypeSafeMatcher<Pokemon> {
    public static PokemonMatcher POKEMON_MATCHER = new PokemonMatcher();

    @Override
    protected boolean matchesSafely(Pokemon pokemon) {
        return pokemon.getName() != null;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("pokemon with name.");

    }
}