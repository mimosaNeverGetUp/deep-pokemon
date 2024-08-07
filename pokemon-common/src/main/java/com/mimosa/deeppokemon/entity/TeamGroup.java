/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Document("team_group")
public record TeamGroup(@MongoId byte[] id, String tier, int uniquePlayerNum, int maxRating, List<Pokemon> pokemons,
                        Set<Tag> tagSet, LocalDate latestBattleDate, List<BattleTeam> teams) implements Serializable {

}