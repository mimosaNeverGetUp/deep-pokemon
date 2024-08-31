/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.tour;

import com.mimosa.deeppokemon.entity.BattleTeam;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tour_team")
public class TourTeam extends BattleTeam {
    protected String tourId;
    protected String stage;
    protected TourPlayer player;

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public TourPlayer getPlayer() {
        return player;
    }

    public void setPlayer(TourPlayer player) {
        this.player = player;
    }
}