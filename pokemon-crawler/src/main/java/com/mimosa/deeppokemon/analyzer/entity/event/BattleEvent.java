/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.event;

import java.util.List;

public record BattleEvent(String type, String content, boolean parentEvent, List<BattleEvent> childrenEvents) {
    public BattleEvent withChildrenEvents(List<BattleEvent> childrenEvents) {
        return new BattleEvent(type, content, parentEvent, childrenEvents);
    }
}