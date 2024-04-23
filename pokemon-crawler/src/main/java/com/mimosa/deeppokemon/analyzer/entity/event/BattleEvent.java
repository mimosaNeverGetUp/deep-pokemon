/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.event;

import java.util.List;

public record BattleEvent(String type, List<String> contents, boolean parentEvent, List<BattleEvent> childrenEvents) {
    public BattleEvent withChildrenEvents(List<BattleEvent> childrenEvents) {
        return new BattleEvent(type, contents, parentEvent, childrenEvents);
    }

    @Override
    public String toString() {
        return "BattleEvent{" +
                "type='" + type + '\'' +
                ", contents='" + contents + '\'' +
                ", parentEvent=" + parentEvent +
                ", childrenEvents=" + childrenEvents +
                '}';
    }
}