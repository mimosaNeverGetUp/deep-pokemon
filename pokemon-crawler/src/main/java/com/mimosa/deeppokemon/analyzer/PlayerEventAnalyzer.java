/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.PlayerStat;
import com.mimosa.deeppokemon.analyzer.entity.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.utils.MatcherUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.regex.Pattern;

@Component
public class PlayerEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(PlayerEventAnalyzer.class);

    private static final String PLAYER = "player";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(PLAYER);
    public static final Pattern PLAYER_NUMBER_PATTERN = Pattern.compile("p(\\d)");

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.contents().size() < 2) {
            log.error("can not match player content: {}", battleEvent);
            throw new IllegalArgumentException("can not match player content");
        }

        String playerNumberStr = MatcherUtil.groupMatch(PLAYER_NUMBER_PATTERN, battleEvent.contents().get(0), 1);
        String playerName = battleEvent.contents().get(1);
        if (!StringUtils.hasText(playerNumberStr) || !StringUtils.hasText(playerName)) {
            log.error("can not match player name or number: {}", battleEvent);
            throw new IllegalArgumentException("can not match player name or number");
        }

        battleStat.playerStatList().add(new PlayerStat(Integer.parseInt(playerNumberStr), playerName));
        battleStatus.getPlayerStatusList().add(new PlayerStatus());
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.type());
    }
}