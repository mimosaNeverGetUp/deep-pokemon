/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Document("team_set")
public record TeamSet(@MongoId Binary id, String tier, long replayNum, LocalDate minReplayDate, Set<Tag> tagSet,
                      List<PokemonBuildSet> pokemons) {
    public TeamSet withTags(Set<Tag> tagSet) {
        return new TeamSet(id, tier, replayNum, minReplayDate, tagSet, pokemons);
    }
}