/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.utils;

import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BattleEventUtil {
    private static final Logger log = LoggerFactory.getLogger(BattleEventUtil.class);
    private static final Pattern TARGET_PATTERN = Pattern.compile(".*p(\\d)[a-z]*: (.+)");
    private static final Pattern FROM_PATTERN = Pattern.compile(Pattern.quote("[from] ") + "(.+)");
    private static final String MOVE_PATTERN = "move:";
    private static final Pattern FAINT_PATTERN = Pattern.compile("(\\d+) fnt");
    private static final Pattern HEALTH_PATTERN = Pattern.compile("(\\d+)/(\\d+)");
    private static final int PERCENTAGE = 100;

    private BattleEventUtil() {}

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

    public static EventTarget getEventTarget(String eventContent, BattleContext battleContext) {
        EventTarget target = getEventTarget(eventContent);
        if (target == null) {
            return null;
        }
        String pokemonName = battleContext.getPlayerStatusList()
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

    public static BigDecimal getHealthPercentage(String healthContext) {
        Matcher matcher = FAINT_PATTERN.matcher(healthContext);
        if (matcher.find()) {
            return new BigDecimal(matcher.group(1)).setScale(1, RoundingMode.HALF_DOWN);
        } else {
            Matcher healthMatcher = HEALTH_PATTERN.matcher(healthContext);
            if (healthMatcher.find()) {
                BigDecimal currentHealth = new BigDecimal(healthMatcher.group(1));
                BigDecimal totalHealth = new BigDecimal(healthMatcher.group(2));
                BigDecimal healthPercentage = currentHealth.divide(totalHealth, 3, RoundingMode.HALF_DOWN);
                return healthPercentage.multiply(BigDecimal.valueOf(PERCENTAGE)).setScale(1, RoundingMode.HALF_DOWN);
            }
        }
        throw new RuntimeException("can not match health context " + healthContext);
    }

    public static PokemonStatus getPokemonStatus(BattleContext battleContext, EventTarget eventTarget) {
        return getPokemonStatus(battleContext, eventTarget.playerNumber(), eventTarget.targetName());
    }

    public static PokemonStatus getPokemonStatus(BattleContext battleContext, int playerNumber, String pokemonName) {
        return battleContext.getPlayerStatusList().get(playerNumber - 1).getPokemonStatus(pokemonName);
    }

    public static PokemonBattleStat getPokemonStat(BattleStat battleStat, EventTarget eventTarget) {
        return getPokemonStat(battleStat, eventTarget.playerNumber(), eventTarget.targetName());
    }

    public static PokemonBattleStat getPokemonStat(BattleStat battleStat, int playerNumber, String pokemonName) {
        return battleStat.playerStatList().get(playerNumber - 1).getPokemonBattleStat(pokemonName);
    }

    public static BattleEvent getPreviousChildrenEvent(BattleEvent battleEvent) {
        if (battleEvent.getPreviousEvent() == null || battleEvent.getPreviousEvent().getChildrenEvents() == null) {
            return null;
        }
        int index = battleEvent.getParentEvent().getChildrenEvents().indexOf(battleEvent);
        return index == 0 ? null : battleEvent.getParentEvent().getChildrenEvents().get(index - 1);
    }

    public static PlayerStatus getOpponentPlayerStatus(BattleContext battleContext, EventTarget eventTarget) {
        int opponentPlayerNumber = 3 - eventTarget.playerNumber();
        return battleContext.getPlayerStatusList().get(opponentPlayerNumber - 1);
    }

    public static EventTarget getOpponentTurnStartPokemonTarget(BattleContext battleContext, EventTarget eventTarget) {
        int opponentPlayerNumber = 3 - eventTarget.playerNumber();
        PlayerStatus oppentPlayerStatus = battleContext.getPlayerStatusList().get(opponentPlayerNumber - 1);
        return new EventTarget(opponentPlayerNumber, oppentPlayerStatus.getTurnStartPokemonName(), null);
    }


    public static EventTarget getOpponentActivePokemonTarget(BattleContext battleContext, EventTarget eventTarget) {
        int opponentPlayerNumber = 3 - eventTarget.playerNumber();
        PlayerStatus oppentPlayerStatus = battleContext.getPlayerStatusList().get(opponentPlayerNumber - 1);
        return new EventTarget(opponentPlayerNumber, oppentPlayerStatus.getActivePokemonName(), null);
    }
}