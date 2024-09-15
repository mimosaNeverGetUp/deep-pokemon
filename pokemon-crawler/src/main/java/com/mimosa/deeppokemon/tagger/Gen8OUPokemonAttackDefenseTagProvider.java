/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.tagger;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Gen8OUPokemonAttackDefenseTagProvider extends PokemonAttackDefenseTagProvider{
    public Gen8OUPokemonAttackDefenseTagProvider(PokemonStatsLevelCrawler pokemonStatsLevelCrawler, PokemonTypeTagProvider pokemonTypeTagProvider) {
        super(pokemonStatsLevelCrawler, pokemonTypeTagProvider);
    }

    @Override
    public Set<String> getSupportFormat() {
        return Set.of("gen8ou");
    }
}