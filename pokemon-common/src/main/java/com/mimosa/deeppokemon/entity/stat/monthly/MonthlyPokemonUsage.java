/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat.monthly;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Document("monthly_stat_pokemon_usage")
public record MonthlyPokemonUsage(@MongoId String id, String name, String format, LocalDate date, String statId,
                                  long count, Usage usage) {
}