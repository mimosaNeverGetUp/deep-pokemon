package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.crawler.PokemonInfoCrawler;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class PokemonAttackDefenseTagProviderTest {
    @Autowired
    private PokemonInfoCrawler pokemonInfoCrawler;

    @Autowired
    private PokemonAttackDefenseTagProvider pokemonAttackDefenseTagProvider;

    @Test
    void tag() throws Exception {
        List<PokemonInfo> pokemonInfoList = pokemonInfoCrawler.craw();
        for (PokemonInfo pokemonInfo : pokemonInfoList) {
            pokemonAttackDefenseTagProvider.tag(pokemonInfo);
        }
    }
}