package com.mimosa.deeppokemon.entity;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Team {
    private  String playerName;
    private  String tier;
    private ArrayList<Pokemon> pokemons;

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
        return  " Team:" +"\n"+
                "       playerName:'" + playerName + '\'' +"\n"+
                "       tier:'" + tier + '\'' +"\n"+
                        pokemons +
                "\n\n";
    }
}
