/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.tour;

import com.mimosa.deeppokemon.entity.Battle;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "toru_battle")
public class TourBattle extends Battle {
    protected List<TourPlayer> smogonPlayer;
    protected String winSmogonPlayerName;

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