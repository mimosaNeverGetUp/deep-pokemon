/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.event;

import java.util.List;

public final class BattleEvent {
    private final String type;
    private final List<String> contents;
    private final BattleEvent parentEvent;
    private List<BattleEvent> childrenEvents;
    private Object battleEventStat;

    public BattleEvent(String type, List<String> contents, BattleEvent parentEvent, List<BattleEvent> childrenEvents) {
        this.type = type;
        this.contents = contents;
        this.parentEvent = parentEvent;
        this.childrenEvents = childrenEvents;
        this.battleEventStat = null;
    }

    public void setChildrenEvents(List<BattleEvent> childrenEvents) {
        this.childrenEvents = childrenEvents;
    }

    public String getType() {
        return type;
    }

    public List<String> getContents() {
        return contents;
    }

    public BattleEvent getParentEvent() {
        return parentEvent;
    }

    public List<BattleEvent> getChildrenEvents() {
        return childrenEvents;
    }

    public Object getBattleEventStat() {
        return battleEventStat;
    }

    public void setBattleEventStat(Object battleEventStat) {
        this.battleEventStat = battleEventStat;
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