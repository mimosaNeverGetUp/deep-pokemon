/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class BattleEventParser {
    public static final String CHILDREN_EVENT_FLAG = "-";

    List<BattleEvent> parse(String battleLog) {
        if (battleLog == null) {
            return new ArrayList<>();
        }
        List<BattleEvent> battleEvents = new ArrayList<>();
        AtomicReference<BattleEvent> currentParentEvent = new AtomicReference<>();

        battleLog.lines().forEach(eventStr -> {
            BattleEvent battleEvent = parseBattleEvent(eventStr, currentParentEvent.get());
            if (battleEvent == null) {
                currentParentEvent.set(null);
                return;
            }

            if (battleEvent.getParentEvent() == null) {
                battleEvent.setChildrenEvents(new ArrayList<>());
                battleEvents.add(battleEvent);
                currentParentEvent.set(battleEvent);
            } else {
                currentParentEvent.get().getChildrenEvents().add(battleEvent);
            }
        });

        return battleEvents;
    }

    private BattleEvent parseBattleEvent(String eventStr, BattleEvent currentParentEvent) {
        String[] elements = eventStr.replaceFirst("\\|", "").split("\\|");
        if (elements.length == 0) {
            return null;
        }
        BattleEvent parentEvent = null;
        String eventType = elements[0];
        if (!StringUtils.hasText(eventType)) {
            return null;
        }

        List<String> content = elements.length < 2 ? null : List.of(Arrays.copyOfRange(elements, 1, elements.length));
        if (eventType.contains(CHILDREN_EVENT_FLAG)) {
            parentEvent = currentParentEvent;
            eventType =eventType.replaceAll(CHILDREN_EVENT_FLAG, "");
        }

        return new BattleEvent(eventType, content, parentEvent, null);
    }
}