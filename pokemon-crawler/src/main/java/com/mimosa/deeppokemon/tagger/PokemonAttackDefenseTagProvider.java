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
import com.mimosa.deeppokemon.tagger.rule.AbilityValue;
import com.mimosa.deeppokemon.tagger.rule.ItemValue;
import com.mimosa.deeppokemon.tagger.rule.TagSetting;
import com.mimosa.deeppokemon.tagger.rule.TagSettingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PokemonAttackDefenseTagProvider implements PokemonTagProvider {
    private static final Logger log = LoggerFactory.getLogger(PokemonAttackDefenseTagProvider.class);

    protected static final String TYPE_PATTERN = "TYPE";

    protected final PokemonStatsLevelCrawler pokemonStatsLevelCrawler;

    protected final PokemonTypeTagProvider pokemonTypeTagProvider;

    protected final TagSettingProvider tagSettingProvider;

    public PokemonAttackDefenseTagProvider(PokemonStatsLevelCrawler pokemonStatsLevelCrawler,
                                           PokemonTypeTagProvider pokemonTypeTagProvider, TagSettingProvider tagSettingProvider) {
        this.pokemonStatsLevelCrawler = pokemonStatsLevelCrawler;
        this.pokemonTypeTagProvider = pokemonTypeTagProvider;
        this.tagSettingProvider = tagSettingProvider;
    }

    @Override
    public void tag(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet, String format) {
        TagSetting tagSetting = tagSettingProvider.getTagSetting(format);
        if (tagSpecifyPokemon(pokemonInfo, pokemonBuildSet, tagSetting)) {
            log.debug("pokemon {} tag {}", pokemonInfo.getName(), pokemonInfo.getTags());
            return;
        }
        pokemonTypeTagProvider.tag(pokemonInfo, pokemonBuildSet, format);
        log.debug("pokemon {} tag {}", pokemonInfo.getName(), pokemonInfo.getTags());
        //获取攻防种族level
        float levelAttack = pokemonStatsLevelCrawler.getAtkLevel(pokemonInfo);
        float levelDefence = pokemonStatsLevelCrawler.getDefLevel(pokemonInfo);
        float levelSpa = pokemonStatsLevelCrawler.getSatkLevel(pokemonInfo);
        float levelSpd = pokemonStatsLevelCrawler.getSpdLevel(pokemonInfo);

        float maxLevelAttack = Math.max(levelAttack, levelSpa);
        float maxLevelDefence = Math.max(levelDefence, levelSpd);
        if (levelDefence >= tagSetting.goodDefThreshold() && levelSpd >= tagSetting.goodDefThreshold()) {
            // 双盾
            maxLevelDefence += 0.25F;
        }
        //获取属性和特性加成value
        float typeValue = getValueOfType(pokemonInfo);
        float abilityDefenceValue = getMaxDefLevelOfAbilities(pokemonInfo, tagSetting);
        float abilityAttackValue = getMaxAtkLevelOfAbilities(pokemonInfo, tagSetting);
        float setAttackValue = setAttackValue(pokemonBuildSet, tagSetting);
        float setDefValue = setDefValue(pokemonBuildSet, tagSetting);
        maxLevelAttack += abilityAttackValue;
        maxLevelAttack += setAttackValue;
        maxLevelDefence += abilityDefenceValue + typeValue;
        maxLevelDefence += setDefValue;

        log.debug("pokemon {} maxLevel_attack {} maxLevel_defence {} typeValue {} abilityDefenceValue {} " +
                        "abilityAttackValue {} setAttackValue {} setDefValue {} set {}",
                pokemonInfo.getName(), maxLevelAttack, maxLevelDefence, typeValue, abilityDefenceValue,
                abilityAttackValue, setAttackValue, setDefValue, pokemonBuildSet);

        Set<Tag> highLevelTags = getHighLevelTags(maxLevelAttack, maxLevelDefence, tagSetting);

        pokemonInfo.setTags(highLevelTags);
        log.debug("pokemon {} tag {}", pokemonInfo.getName(), pokemonInfo.getTags());
    }

    protected Set<Tag> getHighLevelTags(float maxLevelAttack, float maxLevelDefence, TagSetting tagSetting) {
        Set<Tag> highLevelTagSet = new HashSet<>();
        if (maxLevelAttack > maxLevelDefence) {
            if (maxLevelDefence >= tagSetting.bulkAtkThreshold()) {
                highLevelTagSet.add(Tag.ATTACK_BULK_SET);
            } else if (maxLevelDefence >= tagSetting.goodAtkThreshold()) {
                highLevelTagSet.add(Tag.ATTACK_MIX_SET);
            } else {
                highLevelTagSet.add(Tag.ATTACK_SET);
            }
        } else if (maxLevelDefence > maxLevelAttack) {
            if (maxLevelAttack >= tagSetting.bulkDefThreshold()) {
                highLevelTagSet.add(Tag.DEFENSE_BULK_SET);
            } else if (maxLevelAttack >= tagSetting.goodDefThreshold()) {
                highLevelTagSet.add(Tag.DEFENSE_MIX_SET);
            } else {
                highLevelTagSet.add(Tag.DEFENSE_SET);
            }
        } else {
            if (maxLevelAttack >= tagSetting.bulkAtkThreshold()) {
                highLevelTagSet.add(Tag.BALANCE_BULK_SET);
            } else {
                highLevelTagSet.add(Tag.BALANCE_SET);
            }
        }
        return highLevelTagSet;
    }

    protected float setAttackValue(PokemonBuildSet pokemonBuildSet, TagSetting tagSetting) {
        if (pokemonBuildSet == null) {
            return 0;
        }
        float setAttackValue = 0;
        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        if (item != null) {
            ItemValue itemValue = tagSettingProvider.getItemValue(tagSetting, item);
            setAttackValue += itemValue == null ? 0F : itemValue.atkValue().floatValue();
        }

        float moveAttackValue = 0;
        if (pokemonBuildSet.moves() != null) {
            Set<String> topMoves = new HashSet<>(pokemonBuildSet.moves().subList(0,
                    Math.min(pokemonBuildSet.moves().size(), 4)));

            moveAttackValue = (float) tagSettingProvider.getMoveAtkValue(tagSetting, topMoves);
            if (topMoves.contains("Body Press") && topMoves.contains("Iron Defense")) {
                moveAttackValue = Math.max(moveAttackValue, 0.5F);
            }
        }
        setAttackValue += moveAttackValue;
        return setAttackValue;
    }

    protected float setDefValue(PokemonBuildSet pokemonBuildSet, TagSetting tagSetting) {
        if (pokemonBuildSet == null) {
            return 0;
        }
        float setDefValue = 0;
        List<String> items = pokemonBuildSet.items();
        String item = items == null || items.isEmpty() ? null : items.get(0);
        if (item != null) {
            ItemValue itemValue = tagSettingProvider.getItemValue(tagSetting, item);
            setDefValue += itemValue == null ? 0F : itemValue.defValue().floatValue();
        }

        if (pokemonBuildSet.moves() != null) {
            Set<String> topMoves = new HashSet<>(pokemonBuildSet.moves().subList(0,
                    Math.min(pokemonBuildSet.moves().size(), 4)));
            setDefValue += (float) tagSettingProvider.getMoveDefValue(tagSetting, topMoves);
        }

        return setDefValue;
    }

    protected boolean tagSpecifyPokemon(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet, TagSetting tagSetting) {
        Set<Tag> pokemonTags = tagSettingProvider.getPokemonTags(tagSetting, pokemonInfo.getName(), pokemonBuildSet);
        if (pokemonTags.isEmpty()) {
            return false;
        } else {
            pokemonInfo.setTags(pokemonTags);
            return true;
        }
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

    protected float getMaxAtkLevelOfAbilities(PokemonInfo pokemonInfo, TagSetting tagSetting) {
        float maxAttackLevel = 0; //特性之中最好的进攻等级

        for (String ability : pokemonInfo.getAbilities()) {
            AbilityValue abilityValue = tagSettingProvider.getAbilityValue(tagSetting, ability);
            if (abilityValue != null) {
                maxAttackLevel = Math.max(maxAttackLevel, abilityValue.atkValue().floatValue());
            } else {
                log.debug("Unknown ability:{}", ability);
            }
        }
        return maxAttackLevel;
    }

    protected float getMaxDefLevelOfAbilities(PokemonInfo pokemonInfo, TagSetting tagSetting) {
        float maxDefLevel = 0;

        for (String ability : pokemonInfo.getAbilities()) {
            AbilityValue abilityValue = tagSettingProvider.getAbilityValue(tagSetting, ability);
            if (abilityValue != null) {
                maxDefLevel = Math.max(maxDefLevel, abilityValue.defValue().floatValue());
            } else {                log.debug("Unknown ability:{}", ability);
            }
        }
        return maxDefLevel;
    }
}