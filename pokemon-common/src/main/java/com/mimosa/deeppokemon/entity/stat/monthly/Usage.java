/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat.monthly;

import java.io.Serializable;

public record Usage(double raw, double real, double weighted) implements Serializable {
}