/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.crawler.PokemonInfoCrawler;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


@SpringBootTest
class Gen8OUPokemonAttackDefenseTagProviderTest {
    @Autowired
    private Gen8OUPokemonAttackDefenseTagProvider tagProvider;

    @Autowired
    private PokemonInfoCrawler pokemonInfoCrawler;

    @Value("classpath:gen8ou.csv")
    Resource resource;

    @Test
    void test() throws IOException {
        List<String> pokemons = Files.readAllLines(resource.getFile().toPath());
        for (String pokemon : pokemons) {
            PokemonInfo pokemonInfo = pokemonInfoCrawler.getPokemonInfo(pokemon);
            tagProvider.tag(pokemonInfo, null);
            System.out.println(String.format("name: %s tag: %s", pokemon, pokemonInfo.getTags()));
        }
    }

}