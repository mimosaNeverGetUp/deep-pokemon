/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.dto;

import com.mimosa.deeppokemon.entity.BattleTeam;

import java.time.LocalDate;
import java.util.List;

public final class BattleDto {
    private String id;
    private LocalDate date;
    private float avageRating;
    private List<String> type;
    private String winner;
    private List<BattleTeam> teams;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public float getAvageRating() {
        return avageRating;
    }

    public void setAvageRating(float avageRating) {
        this.avageRating = avageRating;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<BattleTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<BattleTeam> teams) {
        this.teams = teams;
    }
}