/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Map;

@Document("pokemon_analyze")
public record PokemonAnalyze(@MongoId String id, String name, String format, Map<String, String> setAnalyzes,
                             Map<String, String> setChineseAnalyzes) {
}