/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat.monthly;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

@Document("monthly_stat_pokemon_moveset")
public record MonthlyPokemonMoveSet(@MongoId String id, String name, String format, LocalDate date, String statId,
                                    LinkedHashMap<String, Double> abilities, LinkedHashMap<String, Double> items,
                                    LinkedHashMap<String, Double> spreads, LinkedHashMap<String, Double> moves,
                                    LinkedHashMap<String, Double> teammates, LinkedHashMap<String, Double> happinesses,
                                    List<Counter> counters) {
}