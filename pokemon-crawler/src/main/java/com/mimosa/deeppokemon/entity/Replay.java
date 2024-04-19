/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Replay(String id, @JsonProperty("uploadtime") long uploadTime, String format, int rating, String[] players,
        boolean isPrivate) {

}