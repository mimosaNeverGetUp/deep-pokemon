/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.status;

import com.mimosa.deeppokemon.analyzer.entity.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerStatus {
    private final Map<String, String> pokemonNickNameMap;
    private final Map<String, PokemonStatus> pokemonStatusMap;
    private final List<Side> sideList;
    private final Map<Integer, String> turnStartPokemonNames;
    private String turnStartPokemonName;
    private String activePokemonName;

    public PlayerStatus() {
        this.pokemonNickNameMap = new HashMap<>();
        this.pokemonStatusMap = new HashMap<>();
        this.sideList = new ArrayList<>();
        this.activePokemonName = null;
        this.turnStartPokemonName = null;
        this.turnStartPokemonNames = new HashMap<>();
    }

    public String getPokemonName(String nickName) {
        return pokemonNickNameMap.get(nickName);
    }

    public void setPokemonNickNameMap(String nickName, String pokemonName) {
        pokemonNickNameMap.put(nickName, pokemonName);
    }

    public String getActivePokemonName() {
        return activePokemonName;
    }

    public void setActivePokemonName(String activePokemonName) {
        this.activePokemonName = activePokemonName;
    }

    public Map<String, PokemonStatus> getPokemonStatusMap() {
        return pokemonStatusMap;
    }

    public PokemonStatus getPokemonStatus(String pokemonName) {
        return pokemonStatusMap.get(pokemonName);
    }

    public void setPokemonStatus(String pokemonName, PokemonStatus pokemonStatus) {
        pokemonStatusMap.put(pokemonName, pokemonStatus);
    }

    public String getTurnStartPokemonName() {
        return turnStartPokemonName;
    }


    public String getTurnStartPokemonName(int turn) {
        return turnStartPokemonNames.get(turn);
    }

    public void setTurnStartPokemonName(int turn, String turnStartPokemonName) {
        this.turnStartPokemonName = turnStartPokemonName;
        turnStartPokemonNames.put(turn, turnStartPokemonName);
    }

    public void addSide(Side side) {
        sideList.add(side);
    }

    public List<Side> getSideList() {
        return sideList;
    }

    public List<Side> getSideListByName(String name) {
        return sideList.stream().filter(side -> side.name().equals(name)).collect(Collectors.toList());
    }

    public void changePokemonName(String nameBefore, String changeName) {
        PokemonStatus pokemonStatus = pokemonStatusMap.remove(nameBefore);
        pokemonStatus.setPokemonName(changeName);
        pokemonStatusMap.put(changeName, pokemonStatus);
    }
}