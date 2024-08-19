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

import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class PokemonAttackDefenseTagProvider implements PokemonTagProvider {
    private static final Logger log = LoggerFactory.getLogger(PokemonAttackDefenseTagProvider.class);
    private static final Set<String> OFFENSIVE_POKEMONS = Set.of("Deoxys-Speed", "Ribombee", "Ninetales-Alola",
            "Maushold", "Maushold-Four", "Grimmsnarl", "Torkoal");
    private static final Set<String> BALANCE_POKEMONS = Set.of("Ditto", "Tinkaton");

    @Autowired
    private PokemonStatsTagProvider pokemonStatsTagProvider;

    @Autowired
    private PokemonTypeTagProvider pokemonTypeTagProvider;

    @Autowired
    private PokemonAbilityTagProvider pokemonAbilityTagProvider;

    private static final String ATTACK_PATTERN = "ATTACKSTATS";
    private static final String DEFENSE_PATTERN = "DEFENCESTATS";
    private static final String SPA_PATTERN = "SPASTATS";
    private static final String SPD_PATTERN = "SPDSTATS";
    private static final String TYPE_PATTERN = "TYPE";
    private static final String AIBLITY_DEFENCE_PATTERN = "ABILITY_DEFENCE";
    private static final String AIBLITY_ATTACK_PATTERN = "ABILITY_ATTACK";

    @Override
    public void tag(PokemonInfo pokemonInfo) throws Exception {
        if (tagSpecifyPokemon(pokemonInfo)) {
            return;
        }

        //标记低层次的标签
        pokemonStatsTagProvider.tag(pokemonInfo);
        pokemonAbilityTagProvider.tag(pokemonInfo);
        pokemonTypeTagProvider.tag(pokemonInfo);
        HashSet<Tag> highLevelTagSet = new HashSet<>();//高层次的标签集合，用于替换之前的低层次
        log.debug("pokemon {} tag {}", pokemonInfo.getName(), pokemonInfo.getTags());
        //获取攻防种族level
        int levelAttack = getLevelOfStat(pokemonInfo, ATTACK_PATTERN);
        int levelDefence = getLevelOfStat(pokemonInfo, DEFENSE_PATTERN);
        int levelSpa = getLevelOfStat(pokemonInfo, SPA_PATTERN);
        int levelSpd = getLevelOfStat(pokemonInfo, SPD_PATTERN);

        float maxLevelAttack = Math.max(levelAttack, levelSpa);
        float maxLevelDefence = Math.max(levelDefence, levelSpd);
        //获取属性和特性加成value
        float typeValue = getValueOfType(pokemonInfo);
        float abilityDefenceValue = getValueOfAbility(pokemonInfo, AIBLITY_DEFENCE_PATTERN);
        float abilityAttackValue = getValueOfAbility(pokemonInfo, AIBLITY_ATTACK_PATTERN);
        maxLevelAttack += abilityAttackValue;
        maxLevelDefence += abilityDefenceValue + typeValue;
        log.debug("pokemon {} maxLevel_attack {} maxLevel_defence {} typeValue {} abilityDefenceValue {} " +
                        "abilityAttackValue {}",
                pokemonInfo.getName(), maxLevelAttack, maxLevelDefence, typeValue, abilityDefenceValue, abilityAttackValue);

        if (maxLevelAttack < 2 && maxLevelDefence < 2) {
            highLevelTagSet.add(Tag.WEAK);
        } else if (Math.abs(maxLevelAttack - maxLevelDefence) < 0.5) {
            highLevelTagSet.add(Tag.BALANCE); //相差不大 平衡标签
        } else if (maxLevelAttack - maxLevelDefence >= 2) {
            highLevelTagSet.add(Tag.ATTACK); //相差不大 平衡标签
        } else if (maxLevelDefence - maxLevelAttack >= 2) {
            highLevelTagSet.add(Tag.STAFF); //相差不大 平衡标签
        } else if (maxLevelAttack < 2 && maxLevelDefence >= 3) {
            highLevelTagSet.add(Tag.STAFF);   //攻击太小 受标签
        } else if (maxLevelAttack >= 2 && maxLevelDefence > maxLevelAttack) {
            highLevelTagSet.add(Tag.BALANCE_STAFF); //攻击还行 平衡受
        } else if (maxLevelDefence <= 2 && maxLevelAttack >= 3) {
            highLevelTagSet.add(Tag.ATTACK); //防御太小 攻标签
        } else if (maxLevelDefence > 2 && maxLevelAttack > maxLevelDefence) {
            highLevelTagSet.add(Tag.BALANCE_ATTACK);//防御还行 平衡攻
        } else {
            highLevelTagSet.add(Tag.WEAK);
        }
        //如果有场地标签，贴上
        if (pokemonInfo.getTags().contains(Tag.ABILITY_WEATHER)) {
            highLevelTagSet.add(Tag.ABILITY_WEATHER);
        }
        pokemonInfo.setTags(highLevelTagSet);
        log.debug("pokemon {} tag {}", pokemonInfo.getName(), pokemonInfo.getTags());
    }

    private boolean tagSpecifyPokemon(PokemonInfo pokemonInfo) {
        if (OFFENSIVE_POKEMONS.contains(pokemonInfo.getName())) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.ATTACK);
            pokemonInfo.setTags(tags);
            return true;
        }

        if (BALANCE_POKEMONS.contains(pokemonInfo.getName())) {
            HashSet<Tag> tags = new HashSet<>();
            tags.add(Tag.BALANCE);
            pokemonInfo.setTags(tags);
            return true;
        }
        return false;
    }

    private int getLevelOfStat(PokemonInfo pokemonInfo, String pattern) {
        for (Tag tag : pokemonInfo.getTags()) {
            String name = tag.name();
            if (name.contains(pattern)) {
                if (name.contains("BAD")) {
                    return 1;
                } else if (name.contains("NORMAL")) {
                    return 2;
                } else if (name.contains("GOOD")) {
                    return 3;
                } else if (name.contains("EXCELLENT")) {
                    return 4;
                } else if (name.contains("PRETTY")) {
                    return 5;
                }
            }
        }
        //没有贴种族标签
        throw new RuntimeException("pokemoninfo.tag does not hava full base stat tag!");
    }

    private float getValueOfType(PokemonInfo pokemonInfo) {
        for (Tag tag : pokemonInfo.getTags()) {
            String name = tag.name();
            if (name.contains(TYPE_PATTERN)) {
                if (name.contains("BAD")) {
                    if (pokemonInfo.getTags().contains(Tag.TYPE_MANYWEAK)) {
                        return -0.6f;
                    } else if (pokemonInfo.getTags().contains(Tag.TYPE_NORMALWEAK)) {
                        return -0.3f;
                    } else {
                        return 0.0f;
                    }
                } else if (name.contains("NORMAL")) {
                    return 0.25f;
                } else if (name.contains("GOOD")) {
                    return 0.5f;
                } else if (name.contains("EXCELLENT")) {
                    return 0.75f;
                } else if (name.contains("PRETTY")) {
                    return 1.0f;
                }
            }
        }
        return 0.0f;
    }

    private float getValueOfAbility(PokemonInfo pokemonInfo, String aiblityPattern) {
        for (Tag tag : pokemonInfo.getTags()) {
            String name = tag.name();
            if (name.contains(aiblityPattern)) {
                if (name.contains("BAD")) {
                    return 0.5f;
                } else if (name.contains("GOOD")) {
                    return 1.0f;
                } else if (name.contains("PRETTY")) {
                    return 2.0f;
                }
            }
        }
        return 0.0f;
    }
}