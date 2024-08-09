/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.util;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Team;

import java.util.ArrayList;

public class BattleBuilder {
    private Battle battle;

    public BattleBuilder() {
        battle = new Battle();
        battle.setTeams(new Team[]{new Team(new ArrayList<>()), new Team(new ArrayList<>())});
    }

    public BattleBuilder addPokemon(int playerNumber, String pokemonName) {
        battle.getTeams()[playerNumber - 1].addPokemon(new Pokemon(pokemonName));
        return this;
    }

    public  Battle build() {return battle;}
}