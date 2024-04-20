/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.matcher;

import com.mimosa.deeppokemon.entity.Battle;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;

public class BattleMatcher extends TypeSafeMatcher<Battle> {

    public static final BattleMatcher BATTLE_MATCHER = new BattleMatcher();

    @Override
    protected boolean matchesSafely(Battle battle) {
        if (battle.getBattleID() == null) {
            return false;
        }

        if (battle.getDate() == null) {
            return false;
        }

        if (battle.getWinner() == null) {
            return false;
        }

        if (battle.getLog() == null) {
            return false;
        }

        if (battle.getTeams() == null || battle.getTeams().length == 0) {
            return false;
        }

        return Arrays.stream(battle.getTeams()).allMatch(TeamMatcher.TEAM_MATCHER::matches);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("battle with valid team.");
    }
}