/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.tour;

import java.io.Serializable;

public class TourPlayer implements Serializable {
    protected String name;
    protected String tourPlayerId;
    protected String team;
    protected String region;

    public TourPlayer() {}

    public TourPlayer(String name, String team, String region) {
        this(name, null, team, region);
    }

    public TourPlayer(String name, String tourPlayerId, String team, String region) {
        this.name = name;
        this.tourPlayerId = tourPlayerId;
        this.team = team;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTourPlayerId() {
        return tourPlayerId;
    }

    public void setTourPlayerId(String tourPlayerId) {
        this.tourPlayerId = tourPlayerId;
    }
}