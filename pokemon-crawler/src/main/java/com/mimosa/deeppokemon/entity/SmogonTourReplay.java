/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import com.mimosa.deeppokemon.entity.tour.TourPlayer;

import java.util.List;

public class SmogonTourReplay extends Replay{
    protected String tourName;
    protected String stage;
    protected List<TourPlayer> tourPlayers;

    public SmogonTourReplay() {
        super();
    }

    public SmogonTourReplay(String id) {
        super(id);
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public List<TourPlayer> getTourPlayers() {
        return tourPlayers;
    }

    public void setTourPlayers(List<TourPlayer> tourPlayers) {
        this.tourPlayers = tourPlayers;
    }
}