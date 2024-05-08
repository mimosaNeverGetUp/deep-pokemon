/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.matcher;

import com.mimosa.deeppokemon.analyzer.entity.PlayerStat;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.util.StringUtils;

public class PlayerStatMatcher extends TypeSafeMatcher<PlayerStat> {
    public static PlayerStatMatcher PLAYER_STAT_MATCHER = new PlayerStatMatcher();

    @Override
    protected boolean matchesSafely(PlayerStat playerStat) {
        if (!StringUtils.hasText(playerStat.getPlayerName())) {
            return false;
        }

        if (playerStat.getPlayerNumber() == 0) {
            return false;
        }

        if (playerStat.getSwitchCount() == 0) {
            return false;
        }

        if (playerStat.getMoveCount() == 0) {
            return false;
        }

        if (playerStat.getHighLights() == null || playerStat.getHighLights().isEmpty()) {
            return false;
        }

        if (!playerStat.getPokemonBattleStats().stream().anyMatch(PokemonStatMatcher.PLAYER_STAT_MATCHER::matches)) {
            return false;
        }

        return playerStat.getPokemonBattleStats().stream().anyMatch(stat -> stat.getMoveCount() != 0)
                && playerStat.getPokemonBattleStats().stream().anyMatch(stat -> stat.getSwitchCount() != 0)
                && playerStat.getPokemonBattleStats().stream().anyMatch(stat -> stat.getHealthValue().intValue() != 0)
                && playerStat.getPokemonBattleStats().stream().anyMatch(stat -> stat.getAttackValue().intValue() != 0);
    }

    @Override
    public void describeTo(Description description) {

    }
}