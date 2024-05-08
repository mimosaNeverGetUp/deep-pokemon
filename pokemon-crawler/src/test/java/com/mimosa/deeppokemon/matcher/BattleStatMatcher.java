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
    public static final BattleStatMatcher BATTLE_STAT_MATCHER = new BattleStatMatcher();
    String msg = "";

    @Override
    protected boolean matchesSafely(BattleStat battleStat) {
        if (battleStat.battleId() == null) {
            msg = "Battle ID is null";
            return false;
        }

        if (battleStat.playerStatList() == null || battleStat.playerStatList().isEmpty()) {
            msg = "Player stat list is null";
            return false;
        }

        if (battleStat.turnStats() == null || battleStat.turnStats().isEmpty()) {
            msg = "Turn stats is null";
            return false;
        }

        if (!battleStat.playerStatList().stream().allMatch(PlayerStatMatcher.PLAYER_STAT_MATCHER::matches)) {
            msg = "Player stats list doesn't match";
            return false;
        }

        if (battleStat.playerStatList().stream().noneMatch(playerStat ->
                playerStat.getPokemonBattleStats().stream().anyMatch(pokemonBattleStat -> pokemonBattleStat.getKillCount() == 0))) {
            msg = "Player stats kill count all 0";
            return false;
        }

        if (!battleStat.turnStats().stream().allMatch(TurnStatMatcher.TURN_STAT_MATCHER::matches)) {
            msg = "Turn stats list doesn't match";
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(msg);
    }
}