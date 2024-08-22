/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.dto;

import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Tag;
import com.mimosa.deeppokemon.entity.TeamSet;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record TeamGroupDto(@MongoId Binary id, String tier, int uniquePlayerNum, Integer replayNum, int maxRating,
                           List<Pokemon> pokemons, Set<Tag> tagSet, LocalDate latestBattleDate,
                           List<BattleTeam> teams, TeamSet teamSet) implements Serializable {
}