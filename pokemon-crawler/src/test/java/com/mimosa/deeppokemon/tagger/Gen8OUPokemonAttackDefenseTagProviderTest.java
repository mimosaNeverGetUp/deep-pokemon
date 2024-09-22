/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.crawler.PokemonInfoCrawler;
import com.mimosa.deeppokemon.entity.PokemonBuildSet;
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
import java.util.Collections;
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


    @Test
    void tagHighUsage() {
        assertTag("Landorus-Therian", Tag.ATTACK_MIX_SET, null);
        assertTag("Ferrothorn", Tag.DEFENSE_SET, null);
        assertTag("Dragapult", Tag.ATTACK_SET, null);
        assertTag("Zapdos", Tag.ATTACK_SET, null);
        assertTag("Garchomp", Tag.ATTACK_MIX_SET, null);
        assertTag("Heatran", Tag.ATTACK_MIX_SET, null);
        assertTag("Clefable", Tag.DEFENSE_SET, null);
        assertTag("Melmetal", Tag.DEFENSE_BULK_SET, null);
        assertTag("Weavile", Tag.ATTACK_SET, null);
        assertTag("Rillaboom", Tag.ATTACK_SET, null);
        assertTag("Urshifu-Rapid-Strike", Tag.ATTACK_MIX_SET, null);
        assertTag("Tapu Lele", Tag.ATTACK_SET, null);
        assertTag("Kartana", Tag.ATTACK_SET, null);
        assertTag("Tornadus-Therian", Tag.DEFENSE_SET, null);
        assertTag("Toxapex", Tag.DEFENSE_SET, null);
        assertTag("Slowbro", Tag.DEFENSE_SET, null);
        assertTag("Tapu Fini", Tag.DEFENSE_SET, null);
        assertTag("Blissey", Tag.DEFENSE_SET, null);
        assertTag("Corviknight", Tag.DEFENSE_SET, null);
        assertTag("Tapu Koko", Tag.ATTACK_SET, null);
        assertTag("Dragonite", Tag.ATTACK_MIX_SET, null);
        assertTag("Tyranitar", Tag.DEFENSE_BULK_SET, null);
        assertTag("Hatterene", Tag.ATTACK_SET, null);
        assertTag("Blacephalon", Tag.ATTACK_SET, null);
        assertTag("Excadrill", Tag.ATTACK_SET, null);
        assertTag("Blaziken", Tag.ATTACK_SET, null);
        assertTag("Volcarona", Tag.ATTACK_SET, null);
        assertTag("Buzzwole", Tag.ATTACK_BULK_SET, null);
        assertTag("Skarmory", Tag.DEFENSE_SET, null);
        assertTag("Pelipper", Tag.ATTACK_SET, null);
        assertTag("Mew", Tag.DEFENSE_SET, null);
        assertTag("Victini", Tag.DEFENSE_SET, null);
        assertTag("Slowking-Galar", Tag.DEFENSE_SET, null);
        assertTag("Zeraora", Tag.ATTACK_SET, null);
        assertTag("Barraskewda", Tag.ATTACK_SET, null);
        assertTag("Rotom-Wash", Tag.DEFENSE_MIX_SET, null);
        assertTag("Bisharp", Tag.ATTACK_SET, null);
        assertTag("Magnezone", Tag.ATTACK_MIX_SET, null);
        assertTag("Hippowdon", Tag.DEFENSE_MIX_SET, null);
        assertTag("Ninetales-Alola", Tag.ATTACK_SET, null);
        assertTag("Celesteela", Tag.DEFENSE_SET, null);
        assertTag("Aegislash", Tag.ATTACK_MIX_SET, null);
        assertTag("Dracozolt", Tag.ATTACK_SET, null);
        assertTag("Cloyster", Tag.ATTACK_SET, null);

        assertTag("Landorus-Therian", Tag.DEFENSE_MIX_SET, buildSet("Landorus-Therian", "Rocky Helmet", ""));
        assertTag("Landorus-Therian", Tag.DEFENSE_MIX_SET, buildSet("Landorus-Therian", "Leftovers", ""));
        assertTag("Zapdos", Tag.DEFENSE_MIX_SET, buildSet("Zapdos", "Heavy-Duty Boots", "Roost"));
        assertTag("Zapdos", Tag.ATTACK_MIX_SET, buildSet("Zapdos", "Heavy-Duty Boots", ""));
        assertTag("Zapdos", Tag.ATTACK_MIX_SET, buildSet("Zapdos", "Heavy-Duty Boots", ""));

        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Leftovers", ""));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Leftovers", "Protect"));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Leftovers", "Toxic"));
        assertTag("Garchomp", Tag.ATTACK_MIX_SET, buildSet("Garchomp", "Leftovers", "Swords Dance"));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Rocky Helmet", ""));

        assertTag("Heatran", Tag.DEFENSE_MIX_SET, buildSet("Heatran", "Leftovers", ""));
        assertTag("Heatran", Tag.DEFENSE_BULK_SET, buildSet("Heatran", "Leftovers", "Taunt"));

        assertTag("Clefable", Tag.DEFENSE_MIX_SET, buildSet("Clefable", "Sticky Barb", ""));
        assertTag("Clefable", Tag.DEFENSE_MIX_SET, buildSet("Clefable", "Choice Scarf", ""));
        assertTag("Clefable", Tag.DEFENSE_MIX_SET, buildSet("Clefable", "Leftovers", "Calm Mind"));
        assertTag("Clefable", Tag.DEFENSE_SET, buildSet("Clefable", "Leftovers", ""));

        assertTag("Melmetal", Tag.DEFENSE_BULK_SET, buildSet("Melmetal", "Leftovers", "Double Iron Bash"));
        assertTag("Melmetal", Tag.DEFENSE_BULK_SET, buildSet("Melmetal", "Assault Vest", "Double Iron Bash"));
        assertTag("Melmetal", Tag.ATTACK_BULK_SET, buildSet("Melmetal", "Choice Band", "Double Iron Bash"));

        assertTag("Rillaboom", Tag.ATTACK_MIX_SET, buildSet("Rillaboom", "Leftovers", ""));
        assertTag("Rillaboom", Tag.ATTACK_MIX_SET, buildSet("Rillaboom", "Assault Vest", ""));

        assertTag("Tornadus-Therian", Tag.DEFENSE_SET, buildSet("Tornadus-Therian", "Heavy-Duty Boots", ""));
        assertTag("Tornadus-Therian", Tag.ATTACK_MIX_SET, buildSet("Tornadus-Therian", "Heavy-Duty Boots", "Nasty Plot"));
        assertTag("Tornadus-Therian", Tag.DEFENSE_MIX_SET, buildSet("Tornadus-Therian", "Heavy-Duty Boots", "Taunt"));

        assertTag("Tapu Fini", Tag.ATTACK_MIX_SET, buildSet("Tapu Fini", "Choice Scarf", ""));
        assertTag("Tapu Fini", Tag.DEFENSE_SET, buildSet("Tapu Fini", "Leftovers", ""));
        assertTag("Tapu Fini", Tag.DEFENSE_MIX_SET, buildSet("Tapu Fini", "Leftovers", "Taunt"));
        assertTag("Tapu Fini", Tag.DEFENSE_MIX_SET, buildSet("Tapu Fini", "Leftovers", "Calm Mind"));

        assertTag("Dragonite", Tag.ATTACK_BULK_SET, buildSet("Dragonite", "Heavy-Duty Boots", "Roost", "Dragon Dance"));
        assertTag("Dragonite", Tag.DEFENSE_MIX_SET, buildSet("Dragonite", "Heavy-Duty Boots", "Roost"));

        assertTag("Tyranitar", Tag.ATTACK_BULK_SET, buildSet("Tyranitar", "Choice Band", ""));

        assertTag("Hatterene", Tag.ATTACK_MIX_SET, buildSet("Hatterene", "Leftovers", ""));
        assertTag("Hatterene", Tag.ATTACK_SET, buildSet("Hatterene", "Grassy Seed", "Calm Mind"));

        assertTag("Volcarona", Tag.ATTACK_MIX_SET, buildSet("Volcarona", "Heavy-Duty Boots", "Roost"));
        assertTag("Volcarona", Tag.ATTACK_MIX_SET, buildSet("Volcarona", "Heavy-Duty Boots", ""));

        assertTag("Buzzwole", Tag.DEFENSE_BULK_SET, buildSet("Buzzwole", "Rocky Helmet", "Roost", "Close Combat"));
        assertTag("Buzzwole", Tag.DEFENSE_BULK_SET, buildSet("Buzzwole", "Leftovers", "Roost", "Close Combat"));
        assertTag("Buzzwole", Tag.ATTACK_BULK_SET, buildSet("Buzzwole", "Choice Band", ""));

        assertTag("Pelipper", Tag.ATTACK_SET, buildSet("Pelipper", "", "Roost"));

        assertTag("Mew", Tag.DEFENSE_SET, buildSet("Mew", "Heavy-Duty Boots", "Taunt", "Roost"));
        assertTag("Mew", Tag.DEFENSE_MIX_SET, buildSet("Mew", "Leftovers", "Roost", "Cosmic Power", "Stored Power"));
        assertTag("Mew", Tag.ATTACK_SET, buildSet("Mew", "Focus Sash", "Stealth Rock", "Spikes"));

        assertTag("Victini", Tag.ATTACK_MIX_SET, buildSet("Victini", "Heavy-Duty Boots", "V-create"));
        assertTag("Victini", Tag.ATTACK_SET, buildSet("Victini", "Choice Scarf", "V-create"));

        assertTag("Rotom-Wash", Tag.DEFENSE_MIX_SET, buildSet("Rotom-Wash", "Leftovers", "Will-O-Wisp"));
        assertTag("Rotom-Wash", Tag.DEFENSE_MIX_SET, buildSet("Rotom-Wash", "Rocky Helmet", ""));
        assertTag("Rotom-Wash", Tag.ATTACK_MIX_SET, buildSet("Rotom-Wash", "Choice Scarf", ""));

        assertTag("Celesteela", Tag.ATTACK_MIX_SET, buildSet("Celesteela", "Power Herb", "Meteor Beam"));
        assertTag("Celesteela", Tag.DEFENSE_MIX_SET, buildSet("Celesteela", "Leftovers", "Leech Seed"));

        assertTag("Azumarill", Tag.ATTACK_MIX_SET, buildSet("Azumarill", "Leftovers", ""));
        assertTag("Azumarill", Tag.ATTACK_MIX_SET, buildSet("Azumarill", "Assault Vest", ""));
        assertTag("Azumarill", Tag.ATTACK_SET, buildSet("Azumarill", "Sitrus Berry", ""));
        assertTag("Azumarill", Tag.ATTACK_SET, buildSet("Azumarill", "Choice Band", ""));
        assertTag("Azumarill", Tag.ATTACK_SET, buildSet("Azumarill", "", "Belly Drum"));
        assertTag("Azumarill", Tag.DEFENSE_SET, buildSet("Azumarill", "", "Whirlpool"));

        assertTag("Aegislash", Tag.ATTACK_BULK_SET, buildSet("Aegislash", "Leftovers", ""));
        assertTag("Aegislash", Tag.ATTACK_MIX_SET, buildSet("Aegislash", "Choice Specs", ""));
        assertTag("Aegislash", Tag.ATTACK_MIX_SET, buildSet("Aegislash", "", "Swords Dance"));

        assertTag("Dracozolt", Tag.ATTACK_MIX_SET, buildSet("Dracozolt", "Leftovers", ""));
        assertTag("Cloyster", Tag.ATTACK_MIX_SET, buildSet("Cloyster", "Heavy-Duty Boots", "Shell Smash"));
        assertTag("Cloyster", Tag.ATTACK_SET, buildSet("Cloyster", "", "Shell Smash"));
    }

    public void assertTag(String name, Tag tag, PokemonBuildSet pokemonBuildSet) {
        PokemonInfo pokemonInfo = pokemonInfoCrawler.getPokemonInfo(name);
        tagProvider.tag(pokemonInfo, pokemonBuildSet);
        Assertions.assertEquals(1, pokemonInfo.getTags().size());
        Assertions.assertTrue(pokemonInfo.getTags().contains(tag));
    }

    public PokemonBuildSet buildSet(String name, String item, String... moves) {
        return new PokemonBuildSet(name, List.of(moves), null, Collections.singletonList(item), null);
    }

}