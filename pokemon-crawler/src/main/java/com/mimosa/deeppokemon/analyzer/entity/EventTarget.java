/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity;

public record EventTarget(int plyayerNumber, String targetName, String nickName) {
    public EventTarget withTargetName(String targetName) {
        return new EventTarget(plyayerNumber, targetName, nickName);
    }
}