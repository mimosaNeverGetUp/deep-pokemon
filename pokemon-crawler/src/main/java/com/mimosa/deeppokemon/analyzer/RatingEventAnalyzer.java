/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RatingEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(RatingEventAnalyzer.class);
    protected static final String RAW = "raw";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(RAW);
    private static final Pattern RATING_PATTERN = Pattern.compile("(.+)" + Pattern.quote("'s rating: ") +
            "(\\d+)" + ".+" + Pattern.quote("<strong>") + "(\\d+)" + Pattern.quote("</strong>"));

    private static final int CONTENT_INDEX = 0;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().isEmpty()) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }

        String content = battleEvent.getContents().get(CONTENT_INDEX);
        Matcher matcher = RATING_PATTERN.matcher(content);
        if (matcher.find()) {
            try {
                String player = matcher.group(1);
                float beforeRating = Float.parseFloat(matcher.group(2));
                float afterRating = Float.parseFloat(matcher.group(3));
                float rating = Math.max(afterRating, beforeRating);
                log.debug("start to set rating {} for player '{}'", rating, player);
                for (BattleTeam team : battleContext.getBattle().getBattleTeams()) {
                    if (StringUtils.equals(player, team.getPlayerName())) {
                        team.setRating(Math.max(team.getRating(), rating));
                    }
                }
            } catch (Exception e) {
                log.error("set rating error, battle event {}", battleEvent, e);
            }
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}