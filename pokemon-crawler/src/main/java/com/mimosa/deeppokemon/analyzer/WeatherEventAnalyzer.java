/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Weather;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class WeatherEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(WeatherEventAnalyzer.class);
    private static final String WEATHER = "weather";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(WEATHER);
    private static final String NONE = "none";
    private static final String UPKEEP = "upkeep";
    private static final int WEATHER_INDEX = 0;
    private static final int OF_INDEX = 2;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().isEmpty()) {
            log.warn("can not analyze battle event: {}", battleEvent);
            return;
        }

        String weather = battleEvent.getContents().get(WEATHER_INDEX);
        if (NONE.equals(weather)) {
            battleContext.setWeather(null);
            return;
        }

        if (battleEvent.getContents().size() < OF_INDEX || battleEvent.getContents().get(1).contains(UPKEEP)) {
            log.debug("weather is upkeep");
            return;
        }

        EventTarget ofTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(OF_INDEX), battleContext);
        battleContext.setWeather(new Weather(weather, ofTarget));
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}