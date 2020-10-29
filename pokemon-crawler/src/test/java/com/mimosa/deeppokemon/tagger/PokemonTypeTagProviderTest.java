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
class PokemonTypeTagProviderTest {
    @Autowired
    private PokemonInfoCrawler pokemonInfoCrawler;

    @Autowired
    private PokemonTypeTagProvider pokemonTypeTagProvider;

    @Test
    void tag() throws IOException {
        List<PokemonInfo> pokemonInfoList = pokemonInfoCrawler.craw();
        for (int i = 0; i < pokemonInfoList.size(); i++) {
            PokemonInfo pokemonInfo =  pokemonInfoList.get(i);
            pokemonTypeTagProvider.tag(pokemonInfo);
            if ("OU".equals(pokemonInfo.getTier()) || "UU".equals(pokemonInfo.getTier())) {
                System.out.println(pokemonInfo.getName() + pokemonInfo.getTypes() + ':'
                        + pokemonInfo.getTags());
            }
        }
    }
}