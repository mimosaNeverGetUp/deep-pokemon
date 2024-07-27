/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
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

    public List<BattleStat> analyze(Collection<Battle> battles) {
        List<BattleStat> battleStats = new ArrayList<>();
        for (Battle battle : battles) {
            try {
                log.debug("start analyze battle {}", battle.getBattleID());
                BattleStat battleStat = new BattleStat(battle.getBattleID(), new ArrayList<>(), new ArrayList<>());
                BattleStatus battleStatus = new BattleStatus(new ArrayList<>());
                List<BattleEvent> battleEvents = battleEventParser.parse(battle.getLog());
                battleEvents.forEach(battleEvent -> analyzeEvent(battleEvent, battleStat, battleStatus));
                log.debug("end analyze battle {}", battle.getBattleID());
                battleStats.add(battleStat);
            } catch (Exception e) {
                log.error("analyze battle {} error", battle.getBattleID(), e);
            }
        }
        return battleStats;
    }

    public void analyzeEvent(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        battleEventAnalyzers.forEach(battleEventAnalyzer -> {
            if (battleEventAnalyzer.supportAnalyze(battleEvent)) {
                battleEventAnalyzer.analyze(battleEvent, battleStat, battleStatus);
            }
        });
        if (battleEvent.getChildrenEvents() != null) {
            battleEvent.getChildrenEvents().forEach(childEvent -> analyzeEvent(childEvent, battleStat, battleStatus));
        }
    }
}