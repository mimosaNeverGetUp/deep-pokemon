/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.utils;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BattleEventUtil {
    private static final Logger log = LoggerFactory.getLogger(BattleEventUtil.class);
    private static final Pattern TARGET_PATTERN = Pattern.compile(".*p(\\d)[a-z]*: (.+)");
    private static final Pattern FROM_PATTERN = Pattern.compile(Pattern.quote("[from] ") + "(.+)");
    private static final String MOVE_PATTERN = "move:";
    private static final Pattern FAINT_PATTERN = Pattern.compile("(\\d+) fnt");

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
        if (from != null && from.contains(MOVE_PATTERN)) {
            from = from.replace(MOVE_PATTERN, "").strip();
        }

        return from;
    }

    public static int getHealth(String healthContext) {
        Matcher matcher = FAINT_PATTERN.matcher(healthContext);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return Integer.parseInt(healthContext.split(EventConstants.HEALTH_SPLIT)[0]);
        }
    }

    public static PokemonStatus getPokemonStatus(BattleStatus battleStatus, EventTarget eventTarget) {
        return getPokemonStatus(battleStatus, eventTarget.playerNumber(), eventTarget.targetName());
    }

    public static PokemonStatus getPokemonStatus(BattleStatus battleStatus, int playerNumber, String pokemonName) {
        return battleStatus.getPlayerStatusList().get(playerNumber - 1).getPokemonStatus(pokemonName);
    }

    public static PokemonBattleStat getPokemonStat(BattleStat battleStat, EventTarget eventTarget) {
        return getPokemonStat(battleStat, eventTarget.playerNumber(), eventTarget.targetName());
    }


    public static PokemonBattleStat getPokemonStat(BattleStat battleStat, int playerNumber, String pokemonName) {
        return battleStat.playerStatList().get(playerNumber - 1).getPokemonBattleStat(pokemonName);
    }
}