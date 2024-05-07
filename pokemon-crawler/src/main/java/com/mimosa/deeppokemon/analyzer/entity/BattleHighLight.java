/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

public record BattleHighLight(int turn, HighLightType type, String description) {
    public enum HighLightType {KILL, SIDE, END_SIDE,}
}