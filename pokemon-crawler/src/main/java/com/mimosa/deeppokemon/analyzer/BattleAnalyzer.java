/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class BattleAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(BattleAnalyzer.class);

    List<BattleEventAnalyzer> battleEventAnalyzers;

    BattleEventParser battleEventParser;

    public BattleAnalyzer(BattleEventParser battleEventParser, List<BattleEventAnalyzer> battleEventAnalyzers) {
        this.battleEventParser = battleEventParser;
        this.battleEventAnalyzers = battleEventAnalyzers;
    }

    public Collection<Battle> analyze(Collection<Battle> battles) {
        for (Battle battle : battles) {
            try {
                log.debug("start analyze battle {}", battle.getBattleID());
                BattleStat battleStat = new BattleStat(battle.getBattleID(), new ArrayList<>(), new ArrayList<>());
                BattleContext battleContext = new BattleContext(new ArrayList<>(), battle);
                List<BattleEvent> battleEvents = battleEventParser.parse(battle.getLog());
                battleEvents.forEach(battleEvent -> analyzeEvent(battleEvent, battleStat, battleContext));
                log.debug("end analyze battle {}", battle.getBattleID());
                battle.setBattleStat(battleStat);
            } catch (Exception e) {
                log.error("analyze battle {} error", battle.getBattleID(), e);
            }
        }
        return battles;
    }

    public void analyzeEvent(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        battleEventAnalyzers.forEach(battleEventAnalyzer -> {
            if (battleEventAnalyzer.supportAnalyze(battleEvent)) {
                battleEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
            }
        });
        if (battleEvent.getChildrenEvents() != null) {
            battleEvent.getChildrenEvents().forEach(childEvent -> analyzeEvent(childEvent, battleStat, battleContext));
        }
    }
}