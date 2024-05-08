/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.service.BattleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class BattleAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(BattleAnalyzer.class);

    List<BattleEventAnalyzer> battleEventAnalyzers;

    BattleEventParser battleEventParser;

    BattleService battleService;

    @Lazy
    public BattleAnalyzer(BattleEventParser battleEventParser, List<BattleEventAnalyzer> battleEventAnalyzers,
                          BattleService battleService) {
        this.battleEventParser = battleEventParser;
        this.battleEventAnalyzers = battleEventAnalyzers;
        this.battleService = battleService;
    }

    public List<BattleStat> analyze(Collection<Battle> battles) {
        List<BattleStat> battleStats = new ArrayList<>();
        for (Battle battle : battles) {
            try {
                BattleStat battleStat = new BattleStat(new ArrayList<>());
                BattleStatus battleStatus = new BattleStatus(new ArrayList<>());
                List<BattleEvent> battleEvents = battleEventParser.parse(battle.getLog());
                battleEvents.forEach(battleEvent ->analyzeEvent(battleEvent, battleStat, battleStatus));
                battleStats.add(battleStat);
            } catch (Exception e) {
                log.error("analyze battle {} error", battle.getBattleID(), e);
            }
        }
        battleService.savaAll(battleStats);
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