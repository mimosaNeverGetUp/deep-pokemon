package com.mimosa.deeppokemon.entity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Team {
    private  String playerName;
    private  String tier;
    private ArrayList<Pokemon> pokemons;
    private HashSet<Tag> tagSet=new HashSet<>();
    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public Team(ArrayList<Pokemon> pokemons) {
        this.pokemons = pokemons;
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
        return pokemons;
    }

    public void setPokemons(ArrayList<Pokemon> pokemons) {
        this.pokemons = pokemons;
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
        for (Pokemon pokemon : pokemons) {
            if (!team.pokemons.contains(pokemon)) {
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
