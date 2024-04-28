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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * @program: deep-pokemon
 * @description: 根据宝可梦种族、特性、属性进行攻受向分类标签
 * @author: mimosa
 * @create: 2020//10//23
 */

@Component
public class PokemonAttackDefenseTagProvider implements PokemonTagProvider {
    @Autowired
    private PokemonStatsTagProvider pokemonStatsTagProvider;

    @Autowired
    private PokemonTypeTagProvider pokemonTypeTagProvider;

    @Autowired
    private PokemonAbilityTagProvider pokemonAbilityTagProvider;

    private final static String attackPattern = "ATTACKSTATS";
    private final static String defensePattern = "DEFENCESTATS";
    private final static String spaPattern = "SPASTATS";
    private final static String spdPattern = "SPDSTATS";
    private final static String spePattern = "SPESTATS";
    private final static String typePattern = "TYPE";
    private final static String aiblityDefencePattern = "ABILITY_DEFENCE";
    private final static String aiblityAttackPattern = "ABILITY_ATTACK";

    @Override
    public void tag(PokemonInfo pokemonInfo) throws Exception {

        //标记低层次的标签
        pokemonStatsTagProvider.tag(pokemonInfo);
        pokemonAbilityTagProvider.tag(pokemonInfo);
        pokemonTypeTagProvider.tag(pokemonInfo);
        HashSet<Tag> highLevelTagSet = new HashSet<>();//高层次的标签集合，用于替换之前的低层次
        //获取攻防种族level
        int level_attack = getLevelOfStat(pokemonInfo, attackPattern);
        int level_defence = getLevelOfStat(pokemonInfo, defensePattern);
        int level_spa = getLevelOfStat(pokemonInfo, spaPattern);
        int level_spd = getLevelOfStat(pokemonInfo, spdPattern);

        float maxLevel_attack = level_attack > level_spa ? level_attack : level_spa;
        float maxLevel_defence = level_defence > level_spd ? level_defence : level_spd;
        //获取属性和特性加成value
        float typeValue = getValueOfType(pokemonInfo);
        float abilityDefenceValue = getValueOfAbility(pokemonInfo, aiblityDefencePattern);
        float abilityAttackValue = getValueOfAbility(pokemonInfo, aiblityAttackPattern);
        maxLevel_attack += abilityAttackValue;
        maxLevel_defence += abilityDefenceValue + typeValue;
        if (maxLevel_attack < 2 && maxLevel_defence < 2) {
            highLevelTagSet.add(Tag.WEAK);
        } else if (Math.abs(maxLevel_attack - maxLevel_defence) < 0.5) {
            highLevelTagSet.add(Tag.BALANCE); //相差不大 平衡标签
        } else if (maxLevel_attack < 2 && maxLevel_defence >= 3) {
            highLevelTagSet.add(Tag.STAFF);   //攻击太小 受标签
        } else if (maxLevel_attack >= 2 && maxLevel_defence > maxLevel_attack) {
            highLevelTagSet.add(Tag.BALANCE_STAFF); //攻击还行 平衡受
        } else if (maxLevel_defence <= 2 && maxLevel_attack >= 3) {
            highLevelTagSet.add(Tag.ATTACK); //防御太小 攻标签
        } else if (maxLevel_defence > 2 && maxLevel_attack > maxLevel_defence) {
            highLevelTagSet.add(Tag.BALANCE_ATTACK);//防御还行 平衡攻
        } else {
            highLevelTagSet.add(Tag.WEAK);
        }
        //如果有场地标签，贴上
        if (pokemonInfo.getTags().contains(Tag.ABILITY_WEATHER)) {
            highLevelTagSet.add(Tag.ABILITY_WEATHER);
        }
        pokemonInfo.setTags(highLevelTagSet);
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
            if (name.contains(typePattern)) {
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