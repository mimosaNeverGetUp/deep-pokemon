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

package com.mimosa.deeppokemon.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: deep-pokemon
 * @description: pokemon info of ability,type,tag
 * @author: mimosa
 * @create: 2020//10//18
 */
public class PokemonInfo {
    BaseStats baseStats;
    List<Type> types;
    String tier;
    String name;
    List<String> abilities;
    Set<Tag> tags =new HashSet<>();
    public PokemonInfo() {
    }



    public PokemonInfo(BaseStats baseStats, List<Type> types, String tier, String name, List<String> abilities, Set<Tag> tags) {
        this.baseStats = baseStats;
        this.types = types;
        this.tier = tier;
        this.name = name;
        this.abilities = abilities;
        this.tags = tags;
    }

    public PokemonInfo(BaseStats baseStats, List<Type> types, String tier, String name, List<String> abilities) {
        this.baseStats = baseStats;
        this.types = types;
        this.tier = tier;
        this.name = name;
        this.abilities = abilities;
    }

    public void addTag(Tag tag){
        tags.add(tag);
    }
    public BaseStats getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(BaseStats baseStats) {
        this.baseStats = baseStats;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }



    @Override
    public String toString() {
        return "PokemonInfo{" +
                "baseStats=" + baseStats +
                ", types=" + types +
                ", tier='" + tier + '\'' +
                ", name='" + name + '\'' +
                ", abilities=" + abilities +
                ", tags=" + tags +
                '}';
    }
}
