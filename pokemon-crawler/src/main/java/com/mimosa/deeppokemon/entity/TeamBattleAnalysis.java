/*
 * The MIT License
 *
 * Copyright (c) [2022] [Xiaocong Huang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mimosa.deeppokemon.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: deep-pokemon
 * @description: 队伍在一局比赛里的表现及度量分析
 * @author: mimosa
 * @create: 2020//12//07
 */
public class TeamBattleAnalysis {
    protected Map<String,PokemonBattleAnalysis> pokemonBattleAnalysisMap;

    public TeamBattleAnalysis(Team team) {
        Map<String, PokemonBattleAnalysis> pokemonBattleAnalysisMap = new HashMap<>();
        for (Pokemon pokemon : team.getPokemons()) {
            pokemonBattleAnalysisMap.put(pokemon.getName(),new PokemonBattleAnalysis(pokemon.getName()));
        }
        this.pokemonBattleAnalysisMap = pokemonBattleAnalysisMap;
    }

    public Map<String,PokemonBattleAnalysis> getPokemonBattleAnalysisMap() {
        return pokemonBattleAnalysisMap;
    }

    public PokemonBattleAnalysis getPokemonBattleAnalysis(String pokemonName) {
        return pokemonBattleAnalysisMap.get(pokemonName);
    }

    public void setPokemonBattleAnalysisMap(Map<String,PokemonBattleAnalysis> pokemonBattleAnalysisMap) {
        this.pokemonBattleAnalysisMap = pokemonBattleAnalysisMap;
    }

}
