/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.utils;

import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BattleEventUtil {
    private static final Logger log = LoggerFactory.getLogger(BattleEventUtil.class);
    private static final Pattern TARGET_PATTERN = Pattern.compile(".*p(\\d)[a-z]: (.+)");
    private static final Pattern FROM_PATTERN = Pattern.compile(Pattern.quote("[from] ") + "(.+)");

    public static EventTarget getEventTarget(String eventContent) {
        Matcher matcher = TARGET_PATTERN.matcher(eventContent);
        if (matcher.find()) {
            int playerNumber = Integer.parseInt(matcher.group(1));
            String pokemonNickName = matcher.group(2);

            return new EventTarget(playerNumber, null, pokemonNickName);
        }
        log.warn("can't find event target");
        return null;
    }

    public static EventTarget getEventTarget(String eventContent, BattleStatus battleStatus) {
        EventTarget target = getEventTarget(eventContent);
        if (target == null) {
            return null;
        }
        String pokemonName = battleStatus.getPlayerStatusList()
                .get(target.plyayerNumber() - 1).getPokemonName(target.nickPokemonName());
        return target.withPokemonName(pokemonName);
    }

    public static String getEventFrom(String eventContent) {
        Matcher matcher = FROM_PATTERN.matcher(eventContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        log.warn("can't find event target");
        return null;
    }
}