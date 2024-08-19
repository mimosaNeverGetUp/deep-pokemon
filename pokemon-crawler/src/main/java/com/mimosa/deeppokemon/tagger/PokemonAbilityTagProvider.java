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

import java.util.Arrays;
import java.util.HashSet;

@Component
public class PokemonAbilityTagProvider implements PokemonTagProvider {
    private static final Logger log = LoggerFactory.getLogger(PokemonAbilityTagProvider.class);

    private static final HashSet<String> ABILITIES_ATTACK_BAD = new HashSet<>();
    private static final HashSet<String> ABILITIES_ATTACK_GOOD = new HashSet<>();
    private static final HashSet<String> ABILITIES_ATTACK_PRETTY = new HashSet<>();

    private static final HashSet<String> ABILITIES_DEFENSE_BAD = new HashSet<>();
    private static final HashSet<String> ABILITIES_DEFENSE_GOOD = new HashSet<>();
    private static final HashSet<String> ABILITIES_DEFENSE_PRETTY = new HashSet<>();

    //硬编码，需要维护特性名单
    static {
        String[] normalAtkAbility = {"Iron Fist", "Skill Link", "Tinted Lens", "Reckless", "Defiant", "Infiltrator", "Moxie"
                , "Normalize", "Tough Claws", "Pixilate", "Aerilate", "Psychic Surge", "Electric Surge", "Grassy Surge",
                "Punk Rock", "Neutralizing Gas", "Toxic Chain", "Guard Dog"};
        String[] goodAtkAbility = {"Guts", "Adaptability", "Mold Breaker", "Contrary", "Protean", "Battle Bond",
                "Transistor", "Dragon's Maw", "Libero", "Soul-Heart", "Beast Boost", "Good as Gold", "Protosynthesis",
                "Quark Drive", "Water Bubble", "Sharpness", "Toxic Debris", "Grassy Terrain", "Swift Swim",
                "Tinted Lens", "Chlorophyll", "Sand Rush", "Speed Boost", "Technician"};
        String[] prettyAtkAbility = {"Huge Power", "Magnet Pull", "Stance Change", "Supreme Overlord", "Drought",
                "Drizzle", "Snow Warning", "Sand Stream"};

        String[] normalDefAbility = {"Volt Absorb", "Sturdy", "Static", "Water Absorb", "Flash Fire", "Levitate", "Rough Skin"
                , "Natural Cure", "Thick Fat", "Flame Body", "Marvel Scale", "Storm Drain", "Sap Sipper", "Stamina",
                "Triage", "Good as Gold", "Dauntless Shield", "Multiscale", "Grassy Terrain", "Heatproof", "Sand Stream"};
        String[] goodDefAbility = {"Unaware", "Regenerator", "Fluffy", "Magic Bounce", "Poison Heal", "Disguise",
                "Intimidate", "Vessel of Ruin",};
        String[] prettyDefAbility = {"Magic Guard", "Purifying Salt"};

        ABILITIES_ATTACK_BAD.addAll(Arrays.asList(normalAtkAbility));
        ABILITIES_ATTACK_GOOD.addAll(Arrays.asList(goodAtkAbility));
        ABILITIES_ATTACK_PRETTY.addAll(Arrays.asList(prettyAtkAbility));

        ABILITIES_DEFENSE_BAD.addAll(Arrays.asList(normalDefAbility));
        ABILITIES_DEFENSE_GOOD.addAll(Arrays.asList(goodDefAbility));
        ABILITIES_DEFENSE_PRETTY.addAll(Arrays.asList(prettyDefAbility));
    }

    @Override
    public void tag(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
        int maxAttackLevel = 0; //特性之中最好的进攻等级
        int maxDefenceLevel = 0;//特性之中最好的防守等级

        for (String ability : pokemonInfo.getAbilities()) {

            if (ABILITIES_ATTACK_PRETTY.contains(ability)) {
                maxAttackLevel = 3;
            } else if (ABILITIES_ATTACK_GOOD.contains(ability)) {
                maxAttackLevel = Math.max(maxAttackLevel, 2);
            } else if (ABILITIES_ATTACK_BAD.contains(ability)) {
                maxAttackLevel = Math.max(maxAttackLevel, 1);
            }


            if (ABILITIES_DEFENSE_PRETTY.contains(ability)) {
                maxDefenceLevel = 3;
            } else if (ABILITIES_DEFENSE_GOOD.contains(ability)) {
                maxDefenceLevel = Math.max(maxDefenceLevel, 2);
            } else if (ABILITIES_DEFENSE_BAD.contains(ability)) {
                maxDefenceLevel = Math.max(maxDefenceLevel, 1);
            }
        }

        switch (maxAttackLevel) {
            case 1:
                pokemonInfo.addTag(Tag.ABILITY_ATTACK_BAD);
                break;
            case 2:
                pokemonInfo.addTag(Tag.ABILITY_ATTACK_GOOD);
                break;
            case 3:
                pokemonInfo.addTag(Tag.ABILITY_ATTACK_PRETTY);
                break;
            default:
                log.debug("pokemon {} ability {} level {} is invalid?", pokemonInfo.getName(),
                        pokemonInfo.getAbilities(), maxAttackLevel);
        }
        switch (maxDefenceLevel) {
            case 1:
                pokemonInfo.addTag(Tag.ABILITY_DEFENCE_BAD);
                break;
            case 2:
                pokemonInfo.addTag(Tag.ABILITY_DEFENCE_GOOD);
                break;
            case 3:
                pokemonInfo.addTag(Tag.ABILITY_DEFENCE_PRETTY);
                break;
            default:
                log.debug("pokemon {} ability {} level {} is invalid?", pokemonInfo.getName(),
                        pokemonInfo.getAbilities(), maxDefenceLevel);
        }
    }
}