/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.status;

import com.mimosa.deeppokemon.analyzer.entity.ActivateStatus;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonStatus {
    private String pokemonName;
    private BigDecimal health;
    private Status status;
    private int lastActivateTurn;
    private Map<String, EventTarget> buffOf;
    private List<ActivateStatus> activateStatusList;

    public PokemonStatus(String pokemonName) {
        this.pokemonName = pokemonName;
        this.health = BigDecimal.valueOf(100.0);
        this.lastActivateTurn = 0;
        this.status = null;
        this.buffOf = new HashMap<>();
        activateStatusList = new ArrayList<>();
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public BigDecimal getHealth() {
        return health;
    }

    public void setHealth(BigDecimal health) {
        this.health = health;
    }

    public void setPokemonName(String pokemonName) {
        this.pokemonName = pokemonName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getLastActivateTurn() {
        return lastActivateTurn;
    }

    public EventTarget getBuffOf(String buff) {
        return buffOf.get(buff);
    }

    public void setBuffOf(String buff, EventTarget buffOf) {
        this.buffOf.put(buff, buffOf);
    }

    public void addActivateStatus(ActivateStatus activateStatus) {
        activateStatusList.add(activateStatus);
    }

    public List<ActivateStatus> getActivateStatusList() {
        return activateStatusList;
    }

    public void setLastActivateTurn(int lastActivateTurn) {
        this.lastActivateTurn = lastActivateTurn;
    }
}