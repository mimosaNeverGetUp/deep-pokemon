/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.matcher;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Map;

public class PlayerMatcher {

    public static final String NAME = "name";
    public static final String INFO_DATE = "infoDate";
    public static final String ELO = "elo";
    public static final String RANK = "rank";
    public static final String GXE = "gxe";
    public static final String RECENT_TEAM = "recentTeam";
    private static final Matcher<?> PLAYER_MATCHER = Matchers.allOf(
            Matchers.hasEntry(Matchers.equalTo(NAME), Matchers.notNullValue()),
            Matchers.hasEntry(Matchers.equalTo(INFO_DATE), Matchers.notNullValue()),
            Matchers.hasEntry(Matchers.equalTo(ELO), Matchers.notNullValue()),
            Matchers.hasEntry(Matchers.equalTo(RANK), Matchers.notNullValue()),
            Matchers.hasEntry(Matchers.equalTo(GXE), Matchers.notNullValue()),
            Matchers.hasEntry(Matchers.equalTo(RECENT_TEAM),
                    Matchers.anyOf(Matchers.everyItem(TeamMatcher.isValidTeam()), Matchers.nullValue()))
    );

    public static Matcher<?> isValidPlayer() {
        return PLAYER_MATCHER;
    }

}