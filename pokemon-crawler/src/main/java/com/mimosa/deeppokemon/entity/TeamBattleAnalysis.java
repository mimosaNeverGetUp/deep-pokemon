package com.mimosa.deeppokemon.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @program: deep-pokemon
 * @description: 队伍在一局比赛里的表现及度量分析
 * @author: mimosa
 * @create: 2020//12//07
 */
public class TeamBattleAnalysis {
    protected List<PokemonBattleAnalysis> pokemonBattleAnalysisList;

    public TeamBattleAnalysis(Team team) {
        List<PokemonBattleAnalysis> pokemonBattleAnalysisList = new ArrayList<>();
        for (Pokemon pokemon : team.getPokemons()) {
            pokemonBattleAnalysisList.add(new PokemonBattleAnalysis(pokemon.getName()));
        }
        this.pokemonBattleAnalysisList = pokemonBattleAnalysisList;
    }

    public List<PokemonBattleAnalysis> getPokemonBattleAnalysisList() {
        return pokemonBattleAnalysisList;
    }

    public void setPokemonBattleAnalysisList(List<PokemonBattleAnalysis> pokemonBattleAnalysisList) {
        this.pokemonBattleAnalysisList = pokemonBattleAnalysisList;
    }

}
