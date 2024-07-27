/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Map;

public record MonthlyBattleStatDto(LocalDate date, int battles, Map<String, MonthlyPokemonStatDto> pokemon,
                                   MonthlyMetaGameStatDto metagame) {
    @JsonCreator
    public MonthlyBattleStatDto(@JsonProperty("battles") int battles, @JsonProperty("pokemon") Map<String,
            MonthlyPokemonStatDto> pokemon, @JsonProperty("metagame") MonthlyMetaGameStatDto metagame) {
        this(LocalDate.now().minusMonths(1).withDayOfMonth(1), battles, pokemon, metagame);
    }
}