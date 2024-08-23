/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.dto;

import com.mimosa.deeppokemon.entity.stat.monthly.Usage;

import java.io.Serializable;
import java.time.LocalDate;

public class MonthlyPokemonUsageDto implements Serializable {
    private int rank;
    private String name;
    private LocalDate date;
    private String format;
    private String statId;
    private String count;
    private Usage usage;
    private MonthlyPokemonUsageDto lastMonthUsage;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getStatId() {
        return statId;
    }

    public void setStatId(String statId) {
        this.statId = statId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public MonthlyPokemonUsageDto getLastMonthUsage() {
        return lastMonthUsage;
    }

    public void setLastMonthUsage(MonthlyPokemonUsageDto lastMonthUsage) {
        this.lastMonthUsage = lastMonthUsage;
    }
}