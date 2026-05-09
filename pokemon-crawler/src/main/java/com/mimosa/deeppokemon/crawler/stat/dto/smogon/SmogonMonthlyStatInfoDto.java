/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat.dto.smogon;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SmogonMonthlyStatInfoDto(@JsonProperty("metagame") String format, int cutoff, @JsonProperty(
        "number of battles") int battleCount) {

}