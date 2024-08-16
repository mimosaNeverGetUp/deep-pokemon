/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.dto;

import java.time.LocalDate;
import java.util.Map;


public final class MonthlyPokemonMoveSetDto {
    private String name;
    private String format;
    private LocalDate date;
    private String statId;
    private Map<String, Double> abilities;
    private Map<String, Double> items;
    private Map<String, Double> spreads;
    private Map<String, Double> moves;
    private Map<String, Double> teammates;
    private MonthlyPokemonMoveSetDto lastMonthMoveSet;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatId() {
        return statId;
    }

    public void setStatId(String statId) {
        this.statId = statId;
    }

    public Map<String, Double> getAbilities() {
        return abilities;
    }

    public void setAbilities(Map<String, Double> abilities) {
        this.abilities = abilities;
    }

    public Map<String, Double> getItems() {
        return items;
    }

    public void setItems(Map<String, Double> items) {
        this.items = items;
    }

    public Map<String, Double> getSpreads() {
        return spreads;
    }

    public void setSpreads(Map<String, Double> spreads) {
        this.spreads = spreads;
    }

    public Map<String, Double> getMoves() {
        return moves;
    }

    public void setMoves(Map<String, Double> moves) {
        this.moves = moves;
    }

    public Map<String, Double> getTeammates() {
        return teammates;
    }

    public void setTeammates(Map<String, Double> teammates) {
        this.teammates = teammates;
    }

    public MonthlyPokemonMoveSetDto getLastMonthMoveSet() {
        return lastMonthMoveSet;
    }

    public void setLastMonthMoveSet(MonthlyPokemonMoveSetDto lastMonthMoveSet) {
        this.lastMonthMoveSet = lastMonthMoveSet;
    }
}