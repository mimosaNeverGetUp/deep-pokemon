/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.event;

import com.mimosa.deeppokemon.analyzer.entity.EventTarget;

public record EndEventStat(EventTarget eventTarget, String endEvent) {
}