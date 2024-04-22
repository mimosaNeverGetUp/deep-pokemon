/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BattleEventParser {

    /**
     * common patternEvent: |eventType|eventString
     * common childrenEvent: |-eventType|eventString
     */
    private static final Pattern eventPattern = Pattern.compile(String.format("%s([^%s]*)%s*(.*)", Pattern.quote("|"),
            Pattern.quote("|"), Pattern.quote("|")));

    public static final String CHILDREN_EVENT_FLAG = "-";

    List<BattleEvent> parse(String battleLog) {
        if (battleLog == null) {
            return new ArrayList<>();
        }
        List<BattleEvent> battleEvents = new ArrayList<>();
        AtomicReference<BattleEvent> currentParentEvent = new AtomicReference<>();

        battleLog.lines().forEach(eventStr -> {
            BattleEvent battleEvent = parseBattleEvent(eventStr);
            if (battleEvent == null) {
                return;
            }

            if (battleEvent.parentEvent()) {
                battleEvent = battleEvent.withChildrenEvents(new ArrayList<>());
                battleEvents.add(battleEvent);
                currentParentEvent.set(battleEvent);
            } else {
                currentParentEvent.get().childrenEvents().add(battleEvent);
            }
        });

        return battleEvents;
    }

    private BattleEvent parseBattleEvent(String eventStr) {
        String eventType = null;
        String content = null;
        boolean parentEvent = true;

        Matcher matcher = eventPattern.matcher(eventStr);
        if (matcher.find()) {
            eventType = matcher.group(1);
            if (eventType == null || eventType.isEmpty()) {
                return null;
            }

            content = matcher.group(2);
            if (eventType.contains(CHILDREN_EVENT_FLAG)) {
                eventType = eventType.replaceAll(CHILDREN_EVENT_FLAG, "");
                parentEvent = false;
            }
        }

        return new BattleEvent(eventType, content, parentEvent, null);
    }
}