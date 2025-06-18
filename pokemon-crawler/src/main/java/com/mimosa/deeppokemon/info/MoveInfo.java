/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.info;

import java.io.Serializable;

public record MoveInfo(String name, String type, int basePower, String isZ) implements Serializable {
}