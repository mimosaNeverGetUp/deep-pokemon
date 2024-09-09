/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.dto;

import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Tag;
import com.mimosa.deeppokemon.entity.TeamSet;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record TeamGroupDto(@MongoId Binary id, String tier, Integer uniquePlayerNum, Integer replayNum,
                           Integer maxRating, Float maxPlayerWinRate, Integer maxPlayerWinDif,
                           List<Pokemon> pokemons, Set<Tag> tagSet, List<Binary> featureIds, LocalDate latestBattleDate,
                           List<BattleTeamDto> teams, TeamSet teamSet, List<TeamGroupDto> similarTeams) implements Serializable {

}