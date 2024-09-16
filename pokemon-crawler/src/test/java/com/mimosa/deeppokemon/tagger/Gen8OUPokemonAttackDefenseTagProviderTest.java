/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.crawler.PokemonInfoCrawler;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;


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
        Set<Tag> tagSet = Set.of(Tag.ATTACK_SET, Tag.DEFENSE_SET, Tag.DEFENSE_MIX_SET, Tag.DEFENSE_BULK_SET,
                Tag.ATTACK_MIX_SET, Tag.ATTACK_BULK_SET, Tag.BALANCE_SET, Tag.BALANCE_BULK_SET);
        Assertions.assertTrue(tagProvider.supportTag("gen8ou"));
        Assertions.assertFalse(tagProvider.supportTag("gen9ou"));
        List<String> pokemons = Files.readAllLines(resource.getFile().toPath());
        for (String pokemon : pokemons) {
            PokemonInfo pokemonInfo = pokemonInfoCrawler.getPokemonInfo(pokemon);
            tagProvider.tag(pokemonInfo, null);
            Assertions.assertEquals(1, pokemonInfo.getTags().size());
            Assertions.assertTrue(tagSet.contains(pokemonInfo.getTags().stream().findFirst().orElseThrow()));
        }
    }

}