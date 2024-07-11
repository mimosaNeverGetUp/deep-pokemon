/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Document("monthly_stat_battle)")
public record MonthlyBattleStat(@MongoId String id, String format, LocalDate date, int total, double avgTeamWeight) {

}