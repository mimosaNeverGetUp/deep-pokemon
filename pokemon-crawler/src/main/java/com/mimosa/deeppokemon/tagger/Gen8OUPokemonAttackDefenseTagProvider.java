/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.entity.PokemonBuildSet;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class Gen8OUPokemonAttackDefenseTagProvider extends PokemonAttackDefenseTagProvider {
    private static final Logger log = LoggerFactory.getLogger(Gen8OUPokemonAttackDefenseTagProvider.class);

    public Gen8OUPokemonAttackDefenseTagProvider(PokemonStatsLevelCrawler pokemonStatsLevelCrawler, PokemonTypeTagProvider pokemonTypeTagProvider) {
        super(pokemonStatsLevelCrawler, pokemonTypeTagProvider);
    }

    @Override
    public boolean supportTag(String format) {
        return "gen8ou".equalsIgnoreCase(format);
    }

    @Override
    protected boolean tagSpecifyPokemon(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        switch (pokemonInfo.getName()) {
            case "Landorus-Therian" -> {
                return tagLandorus(pokemonInfo, pokemonBuildSet);
            }
            case "Heatran" -> {
                return tagHeatran(pokemonInfo, pokemonBuildSet);
            }
            case "Rillaboom" -> {
                return tagRillaboom(pokemonInfo, pokemonBuildSet);
            }
            case "Tapu Fini" -> {
                return tagTapuFini(pokemonInfo, pokemonBuildSet);
            }
            case "Clefable" -> {
                return tagClefable(pokemonInfo, pokemonBuildSet);
            }
            case "Mew" -> {
                return tagMew(pokemonInfo, pokemonBuildSet);
            }
            case "Victini" -> {
                return tagVictini(pokemonInfo, pokemonBuildSet);
            }
            case "Rotom-Wash" -> {
                return tagRotomWash(pokemonInfo, pokemonBuildSet);
            }
            case "Celesteela" -> {
                return tagCelesteela(pokemonInfo, pokemonBuildSet);
            }
            case "Azumarill" -> {
                return tagAzumarill(pokemonInfo, pokemonBuildSet);
            }
            case "Aegislash" -> {
                return tagAegislash(pokemonInfo, pokemonBuildSet);
            }
            case "Dracozolt" -> {
                return tagDracozolt(pokemonInfo, pokemonBuildSet);
            }
            case "Cloyster" -> {
                return tagCloyster(pokemonInfo, pokemonBuildSet);
            }
            case "Garchomp" -> {
                return tagGarchomp(pokemonInfo, pokemonBuildSet);
            }
            case "Tornadus-Therian" -> {
                return tagTornadusTherian(pokemonInfo, pokemonBuildSet);
            }
            case "Hatterene" -> {
                return tagHatterene(pokemonInfo, pokemonBuildSet);
            }
            default -> log.debug("Unknown pokemon:{}", pokemonInfo.getName());
        }
        if (ATTACK_SET_POKEMONS.contains(pokemonInfo.getName())) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        if (ATTACK_MIX_POKEMONS.contains(pokemonInfo.getName())) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        return false;
    }

    @Override
    protected boolean tagLandorus(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));
        if (topMoves.contains("Defog") || topMoves.contains("Toxic")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        if ("Rocky Helmet".equals(item) || "Leftovers".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        return false;
    }


    protected boolean tagHeatran(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));

        if ("Leftovers".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            if (topMoves.contains("Taunt")) {
                tags.add(Tag.DEFENSE_BULK_SET);
            } else {
                tags.add(Tag.DEFENSE_MIX_SET);
            }

            pokemonInfo.setTags(tags);
            return true;
        }

        return false;
    }

    protected boolean tagRillaboom(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);

        if ("Leftovers".equals(item) || "Assault Vest".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        return false;
    }

    protected boolean tagTapuFini(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);

        if ("Choice Scarf".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));
        if (topMoves.contains("Taunt") || topMoves.contains("Calm Mind")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        return false;
    }

    protected boolean tagVictini(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);

        if ("Heavy-Duty Boots".equals(item) || "Iron Ball".equals(item) || "Assault Vest".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        } else if ("Choice Scarf".equals(item) || "Choice Band".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        return false;
    }

    protected boolean tagCelesteela(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));

        if ("Power Herb".equals(item) || topMoves.contains("Autotomize")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        } else if ("Leftovers".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        return false;
    }

    protected boolean tagAegislash(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));

        if ("Leftovers".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_BULK_SET);
            pokemonInfo.setTags(tags);
            return true;
        } else if (topMoves.contains("Swords Dance") || topMoves.contains("Steel Beam")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        HashSet<Tag> tags = new HashSet<>();
        tags.add(Tag.ATTACK_MIX_SET);
        pokemonInfo.setTags(tags);
        return true;
    }

    protected boolean tagDracozolt(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);

        if ("Leftovers".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        HashSet<Tag> tags = new HashSet<>();
        tags.add(Tag.ATTACK_SET);
        pokemonInfo.setTags(tags);
        return true;
    }

    protected boolean tagCloyster(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);

        if ("Heavy-Duty Boots".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        HashSet<Tag> tags = new HashSet<>();
        tags.add(Tag.ATTACK_SET);
        pokemonInfo.setTags(tags);
        return true;
    }


    protected boolean tagTornadusTherian(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));
        if (topMoves.contains("Taunt") && !topMoves.contains("Nasty Plot")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        return false;
    }
}