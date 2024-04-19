/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.matcher;

import com.mimosa.deeppokemon.entity.Team;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class TeamMatcher extends TypeSafeMatcher<Team> {
    public static final TeamMatcher TEAM_MATCHER = new TeamMatcher();

    @Override
    protected boolean matchesSafely(Team team) {
        if (team.getTier() == null) {
            return false;
        }

        if (team.getPlayerName() == null) {
            return false;
        }

        if (team.getTagSet() == null || team.getTagSet().isEmpty()) {
            return false;
        }

        if (team.getPokemons() == null || team.getPokemons().isEmpty()) {
            return false;
        }

        return team.getPokemons().stream().allMatch(PokemonMatcher.POKEMON_MATCHER::matches);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("team with valid pokemon.");
    }
}