/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Document("team_group")
public record TeamGroup(@MongoId byte[] id, String tier, Integer uniquePlayerNum, Integer replayNum, Integer maxRating,
                        Float maxPlayerWinRate, Integer maxPlayerWinDif,
                        List<Pokemon> pokemons, Set<Tag> tagSet, List<Binary> featureIds, LocalDate latestBattleDate,
                        List<BattleTeam> teams) implements Serializable {
}