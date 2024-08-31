/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.tour;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

@Document("tour_player_record")
public class TourPlayerRecord implements Serializable {
    @MongoId
    protected String id;
    protected String name;
    protected String format;
    protected String team;
    protected String tourId;
    protected int total;
    protected int win;
    protected int loss;
    protected int winDif;
    protected float winRate;

    public TourPlayerRecord() {}

    public TourPlayerRecord(String id, String name, String tourId, String format, String team) {
        this.id = id;
        this.name = name;
        this.tourId = tourId;
        this.format = format;
        this.team = team;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLoss() {
        return loss;
    }

    public void setLoss(int loss) {
        this.loss = loss;
    }

    public float getWinRate() {
        return winRate;
    }

    public void setWinRate(float winRate) {
        this.winRate = winRate;
    }

    public int getWinDif() {
        return winDif;
    }

    public void setWinDif(int winDif) {
        this.winDif = winDif;
    }
}