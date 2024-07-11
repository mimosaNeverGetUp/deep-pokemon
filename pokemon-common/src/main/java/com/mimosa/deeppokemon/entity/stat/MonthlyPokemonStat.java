/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.LinkedHashMap;

@Document("monthly_stat_pokemon)")
public record MonthlyPokemonStat(@MongoId String id, String name, String format, LocalDate date, String statId, int rank,
                                 double usage, int rawCount, double rawPercentage,
                                 int realCount, double realPercentage, MoveSet moveSet) {

    public MonthlyPokemonStat withMoveSet(MoveSet moveSet) {
        return new MonthlyPokemonStat(this.id, this.name, this.format, this.date, this.statId, this.rank,
                this.usage, this.rawCount, this.rawPercentage, this.rawCount, this.rawPercentage, moveSet);
    }

    public record MoveSet(LinkedHashMap<String, Double> abilities, LinkedHashMap<String, Double> items,
                          LinkedHashMap<String, Double> spreads, LinkedHashMap<String, Double> moves,
                          LinkedHashMap<String, Double> teammates) {
    }
}