package com.mimosa.deeppokemon.entity;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Team {
    private  String playerName;
    private  String tier;
    private Map<String,Pokemon> pokemons;
    private HashSet<Tag> tagSet=new HashSet<>();
    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public Team(ArrayList<Pokemon> pokemons) {
        this.pokemons = pokemons.stream().collect(Collectors.toMap(Pokemon::getName, Function.identity()));
    }

    public Team() {
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }



    public ArrayList<Pokemon> getPokemons() {
        return new ArrayList<>(pokemons.values());
    }

    public Map<String,Pokemon> getPokemonMap() {
        return pokemons;
    }

    public void setPokemons(ArrayList<Pokemon> pokemons) {
        this.pokemons = pokemons.stream().collect(Collectors.toMap(Pokemon::getName, Function.identity()));
    }

    public HashSet<Tag> getTagSet() {
        return tagSet;
    }

    public void setTagSet(HashSet<Tag> tagSet) {
        this.tagSet = tagSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        for (Pokemon pokemon : pokemons.values()) {
            if (!team.pokemons.containsValue(pokemon)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return pokemons != null ? pokemons.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Team{" +
                "playerName='" + playerName + '\'' +
                ", tier='" + tier + '\'' +
                ", pokemons=" + pokemons +
                ", tagSet=" + tagSet +
                '}';
    }
}
