/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat;

import java.util.Objects;

/**
 * 单个回合内按结算顺序记录的一条关键对战事件。
 * 通过 eventType 区分事件种类，并携带该事件的主体/来源等最小字段，
 * 供 AI 复盘与前端按时间线叙事、分层展示。
 *
 * <p>当前支持的事件类型：
 * <ul>
 *   <li>move —— 某精灵出招（actionName 为招式名）</li>
 *   <li>damage —— 某精灵受到伤害（eventFrom 为伤害来源，如招式名/Stealth Rock/Rocky Helmet）</li>
 *   <li>switch —— 某方换上某精灵</li>
 *   <li>faint —— 某精灵倒下</li>
 * </ul>
 *
 * <p>playerNumber/pokemon 为该事件的主体：move=出招方，damage=受击方，switch=换入方，faint=倒下方。
 * description 为面向网页展示的事实性补充描述（尽量用简单清晰的英文，如 "took 44% damage"），不承担数据聚合职责。
 * eventFrom 记录该事件的事实来源（当前主要用在 damage：其伤害来源）。
 *
 * <p>childEvent 标记该事件是否为子事件（如 damage 是 move 的子事件）；若为子事件，
 * parentEventType 记录其父事件类型（如 "move"），用于前端分层展示时正确嵌套。
 */
public class TurnEvent {
    private String eventType;
    private int playerNumber;
    private String pokemon;
    private String actionName;
    private String eventFrom;
    private String description;
    private boolean childEvent;
    private String parentEventType;

    public TurnEvent() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getPokemon() {
        return pokemon;
    }

    public void setPokemon(String pokemon) {
        this.pokemon = pokemon;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getEventFrom() {
        return eventFrom;
    }

    public void setEventFrom(String eventFrom) {
        this.eventFrom = eventFrom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChildEvent() {
        return childEvent;
    }

    public void setChildEvent(boolean childEvent) {
        this.childEvent = childEvent;
    }

    public String getParentEventType() {
        return parentEventType;
    }

    public void setParentEventType(String parentEventType) {
        this.parentEventType = parentEventType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TurnEvent that = (TurnEvent) o;
        return playerNumber == that.playerNumber && childEvent == that.childEvent
                && Objects.equals(eventType, that.eventType) && Objects.equals(pokemon, that.pokemon)
                && Objects.equals(actionName, that.actionName) && Objects.equals(eventFrom, that.eventFrom)
                && Objects.equals(description, that.description) && Objects.equals(parentEventType, that.parentEventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, playerNumber, pokemon, actionName, eventFrom, description, childEvent,
                parentEventType);
    }
}