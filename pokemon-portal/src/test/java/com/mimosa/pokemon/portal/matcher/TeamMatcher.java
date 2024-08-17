/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.matcher;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamMatcher {

    public static final String PLAYER_NAME = "playerName";
    public static final String TIER = "tier";
    public static final String TAG_SET = "tagSet";
    public static final String POKEMONS = "pokemons";

    private static final Matcher<?> VALID_TEAM_MAP_MATCHER = Matchers.allOf(
            Matchers.hasKey(PLAYER_NAME),
            Matchers.hasKey(TIER),
            Matchers.hasEntry(Matchers.equalTo(TAG_SET), Matchers.instanceOf(List.class)),
            Matchers.hasEntry(Matchers.equalTo(POKEMONS), Matchers.everyItem(PokemonMatcher.isValidPokemon())));

    public static Matcher<?> isValidTeam() {
        return Matchers.allOf(
                VALID_TEAM_MAP_MATCHER,
                Matchers.hasEntry(Matchers.equalTo(POKEMONS), Matchers.iterableWithSize(6))
        );
    }

    @SuppressWarnings("unchecked")
    public static Matcher<?> hasPokemons(@NotNull String... pokemonNames) {
        List<Matcher<?>> pokemonMatchers = new ArrayList<>();
        for (String pokemonName : pokemonNames) {
            if (pokemonName.equals("Zamazenta")) {
                pokemonMatchers.add(PokemonMatcher.isPokemon("Zamazenta-*"));
            } else {
                pokemonMatchers.add(PokemonMatcher.isPokemon(pokemonName));
            }
        }

        Matcher<? super Map<?, ?>>[] itemMatchers = (Matcher<? super Map<?, ?>>[]) pokemonMatchers.toArray(new Matcher<?>[0]);
        return Matchers.hasEntry(Matchers.equalTo(POKEMONS),
                Matchers.hasItems(itemMatchers));
    }

    public static Matcher<?> hasTags(@NotNull String... tags) {
        return Matchers.hasEntry(Matchers.equalTo(TAG_SET),
                Matchers.hasItems(Matchers.in(tags)));
    }
}