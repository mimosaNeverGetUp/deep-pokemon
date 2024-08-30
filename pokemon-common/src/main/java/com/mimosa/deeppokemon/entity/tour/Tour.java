/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.tour;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
import java.util.Map;

@Document("tour")
public class Tour {
    @MongoId
    protected String id;
    protected List<String> tires;
    protected List<String> teams;
    protected Map<String, List<String>> tierPlayers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTires() {
        return tires;
    }

    public void setTires(List<String> tires) {
        this.tires = tires;
    }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }

    public Map<String, List<String>> getTierPlayers() {
        return tierPlayers;
    }

    public void setTierPlayers(Map<String, List<String>> tierPlayers) {
        this.tierPlayers = tierPlayers;
    }
}