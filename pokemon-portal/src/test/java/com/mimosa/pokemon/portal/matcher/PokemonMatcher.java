/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.matcher;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.List;

public class PokemonMatcher {

    public static final String NAME = "name";
    public static final String MOVES = "moves";
    public static final String ITEM = "item";
    public static final String ABLITY = "ability";
    private static final Matcher<?> VALID_POKEMON_MATCHER = Matchers.allOf(
            Matchers.hasEntry(Matchers.equalTo(NAME), Matchers.notNullValue()),
            Matchers.hasEntry(Matchers.equalTo(MOVES), Matchers.instanceOf(List.class)),
            Matchers.hasKey(ITEM),
            Matchers.hasKey(ABLITY)
    );

    public static Matcher<?> isValidPokemon() {
        return VALID_POKEMON_MATCHER;
    }

    public static Matcher<?> isPokemon(String pokemonName) {
        return Matchers.hasEntry(NAME, pokemonName);
    }
}