/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.dto;

import com.mimosa.deeppokemon.entity.BattleTeam;

import java.time.LocalDate;
import java.util.List;

public record BattleDto(String id, LocalDate date, float avageRating, List<String> type, String winner,
                        List<BattleTeam> teams) {

}