/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.tagger.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record ItemValue(Set<String> items, @JsonProperty("def_value") Double defValue,
                        @JsonProperty("atk_value") Double atkValue) {
}