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
public class PokemonAttackDefenseTagProvider implements PokemonTagProvider {
    private static final Logger log = LoggerFactory.getLogger(PokemonAttackDefenseTagProvider.class);
    protected static final Set<String> ATTACK_SET_POKEMONS = Set.of("Ninetales", "Ribombee", "Ninetales-Alola");
    protected static final Set<String> ATTACK_MIX_POKEMONS = Set.of("Torkoal", "Araquanid");
    protected static final Set<String> LITTLE_BOOST_ATTACK_MOVES = Set.of("Scale Shot");

    protected static final Set<String> BOOST_ATTACK_MOVES = Set.of("Bulk Up",
            "Growth", "Coil", "Hone Claws", "No Retreat", "Victory Dance", "Work Up", "Curse", "Gear Up", "Howl",
            "Calm Mind", "Take Heart", "Meteor Beam", "Fiery Dance", "Electro Shot",
            "Geomancy", "Torch Song");

    protected static final Set<String> BOOST_MULTI_ATTACK_MOVES = Set.of("Swords Dance",
            "Dragon Dance", "Shell Smash", "Nasty Plot", "Tail Glow", "Quiver Dance", "Tidy Up");

    protected static final Set<String> HIGH_POWER_ATTACK_MOVES = Set.of("Brave Bird", "Head Smash", "Chloroblast", "Mind Blown",
            "Wood Hammer", "Wave Crash", "Head Charge", "Light of Ruin", "Double-Edge", "Steel Beam", "Flare Blitz",
            "High Jump Kick", "Outrage", "Explosion", "Self-Destruct", "Hyper Beam", "Thrash", "Petal Dance", "V-create",
            "Gigaton Hammer", "Eruption", "Blast Burn", "Hydro Cannon", "Water Spout", "Frenzy Plant", "Giga Impact",
            "Psycho Boost", "Boomburst", "Fleur Cannon", "Close Combat", "Raging Fury", "Population Bomb",
            "Double Iron Bash", "Stored Power");

    protected static final Set<String> RECOVERY_MOVES = Set.of("Jungle Healing", "Slack Off",
            "Synthesis", "Strength Sap", "Milk Drink", "Heal Order", "Ingrain", "Morning Sun", "Moonlight", "Aqua Ring",
            "Life Dew", "Soft-Boiled", "Rest", "Wish", "Roost", "Recover", "Shore Up");

    protected static final Set<String> OTHER_DEF_MOVES = Set.of("Will-O-Wisp", "Pain Split", "Thunder Wave");

    protected static final String TYPE_PATTERN = "TYPE";

    protected final PokemonStatsLevelCrawler pokemonStatsLevelCrawler;

    protected final PokemonTypeTagProvider pokemonTypeTagProvider;

    public PokemonAttackDefenseTagProvider(PokemonStatsLevelCrawler pokemonStatsLevelCrawler, PokemonTypeTagProvider pokemonTypeTagProvider) {
        this.pokemonStatsLevelCrawler = pokemonStatsLevelCrawler;
        this.pokemonTypeTagProvider = pokemonTypeTagProvider;
    }

    @Override
    public void tag(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (tagSpecifyPokemon(pokemonInfo, pokemonBuildSet)) {
            log.debug("pokemon {} tag {}", pokemonInfo.getName(), pokemonInfo.getTags());
            return;
        }

        pokemonTypeTagProvider.tag(pokemonInfo, pokemonBuildSet);
        log.debug("pokemon {} tag {}", pokemonInfo.getName(), pokemonInfo.getTags());
        //获取攻防种族level
        float levelAttack = pokemonStatsLevelCrawler.getAtkLevel(pokemonInfo);
        float levelDefence = pokemonStatsLevelCrawler.getDefLevel(pokemonInfo);
        float levelSpa = pokemonStatsLevelCrawler.getSatkLevel(pokemonInfo);
        float levelSpd = pokemonStatsLevelCrawler.getSpdLevel(pokemonInfo);

        float maxLevelAttack = Math.max(levelAttack, levelSpa);
        float maxLevelDefence = Math.max(levelDefence, levelSpd);
        if (levelDefence >= 3.5 && levelSpd >= 3.5) {
            // 双盾
            maxLevelDefence += 0.25F;
        }
        //获取属性和特性加成value
        float typeValue = getValueOfType(pokemonInfo);
        float abilityDefenceValue = getMaxDefLevelOfAbilities(pokemonInfo);
        float abilityAttackValue = getMaxAtkLevelOfAbilities(pokemonInfo);
        float setAttackValue = setAttackValue(pokemonBuildSet);
        float setDefValue = setDefValue(pokemonBuildSet);
        maxLevelAttack += abilityAttackValue;
        maxLevelAttack += setAttackValue;
        maxLevelDefence += abilityDefenceValue + typeValue;
        maxLevelDefence += setDefValue;

        log.debug("pokemon {} maxLevel_attack {} maxLevel_defence {} typeValue {} abilityDefenceValue {} " +
                        "abilityAttackValue {} setAttackValue {} setDefValue {} set {}",
                pokemonInfo.getName(), maxLevelAttack, maxLevelDefence, typeValue, abilityDefenceValue,
                abilityAttackValue, setAttackValue, setDefValue, pokemonBuildSet);

        Set<Tag> highLevelTags = getHighLevelTags(maxLevelAttack, maxLevelDefence);

        pokemonInfo.setTags(highLevelTags);
        log.debug("pokemon {} tag {}", pokemonInfo.getName(), pokemonInfo.getTags());
    }

    protected Set<Tag> getHighLevelTags(float maxLevelAttack, float maxLevelDefence) {
        Set<Tag> highLevelTagSet = new HashSet<>();
        if (maxLevelAttack > maxLevelDefence) {
            if (maxLevelDefence >= 4.25) {
                highLevelTagSet.add(Tag.ATTACK_BULK_SET);
            } else if (maxLevelDefence >= 3.5) {
                highLevelTagSet.add(Tag.ATTACK_MIX_SET);
            } else {
                highLevelTagSet.add(Tag.ATTACK_SET);
            }
        } else if (maxLevelDefence > maxLevelAttack) {
            if (maxLevelAttack >= 4.25) {
                highLevelTagSet.add(Tag.DEFENSE_BULK_SET);
            } else if (maxLevelAttack >= 3.5) {
                highLevelTagSet.add(Tag.DEFENSE_MIX_SET);
            } else {
                highLevelTagSet.add(Tag.DEFENSE_SET);
            }
        } else {
            if (maxLevelAttack >= 4.25) {
                highLevelTagSet.add(Tag.BALANCE_BULK_SET);
            } else {
                highLevelTagSet.add(Tag.BALANCE_SET);
            }
        }
        return highLevelTagSet;
    }

    protected float setAttackValue(PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return 0;
        }
        float setAttackValue = 0;
        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        if (item != null) {
            switch (item) {
                case "Choice Band", "Choice Specs" -> setAttackValue += 1;
                case "Booster Energy", "Weakness Policy" -> setAttackValue += 1;
                case "Life Orb" -> setAttackValue += 1;
                // 1.1x item
                case "Muscle Band", "Punching Glove", "Wise Glasses", "Loaded Dice", "Eject Button", "Focus Sash",
                     "Grassy Seed" -> setAttackValue += 0.5F;
                // 1.2x item
                case "Black Belt", "Black Glasses", "Charcoal", "Draco Plate", "Dragon Fang", "Dread Plate",
                     "Earth Plate", "Expert Belt", "Fairy Feather", "Fist Plate", "Flame Plate", "Hard Stone",
                     "Icicle Plate", "Insect Plate", "Iron Plate", "Magnet", "Meadow Plate", "Metal Coat", "Mind Plate",
                     "Miracle Seed", "Mystic Water", "Never-Melt Ice", "Odd Incense", "Pixie Plate", "Poison Barb",
                     "Rock Incense", "Rose Incense", "Sea Incense", "Sharp Beak", "Silk Scarf", "Silver Powder",
                     "Soft Sand", "Soul Dew", "Spell Tag", "Splash Plate", "Spooky Plate", "Stone Plate", "Toxic Plate",
                     "Twisted Spoon", "Wave Incense", "Zap Plate", "Sky Plate" -> setAttackValue += 0.5F;
                case "Power Herb" -> setAttackValue += 0.25;
                default -> log.debug("no attack item {}", item);
            }
        }

        float moveAttackValue = 0;
        if (pokemonBuildSet.moves() != null) {
            Set<String> topMoves = new HashSet<>(pokemonBuildSet.moves().subList(0,
                    Math.min(pokemonBuildSet.moves().size(), 4)));
            if (topMoves.stream().anyMatch(LITTLE_BOOST_ATTACK_MOVES::contains)) {
                moveAttackValue = 0.25F;
            }

            if (topMoves.stream().anyMatch(BOOST_ATTACK_MOVES::contains)) {
                moveAttackValue = 0.5F;
            }

            if (topMoves.contains("Body Press") && topMoves.contains("Iron Defense")) {
                moveAttackValue = 0.5F;
            }

            if (topMoves.stream().anyMatch(BOOST_MULTI_ATTACK_MOVES::contains)) {
                moveAttackValue = 0.75F;
            }

            if (topMoves.stream().anyMatch("Belly Drum"::equals)) {
                moveAttackValue = 1F;
            }

            if (topMoves.stream().anyMatch("Taunt"::equals)) {
                moveAttackValue += 0.25F;
            }

            if (topMoves.stream().anyMatch(HIGH_POWER_ATTACK_MOVES::contains)) {
                moveAttackValue += 0.25F;
            }
        }
        setAttackValue += moveAttackValue;
        return setAttackValue;
    }

    protected float setDefValue(PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return 0;
        }
        float setDefValue = 0;
        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        if (item != null) {
            switch (item) {
                case "Leftovers", "Heavy-Duty Boots", "Rocky Helmet" -> setDefValue += 0.25F;
                case "Assault Vest" -> setDefValue += 0.5F;
                case "Eviolite" -> setDefValue += 1F;
                default -> log.debug("no def item {}", item);
            }
        }

        if (pokemonBuildSet.moves() != null) {
            Set<String> topMoves = new HashSet<>(pokemonBuildSet.moves().subList(0,
                    Math.min(pokemonBuildSet.moves().size(), 4)));
            if (topMoves.stream().anyMatch(RECOVERY_MOVES::contains)) {
                setDefValue += 0.5F;
            } else if (topMoves.stream().anyMatch(OTHER_DEF_MOVES::contains)) {
                setDefValue += 0.25F;
            }
        }

        return setDefValue;
    }

    protected boolean tagSpecifyPokemon(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        switch (pokemonInfo.getName()) {
            case "Landorus-Therian" -> {
                return tagLandorus(pokemonInfo, pokemonBuildSet);
            }
            case "Iron Treads" -> {
                return tagIronTreads(pokemonInfo, pokemonBuildSet);
            }
            case "Zamazenta" -> {
                return tagZamazenta(pokemonInfo, pokemonBuildSet);
            }
            case "Samurott-Hisui" -> {
                return tagSamurottHisui(pokemonInfo, pokemonBuildSet);
            }
            case "Hatterene" -> {
                return tagHatterene(pokemonInfo, pokemonBuildSet);
            }
            case "Clefable" -> {
                return tagClefable(pokemonInfo, pokemonBuildSet);
            }
            case "Garchomp" -> {
                return tagGarchomp(pokemonInfo, pokemonBuildSet);
            }
            case "Serperior" -> {
                return tagSerperior(pokemonInfo, pokemonBuildSet);
            }
            case "Manaphy" -> {
                return tagManaphy(pokemonInfo, pokemonBuildSet);
            }
            case "Rotom-Wash" -> {
                return tagRotomWash(pokemonInfo, pokemonBuildSet);
            }
            case "Azumarill" -> {
                return tagAzumarill(pokemonInfo, pokemonBuildSet);
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

    protected boolean tagIronTreads(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        if (item != null) {
            switch (item) {
                case "Booster Energy" -> {
                    HashSet<Tag> tags = new HashSet<>();
                    tags.add(Tag.BALANCE_SET);
                    pokemonInfo.setTags(tags);
                    return true;
                }
                case "Leftovers", "Heavy-Duty Boots" -> {
                    HashSet<Tag> tags = new HashSet<>();
                    tags.add(Tag.DEFENSE_MIX_SET);
                    pokemonInfo.setTags(tags);
                    return true;
                }
                default -> log.debug("Unknown item:{}", item);
            }
        }
        return false;
    }

    protected boolean tagMew(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);

        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));
        if ("Focus Sash".equals(item) || "Colbur Berry".equals(item) || "Red Card".equals(item)) {
            if (topMoves.contains("Soft-Boiled") || topMoves.contains("Roost")) {
                return false;
            }

            if (topMoves.contains("Stealth Rock") || topMoves.contains("Spikes")) {
                HashSet<Tag> tags = new HashSet<>();
                tags.add(Tag.ATTACK_SET);
                pokemonInfo.setTags(tags);
                return true;
            }
        }
        if (topMoves.contains("Cosmic Power")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        return false;
    }

    protected boolean tagGarchomp(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));

        if ("Rocky Helmet".equals(item) && !topMoves.contains("Swords Dance")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        if ("Leftovers".equals(item) && (topMoves.contains("Protect") || topMoves.contains("Toxic"))) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        return false;
    }

    protected boolean tagSerperior(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));

        if (topMoves.contains("Synthesis")) {
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

    protected boolean tagRotomWash(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);

        if ("Leftovers".equals(item) || "Heavy-Duty Boots".equals(item) || "Rocky Helmet".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        } else if ("Choice Scarf".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        return false;
    }

    protected boolean tagAzumarill(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
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

        if ("Sitrus Berry".equals(item) || "Choice Band".equals(item) || topMoves.contains("Belly Drum")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_SET);
            pokemonInfo.setTags(tags);
            return true;
        } else if (topMoves.contains("Whirlpool") || topMoves.contains("Perish Song") || topMoves.contains("Rest")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        HashSet<Tag> tags = new HashSet<>();
        tags.add(Tag.ATTACK_MIX_SET);
        pokemonInfo.setTags(tags);
        return true;
    }

    protected boolean tagManaphy(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));

        if (topMoves.contains("Take Heart")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        if (topMoves.contains("Tail Glow") && !topMoves.contains("Rest")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        return false;
    }

    protected boolean tagLandorus(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));
        if (topMoves.contains("Earth Power") && !topMoves.contains("Earthquake")) {
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

    protected boolean tagHatterene(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        if ("Leftovers".equals(item) || "Assault Vest".equals(item) || "Eject Button".equals(item)
                || "Rocky Helmet".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        return false;
    }

    protected boolean tagClefable(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);

        if ("Choice Scarf".equals(item) || "Sticky Barb".equals(item)) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));
        if (topMoves.contains("Calm Mind")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.DEFENSE_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }
        return false;
    }

    protected boolean tagSamurottHisui(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }

        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));

        if ("Assault Vest".equals(item) || topMoves.contains("Rest")) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK_MIX_SET);
            pokemonInfo.setTags(tags);
            return true;
        }

        return false;
    }

    protected boolean tagZamazenta(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        if (pokemonBuildSet == null) {
            return false;
        }
        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        Set<String> topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() : new HashSet<>(pokemonBuildSet.moves().subList(0,
                Math.min(pokemonBuildSet.moves().size(), 4)));
        if (topMoves.contains("Close Combat") && !topMoves.contains("Iron Defense") && !topMoves.contains("Body Press")) {
            HashSet<Tag> tags = new HashSet<>();
            if ("Choice Band".equals(item) || "Life Orb".equals(item) || "Expert Belt".equals(item)) {
                tags.add(Tag.ATTACK_MIX_SET);
            } else {
                tags.add(Tag.ATTACK_BULK_SET);
            }
            pokemonInfo.setTags(tags);
            return true;
        }

        return false;
    }

    protected float getValueOfType(PokemonInfo pokemonInfo) {
        for (Tag tag : pokemonInfo.getTags()) {
            String name = tag.name();
            if (name.contains(TYPE_PATTERN)) {
                if (name.contains("BAD")) {
                    if (pokemonInfo.getTags().contains(Tag.TYPE_MANYWEAK)) {
                        return -0.3f;
                    } else if (pokemonInfo.getTags().contains(Tag.TYPE_NORMALWEAK)) {
                        return -0.15f;
                    } else {
                        return 0.0f;
                    }
                } else if (name.equals("TYPE_NORMAL")) {
                    return 0.05f;
                } else if (name.equals("TYPE_GOOD")) {
                    return 0.15f;
                } else if (name.equals("TYPE_EXCELLENT")) {
                    return 0.3f;
                } else if (name.equals("TYPE_PRETTY")) {
                    return 0.5f;
                }
            }
        }
        return 0.0f;
    }

    protected float getMaxAtkLevelOfAbilities(PokemonInfo pokemonInfo) {
        float maxAttackLevel = 0; //特性之中最好的进攻等级

        for (String ability : pokemonInfo.getAbilities()) {
            switch (ability) {
                case "Defiant", "Infiltrator", "Clear Body", "Torrent", "Blaze", "Overgrow", "Technician",
                     "Hydration", "Guard Dog", "Iron Fist", "Reckless", "Normalize", "Tough Claws", "Aerilate",
                     "Soul-Heart", "Beast Boost", "Grassy Terrain", "Berserk" ->
                        maxAttackLevel = Math.max(0.25F, maxAttackLevel);
                case "Good as Gold", "Sharpness", "Toxic Debris", "Poison Heal", "Libero", "Magic Bounce",
                     "Purifying Salt", "Grassy Surge", "Contrary", "Magic Guard", "Protean", "Mold Breaker",
                     "Unburden", "Battle Bond", "Swift Swim", "Snow Warning", "Tinted Lens", "Sand Stream",
                     "Neutralizing Gas", "Weak Armor", "Chlorophyll", "Sand Rush", "Speed Boost", "Toxic Chain",
                     "Skill Link", "Moxie", "Pixilate", "Psychic Surge", "Electric Surge", "Punk Rock", "Transistor",
                     "Water Bubble" -> maxAttackLevel = Math.max(0.5F, maxAttackLevel);
                case "Magnet Pull", "Supreme Overlord", "Slush Rush" ->
                        maxAttackLevel = Math.max(0.75F, maxAttackLevel);
                case "Drought", "Drizzle", "Guts", "Adaptability", "Huge Power", "Stance Change" ->
                        maxAttackLevel = Math.max(1.0F, maxAttackLevel);
                default -> log.debug("Unknown ability:{}", ability);
            }
        }
        return maxAttackLevel;
    }

    protected float getMaxDefLevelOfAbilities(PokemonInfo pokemonInfo) {
        float maxDefLevel = 0;

        for (String ability : pokemonInfo.getAbilities()) {
            switch (ability) {
                case "Sturdy", "Static", "Water Absorb", "Flash Fire", "Rough Skin", "Natural Cure",
                     "Thick Fat", "Flame Body", "Marvel Scale", "Storm Drain", "Sap Sipper", "Triage", "Good as Gold",
                     "Grassy Terrain", "Heatproof", "Sand Stream", "Disguise", "Hydration", "Grassy Surge" ->
                        maxDefLevel = Math.max(0.25F, maxDefLevel);
                case "Volt Absorb", "Levitate", "Stamina", "Dauntless Shield", "Multiscale", "Unaware", "Fluffy",
                     "Magic Bounce", "Vessel of Ruin", "Magic Guard" -> maxDefLevel = Math.max(0.5F, maxDefLevel);
                case "Regenerator", "Purifying Salt" -> maxDefLevel = Math.max(0.75F, maxDefLevel);
                case "Poison Heal", "Intimidate" -> maxDefLevel = Math.max(1.0F, maxDefLevel);
                case "Wonder Guard" -> maxDefLevel = Math.max(2.0F, maxDefLevel);
                default -> log.debug("Unknown ability:{}", ability);
            }
        }
        return maxDefLevel;
    }
}