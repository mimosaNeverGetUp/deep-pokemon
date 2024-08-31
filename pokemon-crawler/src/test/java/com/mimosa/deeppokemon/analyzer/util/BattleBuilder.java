/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.util;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class BattleBuilder {
    private final Battle battle;

    public BattleBuilder() {
        battle = new Battle();
        BattleTeam battleTeam1 = new BattleTeam();
        battleTeam1.setPokemons(new ArrayList<>());
        BattleTeam battleTeam2 = new BattleTeam();
        battleTeam2.setPokemons(new ArrayList<>());

        battle.setBattleTeams(List.of(battleTeam1, battleTeam2));
    }

    public BattleBuilder addPokemon(int playerNumber, String pokemonName) {
        battle.getBattleTeams().get(playerNumber - 1).getPokemons().add(new Pokemon(pokemonName));
        return this;
    }

    public BattleBuilder setPlayerName(int playerNumber, String playerName) {
        battle.getBattleTeams().get(playerNumber - 1).setPlayerName(playerName);
        return this;
    }

    public BattleBuilder setRating(int playerNumber, float rating) {
        battle.getBattleTeams().get(playerNumber - 1).setRating(rating);
        return this;
    }

    public  Battle build() {return battle;}
}