/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat.monthly;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashMap;

@Document("monthly_stat_meta")
public record MonthlyMetaStat(@MongoId String id, String format, LocalDate date, long total, LinkedHashMap<String,
        Double> tags) implements Serializable {

}