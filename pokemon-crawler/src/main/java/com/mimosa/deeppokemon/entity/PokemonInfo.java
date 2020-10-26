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
