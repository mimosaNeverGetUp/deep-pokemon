/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.entity.ReplaySource;

public interface ReplayProvider {
    ReplaySource next();

    boolean hasNext();
}