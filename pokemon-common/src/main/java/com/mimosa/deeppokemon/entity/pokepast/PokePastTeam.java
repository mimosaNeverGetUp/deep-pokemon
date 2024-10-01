/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.pokepast;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Map;

@Document("pokepast_team")
public record PokePastTeam(@MongoId String id, String url, String format, String author, Binary teamId,
                           Map<String, String> pokemonSets) {

}