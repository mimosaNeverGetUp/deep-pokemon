/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.tour;

import com.mimosa.deeppokemon.entity.Battle;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document(collection = "toru_battle")
public class TourBattle extends Battle implements Serializable {
    protected String tourId;
    protected String stage;
    protected List<TourPlayer> smogonPlayer;
    protected String winSmogonPlayerName;

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

    public List<TourPlayer> getSmogonPlayer() {
        return smogonPlayer;
    }

    public void setSmogonPlayer(List<TourPlayer> smogonPlayer) {
        this.smogonPlayer = smogonPlayer;
    }

    public String getWinSmogonPlayerName() {
        return winSmogonPlayerName;
    }

    public void setWinSmogonPlayerName(String winSmogonPlayerName) {
        this.winSmogonPlayerName = winSmogonPlayerName;
    }
}