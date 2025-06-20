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
class Gen9NDPokemonAttackDefenseTagProviderTest {
    @Autowired
    private PokemonAttackDefenseTagProvider tagProvider;

    @Autowired
    private PokemonInfoCrawler pokemonInfoCrawler;

    @Value("classpath:gen9nd.csv")
    Resource resource;

    @Test
    void test() throws IOException {
        Set<Tag> tagSet = Set.of(Tag.ATTACK_SET, Tag.DEFENSE_SET, Tag.DEFENSE_MIX_SET, Tag.DEFENSE_BULK_SET,
                Tag.ATTACK_MIX_SET, Tag.ATTACK_BULK_SET, Tag.BALANCE_SET, Tag.BALANCE_BULK_SET);
        List<String> pokemons = Files.readAllLines(resource.getFile().toPath());
        for (String pokemon : pokemons) {
            PokemonInfo pokemonInfo = pokemonInfoCrawler.getPokemonInfo(pokemon);
            tagProvider.tag(pokemonInfo, null,"gen9nationaldex");
            Assertions.assertEquals(1, pokemonInfo.getTags().size());
            Assertions.assertTrue(tagSet.contains(pokemonInfo.getTags().stream().findFirst().orElseThrow()));
        }
    }

    @Test
    void tagHighUsage() {
        assertTag("Landorus-Therian", Tag.ATTACK_MIX_SET, null);
        assertTag("Gholdengo", Tag.ATTACK_MIX_SET, null);
        assertTag("Great Tusk", Tag.ATTACK_BULK_SET, null);
        assertTag("Terapagos", Tag.ATTACK_MIX_SET, null);
        assertTag("Alomomola", Tag.DEFENSE_SET, null);
        assertTag("Roaring Moon", Tag.ATTACK_MIX_SET, null);
        assertTag("Raging Bolt", Tag.ATTACK_MIX_SET, null);
        assertTag("Kingambit", Tag.ATTACK_BULK_SET, null);
        assertTag("Iron Valiant", Tag.ATTACK_SET, null);
        assertTag("Charizard", Tag.ATTACK_SET, null);
        assertTag("Tapu Lele", Tag.ATTACK_SET, null);
        assertTag("Ferrothorn", Tag.DEFENSE_SET, null);
        assertTag("Zamazenta", Tag.ATTACK_MIX_SET, null);
        assertTag("Diancie", Tag.ATTACK_SET, null);
        assertTag("Zapdos", Tag.ATTACK_SET, null);
        assertTag("Gliscor", Tag.DEFENSE_SET, null);
        assertTag("Tapu Koko", Tag.ATTACK_SET, null);
        assertTag("Ogerpon-Wellspring", Tag.ATTACK_SET, null);
        assertTag("Urshifu-*", Tag.ATTACK_MIX_SET, null);
        assertTag("Dragonite", Tag.ATTACK_MIX_SET, null);
        assertTag("Volcarona", Tag.ATTACK_SET, null);
        assertTag("Garchomp", Tag.ATTACK_MIX_SET, null);
        assertTag("Corviknight", Tag.DEFENSE_SET, null);
        assertTag("Ting-Lu", Tag.DEFENSE_SET, null);
        assertTag("Moltres", Tag.ATTACK_MIX_SET, null);
        assertTag("Slowking-Galar", Tag.DEFENSE_SET, null);
        assertTag("Toxapex", Tag.DEFENSE_SET, null);
        assertTag("Samurott-Hisui", Tag.ATTACK_SET, null);
        assertTag("Iron Treads", Tag.ATTACK_MIX_SET, null);
        assertTag("Hatterene", Tag.ATTACK_SET, null);
        assertTag("Melmetal", Tag.ATTACK_BULK_SET, null);
        assertTag("Lopunny", Tag.ATTACK_SET, null);
        assertTag("Kyurem", Tag.ATTACK_MIX_SET, null);
        assertTag("Kartana", Tag.ATTACK_SET, null);
        assertTag("Latios", Tag.ATTACK_MIX_SET, null);
        assertTag("Clodsire", Tag.DEFENSE_SET, null);
        assertTag("Tornadus-Therian", Tag.DEFENSE_SET, null);
        assertTag("Clefable", Tag.DEFENSE_SET, null);
        assertTag("Slowbro", Tag.DEFENSE_SET, null);
        assertTag("Kommo-o", Tag.DEFENSE_SET, null);
        assertTag("Victini", Tag.ATTACK_SET, null);
        assertTag("Iron Crown", Tag.BALANCE_SET, null);
        assertTag("Tyranitar", Tag.ATTACK_BULK_SET, null);
        assertTag("Ceruledge", Tag.ATTACK_SET, null);
        assertTag("Pelipper", Tag.ATTACK_SET, null);
        assertTag("Pecharunt", Tag.DEFENSE_SET, null);
        assertTag("Cinderace", Tag.ATTACK_SET, null);
        assertTag("Swampert", Tag.ATTACK_SET, null);
        assertTag("Excadrill", Tag.ATTACK_SET, null);
        assertTag("Greninja", Tag.ATTACK_SET, null);
        assertTag("Rillaboom", Tag.ATTACK_SET, null);
        assertTag("Iron Moth", Tag.ATTACK_SET, null);
        assertTag("Mawile", Tag.ATTACK_MIX_SET, null);
        assertTag("Glimmora", Tag.ATTACK_SET, null);
        assertTag("Archaludon", Tag.ATTACK_BULK_SET, null);
        assertTag("Weavile", Tag.ATTACK_SET, null);
        assertTag("Ursaluna", Tag.ATTACK_MIX_SET, null);
        assertTag("Medicham", Tag.ATTACK_SET, null);
        assertTag("Aegislash", Tag.ATTACK_MIX_SET, null);
        assertTag("Blaziken", Tag.ATTACK_SET, null);
        assertTag("Meowscarada", Tag.ATTACK_SET, null);
        assertTag("Blissey", Tag.DEFENSE_SET, null);
        assertTag("Serperior", Tag.ATTACK_SET, null);
        assertTag("Magnezone", Tag.ATTACK_MIX_SET, null);
        assertTag("Ribombee", Tag.ATTACK_SET, null);
        assertTag("Ogerpon-Cornerstone", Tag.ATTACK_SET, null);
        assertTag("Araquanid", Tag.ATTACK_MIX_SET, null);
        assertTag("Gyarados", Tag.ATTACK_MIX_SET, null);
        assertTag("Gardevoir", Tag.ATTACK_SET, null);
        assertTag("Celesteela", Tag.DEFENSE_SET, null);
        assertTag("Ninetales-Alola", Tag.ATTACK_SET, null);
        assertTag("Hippowdon", Tag.DEFENSE_MIX_SET, null);
        assertTag("Keldeo", Tag.ATTACK_SET, null);
        assertTag("Dondozo", Tag.DEFENSE_SET, null);
        assertTag("Torkoal", Tag.ATTACK_MIX_SET, null);
        assertTag("Hawlucha", Tag.ATTACK_SET, null);
        assertTag("Rotom-Wash", Tag.DEFENSE_MIX_SET, null);
        assertTag("Grimmsnarl", Tag.ATTACK_MIX_SET, null);
        assertTag("Chansey", Tag.DEFENSE_SET, null);
        assertTag("Blacephalon", Tag.ATTACK_SET, null);
        assertTag("Banette", Tag.ATTACK_SET, null);
        assertTag("Altaria", Tag.DEFENSE_MIX_SET, null);
        assertTag("Thundurus-Therian", Tag.ATTACK_SET, null);
        assertTag("Garganacl", Tag.DEFENSE_MIX_SET, null);
        assertTag("Tangrowth", Tag.DEFENSE_MIX_SET, null);
        assertTag("Sableye", Tag.DEFENSE_MIX_SET, null);
        assertTag("Aerodactyl", Tag.ATTACK_SET, null);
        assertTag("Ditto", Tag.BALANCE_SET, null);
        assertTag("Latios", Tag.ATTACK_MIX_SET, null);
        assertTag("Cresselia", Tag.DEFENSE_SET, null);
        assertTag("Zeraora", Tag.ATTACK_SET, null);
        assertTag("Tapu Bulu", Tag.ATTACK_MIX_SET, null);
        assertTag("Gallade", Tag.ATTACK_SET, null);
        assertTag("Manaphy", Tag.ATTACK_SET, null);
        assertTag("Shuckle", Tag.ATTACK_MIX_SET, null);
        assertTag("Dracozolt", Tag.ATTACK_SET, null);
        assertTag("Porygon2", Tag.DEFENSE_SET, null);
        assertTag("Aggron", Tag.DEFENSE_MIX_SET, null);
        assertTag("Manectric", Tag.ATTACK_SET, null);
        assertTag("Camerupt", Tag.ATTACK_SET, null);
        assertTag("Cloyster", Tag.ATTACK_SET, null);
        assertTag("Jirachi", Tag.ATTACK_MIX_SET, null);

        assertTag("Landorus-Therian", Tag.DEFENSE_MIX_SET, buildSet("Landorus-Therian", "Rocky Helmet", ""));
        assertTag("Landorus-Therian", Tag.DEFENSE_MIX_SET, buildSet("Landorus-Therian", "Leftovers", ""));
        assertTag("Gholdengo", Tag.ATTACK_BULK_SET, buildSet("Gholdengo", "", "Recover"));
        assertTag("Gholdengo", Tag.BALANCE_BULK_SET, buildSet("Gold Glasses", "Heavy-Duty Boots", "Recover", "Thunder Wave"));
        assertTag("Gholdengo", Tag.ATTACK_MIX_SET, buildSet("Gold Glasses", "Choice Scarf", ""));
        assertTag("Roaring Moon", Tag.ATTACK_BULK_SET, buildSet("Roaring Moon", "", "Recover"));
        assertTag("Raging Bolt", Tag.ATTACK_MIX_SET, buildSet("Raging Bolt", "Booster Energy", ""));
        assertTag("Raging Bolt", Tag.ATTACK_MIX_SET, buildSet("Raging Bolt", "Leftovers", "Calm Mind"));
        assertTag("Kingambit", Tag.ATTACK_BULK_SET, buildSet("Kingambit", "Black Glasses", "Swords Dance"));
        assertTag("Iron Valiant", Tag.ATTACK_SET, buildSet("Iron Valiant", "Booster Energy", ""));
        assertTag("Iron Valiant", Tag.ATTACK_SET, buildSet("Iron Valiant", "Choice Specs", ""));
        assertTag("Charizard", Tag.ATTACK_SET, buildSet("Charizard", "Charizardite Y", ""));
        assertTag("Charizard", Tag.ATTACK_MIX_SET, buildSet("Charizard", "Charizardite Y", "Roost"));
        assertTag("Tapu Lele", Tag.ATTACK_MIX_SET, buildSet("Tapu Lele", "Assault Vest", "Roost"));
        assertTag("Zamazenta", Tag.ATTACK_MIX_SET, buildSet("Zamazenta", "", "Close Combat"));
        assertTag("Zamazenta", Tag.ATTACK_BULK_SET, buildSet("Zamazenta", "Leftovers", "Iron Defense", "Body Press"));
        assertTag("Diancie", Tag.ATTACK_SET, buildSet("Diancie", "Diancite", ""));
        assertTag("Zapdos", Tag.DEFENSE_MIX_SET, buildSet("Zapdos", "Heavy-Duty Boots", "Roost"));
        assertTag("Zapdos", Tag.ATTACK_MIX_SET, buildSet("Zapdos", "Flyinium Z", "Roost"));
        assertTag("Zapdos", Tag.ATTACK_MIX_SET, buildSet("Zapdos", "Heavy-Duty Boots", ""));
        assertTag("Gliscor", Tag.DEFENSE_MIX_SET, buildSet("Gliscor", "", "Swords Dance"));
        assertTag("Dragonite", Tag.DEFENSE_MIX_SET, buildSet("Dragonite", "Heavy-Duty Boots", "Roost"));
        assertTag("Dragonite", Tag.DEFENSE_MIX_SET, buildSet("Dragonite", "", "Roost"));
        assertTag("Dragonite", Tag.ATTACK_MIX_SET, buildSet("Dragonite", "Heavy-Duty Boots", ""));
        assertTag("Dragonite", Tag.ATTACK_BULK_SET, buildSet("Dragonite", "Heavy-Duty Boots", "Roost", "Dragon Dance"));
        assertTag("Volcarona", Tag.ATTACK_MIX_SET, buildSet("Volcarona", "Heavy-Duty Boots", "Roost"));
        assertTag("Volcarona", Tag.ATTACK_MIX_SET, buildSet("Volcarona", "Heavy-Duty Boots", ""));
        assertTag("Volcarona", Tag.ATTACK_SET, buildSet("Volcarona", "Buginium Z", ""));
        assertTag("Volcarona", Tag.ATTACK_SET, buildSet("Volcarona", "Buginium Z", ""));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Leftovers", ""));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Leftovers", "Protect"));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Leftovers", "Toxic"));
        assertTag("Garchomp", Tag.ATTACK_MIX_SET, buildSet("Garchomp", "Leftovers", "Swords Dance"));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Rocky Helmet", ""));
        assertTag("Heatran", Tag.ATTACK_MIX_SET, null);
        assertTag("Moltres", Tag.DEFENSE_MIX_SET, buildSet("Moltres", "", "Roost"));
        assertTag("Moltres", Tag.ATTACK_MIX_SET, buildSet("Moltres", "Heavy-Duty Boots", ""));

        assertTag("Zapdos", Tag.DEFENSE_MIX_SET, buildSet("Zapdos", "Heavy-Duty Boots", "Roost"));
        assertTag("Zapdos", Tag.ATTACK_MIX_SET, buildSet("Zapdos", "Heavy-Duty Boots", ""));
        assertTag("Zapdos", Tag.ATTACK_MIX_SET, buildSet("Zapdos", "Heavy-Duty Boots", ""));

        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Leftovers", ""));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Leftovers", "Protect"));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Leftovers", "Toxic"));
        assertTag("Garchomp", Tag.ATTACK_MIX_SET, buildSet("Garchomp", "Leftovers", "Swords Dance"));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Rocky Helmet", ""));
        assertTag("Scizor", Tag.DEFENSE_BULK_SET, buildSet("Scizor", "Scizorite", ""));
        assertTag("Scizor", Tag.DEFENSE_BULK_SET, buildSet("Scizor", "Scizorite", "Roost"));
        assertTag("Scizor", Tag.ATTACK_MIX_SET, buildSet("Scizor", "Scizorite", "Swords Dance"));

        assertTag("Heatran", Tag.DEFENSE_MIX_SET, buildSet("Heatran", "Leftovers", ""));
        assertTag("Heatran", Tag.DEFENSE_BULK_SET, buildSet("Heatran", "Leftovers", "Taunt"));
        assertTag("Heatran", Tag.ATTACK_MIX_SET, buildSet("Heatran", "Grassium Z", "Magma Storm"));
        assertTag("Samurott-Hisui", Tag.ATTACK_MIX_SET, buildSet("Samurott-Hisui", "Assault Vest", ""));
        assertTag("Samurott-Hisui", Tag.ATTACK_MIX_SET, buildSet("Samurott-Hisui", "", "Rest"));
        assertTag("Iron Treads", Tag.ATTACK_MIX_SET, buildSet("Iron Treads", "Booster Energy", ""));
        assertTag("Iron Treads", Tag.DEFENSE_MIX_SET, buildSet("Iron Treads", "Leftovers", ""));
        assertTag("Iron Treads", Tag.DEFENSE_MIX_SET, buildSet("Iron Treads", "Heavy-Duty Boots", ""));
        assertTag("Iron Treads", Tag.DEFENSE_MIX_SET, buildSet("Iron Treads", "Assault Vest", ""));
        assertTag("Hatterene", Tag.ATTACK_MIX_SET, buildSet("Hatterene", "Leftovers", ""));
        assertTag("Hatterene", Tag.ATTACK_SET, buildSet("Hatterene", "Grassy Seed", "Calm Mind"));
        assertTag("Kyurem", Tag.ATTACK_BULK_SET, buildSet("Kyurem", "Leftovers", "Roost"));
        assertTag("Kyurem", Tag.ATTACK_BULK_SET, buildSet("Kyurem", "Heavy-Duty Boots", "Roost"));
        assertTag("Kyurem", Tag.ATTACK_MIX_SET, buildSet("Kyurem", "", "Roost"));
        assertTag("Kartana", Tag.ATTACK_MIX_SET, buildSet("Kartana", "", "Synthesis"));
        assertTag("Latios", Tag.ATTACK_MIX_SET, buildSet("Latios", "", ""));
        assertTag("Latios", Tag.ATTACK_MIX_SET, buildSet("Latios", "Latiosite", ""));
        assertTag("Latios", Tag.ATTACK_BULK_SET, buildSet("Latios", "Latiosite", "Roost"));

        assertTag("Melmetal", Tag.ATTACK_BULK_SET, buildSet("Melmetal", "Leftovers", "Double Iron Bash"));
        assertTag("Melmetal", Tag.ATTACK_BULK_SET, buildSet("Melmetal", "Assault Vest", "Double Iron Bash"));
        assertTag("Melmetal", Tag.ATTACK_BULK_SET, buildSet("Melmetal", "Choice Band", "Double Iron Bash"));

        assertTag("Tornadus-Therian", Tag.DEFENSE_SET, buildSet("Tornadus-Therian", "Heavy-Duty Boots", ""));
        assertTag("Tornadus-Therian", Tag.ATTACK_MIX_SET, buildSet("Tornadus-Therian", "Flyinium Z", ""));
        assertTag("Tornadus-Therian", Tag.ATTACK_MIX_SET, buildSet("Tornadus-Therian", "Heavy-Duty Boots", "Nasty Plot"));
        assertTag("Tornadus-Therian", Tag.DEFENSE_MIX_SET, buildSet("Tornadus-Therian", "Heavy-Duty Boots", "Taunt"));

        assertTag("Clefable", Tag.DEFENSE_MIX_SET, buildSet("Clefable", "Sticky Barb", ""));
        assertTag("Clefable", Tag.DEFENSE_MIX_SET, buildSet("Clefable", "Choice Scarf", ""));
        assertTag("Clefable", Tag.DEFENSE_MIX_SET, buildSet("Clefable", "Leftovers", "Calm Mind"));
        assertTag("Clefable", Tag.DEFENSE_SET, buildSet("Clefable", "Leftovers", ""));
        assertTag("Kommo-o", Tag.ATTACK_MIX_SET, buildSet("Kommo-o", "Kommonium Z", ""));
        assertTag("Kommo-o", Tag.DEFENSE_SET, buildSet("Kommo-o", "Leftovers", ""));
        assertTag("Kommo-o", Tag.DEFENSE_MIX_SET, buildSet("Kommo-o", "Leftovers", "Clangorous Soul"));
        assertTag("Victini", Tag.ATTACK_MIX_SET, buildSet("Victini", "Leftovers", ""));
        assertTag("Victini", Tag.ATTACK_MIX_SET, buildSet("Victini", "Heavy-Duty Boots", ""));
        assertTag("Victini", Tag.ATTACK_SET, buildSet("Victini", "Normalium Z", ""));
        assertTag("Victini", Tag.ATTACK_SET, buildSet("Victini", "Choice Band", ""));
        assertTag("Iron Crown", Tag.ATTACK_MIX_SET, buildSet("Iron Crown", "Booster Energy", "Calm Mind"));
        assertTag("Iron Crown", Tag.ATTACK_MIX_SET, buildSet("Iron Crown", "Booster Energy", ""));
        assertTag("Iron Crown", Tag.DEFENSE_MIX_SET, buildSet("Iron Crown", "Assault Vest", ""));
        assertTag("Iron Crown", Tag.DEFENSE_BULK_SET, buildSet("Iron Crown", "Assault Vest", "Psychic Noise"));
        assertTag("Pecharunt", Tag.DEFENSE_MIX_SET, buildSet("Pecharunt", "Ghostium Z", "Nasty Plot"));
        assertTag("Pecharunt", Tag.DEFENSE_MIX_SET, buildSet("Pecharunt", "", "Nasty Plot"));
        assertTag("Charizard", Tag.ATTACK_MIX_SET, buildSet("Charizard", "Charizardite X", "Roost"));
        assertTag("Charizard", Tag.ATTACK_SET, buildSet("Charizard", "Charizardite X", ""));
        assertTag("Swampert", Tag.ATTACK_BULK_SET, buildSet("Swampert", "Swampertite", ""));
        assertTag("Excadrill", Tag.ATTACK_MIX_SET, buildSet("Excadrill", "Leftovers", ""));
        assertTag("Venusaur", Tag.DEFENSE_BULK_SET, buildSet("Venusaur", "Venusaurite", ""));
        assertTag("Keldeo", Tag.ATTACK_MIX_SET, buildSet("Keldeo", "Leftovers", ""));
        assertTag("Latios", Tag.ATTACK_MIX_SET, buildSet("Latios", "", "Roost","Luster Purge"));
        assertTag("Latias", Tag.DEFENSE_BULK_SET, buildSet("Latias", "Latiasite", "Roost","Luster Purge"));

        assertTag("Rillaboom", Tag.ATTACK_MIX_SET, buildSet("Rillaboom", "Leftovers", ""));
        assertTag("Rillaboom", Tag.ATTACK_MIX_SET, buildSet("Rillaboom", "Assault Vest", ""));
        assertTag("Iron Moth", Tag.ATTACK_MIX_SET, buildSet("Iron Moth", "Heavy-Duty Boots", "Morning Sun"));
        assertTag("Iron Moth", Tag.ATTACK_MIX_SET, buildSet("Iron Moth", "Heavy-Duty Boots", ""));
        assertTag("Mawile", Tag.ATTACK_MIX_SET, buildSet("Mawile", "Mawilite", ""));
        assertTag("Aegislash", Tag.ATTACK_BULK_SET, buildSet("Aegislash", "Leftovers", ""));
        assertTag("Aegislash", Tag.ATTACK_MIX_SET, buildSet("Aegislash", "Choice Specs", ""));
        assertTag("Aegislash", Tag.ATTACK_MIX_SET, buildSet("Aegislash", "", "Swords Dance"));
        assertTag("Serperior", Tag.ATTACK_MIX_SET, buildSet("Serperior", "", "Synthesis"));
        assertTag("Celesteela", Tag.ATTACK_MIX_SET, buildSet("Celesteela", "Power Herb", "Meteor Beam"));
        assertTag("Celesteela", Tag.DEFENSE_MIX_SET, buildSet("Celesteela", "Leftovers", "Leech Seed"));
        assertTag("Garchomp", Tag.ATTACK_MIX_SET, buildSet("Garchomp", "Garchompite", ""));

        assertTag("Tapu Fini", Tag.ATTACK_MIX_SET, buildSet("Tapu Fini", "Choice Scarf", ""));
        assertTag("Tapu Fini", Tag.DEFENSE_SET, buildSet("Tapu Fini", "Leftovers", ""));
        assertTag("Tapu Fini", Tag.DEFENSE_MIX_SET, buildSet("Tapu Fini", "Leftovers", "Taunt"));
        assertTag("Tapu Fini", Tag.DEFENSE_MIX_SET, buildSet("Tapu Fini", "Leftovers", "Calm Mind"));

        assertTag("Dragonite", Tag.ATTACK_BULK_SET, buildSet("Dragonite", "Heavy-Duty Boots", "Roost", "Dragon Dance"));
        assertTag("Dragonite", Tag.DEFENSE_MIX_SET, buildSet("Dragonite", "Heavy-Duty Boots", "Roost"));

        assertTag("Tyranitar", Tag.ATTACK_BULK_SET, buildSet("Tyranitar", "Choice Band", ""));
        assertTag("Tyranitar", Tag.ATTACK_BULK_SET, buildSet("Tyranitar", "", ""));
        assertTag("Tyranitar", Tag.DEFENSE_BULK_SET, buildSet("Tyranitar", "Leftovers", ""));
        assertTag("Tyranitar", Tag.DEFENSE_BULK_SET, buildSet("Tyranitar", "Assault Vest", ""));
        assertTag("Tyranitar", Tag.DEFENSE_BULK_SET, buildSet("Tyranitar", "Heavy-Duty Boots", ""));
        assertTag("Tyranitar", Tag.ATTACK_BULK_SET, buildSet("Tyranitar", "Tyranitarite", ""));

        assertTag("Hatterene", Tag.ATTACK_MIX_SET, buildSet("Hatterene", "Leftovers", ""));
        assertTag("Hatterene", Tag.ATTACK_SET, buildSet("Hatterene", "Grassy Seed", "Calm Mind"));

        assertTag("Volcarona", Tag.ATTACK_MIX_SET, buildSet("Volcarona", "Heavy-Duty Boots", "Roost"));
        assertTag("Volcarona", Tag.ATTACK_MIX_SET, buildSet("Volcarona", "Heavy-Duty Boots", ""));

        assertTag("Buzzwole", Tag.DEFENSE_BULK_SET, buildSet("Buzzwole", "Rocky Helmet", "Roost", "Close Combat"));
        assertTag("Buzzwole", Tag.DEFENSE_BULK_SET, buildSet("Buzzwole", "Leftovers", "Roost", "Close Combat"));
        assertTag("Buzzwole", Tag.ATTACK_BULK_SET, buildSet("Buzzwole", "Choice Band", ""));

        assertTag("Pelipper", Tag.ATTACK_SET, buildSet("Pelipper", "", "Roost"));

        assertTag("Mew", Tag.DEFENSE_MIX_SET, buildSet("Mew", "Heavy-Duty Boots", "Taunt", "Roost"));
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

        assertTag("Dracozolt", Tag.ATTACK_SET, buildSet("Dracozolt", "Leftovers", ""));
        assertTag("Cloyster", Tag.ATTACK_MIX_SET, buildSet("Cloyster", "Heavy-Duty Boots", "Shell Smash"));
        assertTag("Cloyster", Tag.ATTACK_SET, buildSet("Cloyster", "", "Shell Smash"));
    }

    public void assertTag(String name, Tag tag, PokemonBuildSet pokemonBuildSet) {
        PokemonInfo pokemonInfo = pokemonInfoCrawler.getPokemonInfo(name);
        tagProvider.tag(pokemonInfo, pokemonBuildSet,"gen9nationaldex");
        Assertions.assertEquals(1, pokemonInfo.getTags().size());
        Assertions.assertTrue(pokemonInfo.getTags().contains(tag));
    }

    public PokemonBuildSet buildSet(String name, String item, String... moves) {
        return new PokemonBuildSet(name, List.of(moves), null, Collections.singletonList(item), null, null);
    }

}