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
    private static final Pattern TARGET_PATTERN = Pattern.compile(".*p(\\d)[a-z]*: (.+)");
    private static final Pattern FROM_PATTERN = Pattern.compile(Pattern.quote("[from] ") + "(.+)");
    private static final String MOVE_PATTERN = "move:";

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
                .get(target.playerNumber() - 1).getPokemonName(target.nickName());
        return target.withTargetName(pokemonName);
    }

    public static String getEventFrom(String eventContent) {
        String from = null;
        Matcher matcher = FROM_PATTERN.matcher(eventContent);
        if (matcher.find()) {
             from = matcher.group(1);
        }
        if (from.contains(MOVE_PATTERN)) {
            from = from.replace(MOVE_PATTERN, "").strip();
        }

        return from;
    }
}