/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BattleReplayData(String id, String format, List<String> players,
                               @JsonProperty("uploadtime") long uploadTime,
                               String log, @JsonProperty("formatid") String formatId, int rating) {
}