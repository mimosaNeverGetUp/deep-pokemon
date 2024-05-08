/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.event;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;

import java.math.BigDecimal;

public record DamageEventStat(EventTarget eventTarget, EventTarget damageOf, String damageFrom, BigDecimal healthDiff) {
    @Override
    public String toString() {
        return "DamageEventStat{" +
                "eventTarget=" + eventTarget +
                ", damageOf=" + damageOf +
                ", damageFrom='" + damageFrom + '\'' +
                ", healthDiff=" + healthDiff +
                '}';
    }
}