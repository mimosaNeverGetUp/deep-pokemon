/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.ActivateStatus;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.utils.MatcherUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

@Component
public class ActivateEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(ActivateEventAnalyzer.class);
    private static final String ACTIVATE = "activate";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(ACTIVATE);

    private static final int TARGET_INDEX = 0;
    private static final int ACTIVATE_INDEX = 1;
    private static final int OF_INDEX = 2;
    private static final Pattern ACTIVATE_PATTERN = Pattern.compile("([^:]+)" + Pattern.quote(": ") + "(.+)");

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }

        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX),
                battleContext);
        String content = battleEvent.getContents().get(ACTIVATE_INDEX);
        if (eventTarget != null) {
            EventTarget ofTarget = null;
            String type = MatcherUtil.groupMatch(ACTIVATE_PATTERN, content, 1);
            String status = MatcherUtil.groupMatch(ACTIVATE_PATTERN, content, 2);
            if (battleEvent.getContents().size() > OF_INDEX) {
                ofTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(OF_INDEX), battleContext);
            }
            battleContext.getPlayerStatusList().get(eventTarget.playerNumber() - 1).getPokemonStatus(eventTarget.targetName())
                    .addActivateStatus(new ActivateStatus(content, type, status, ofTarget));
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}