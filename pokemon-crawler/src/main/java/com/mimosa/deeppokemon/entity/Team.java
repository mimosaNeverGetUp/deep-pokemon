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

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Team implements Serializable {
    private  String playerName;
    private  String tier;
    private ArrayList<Pokemon> pokemons;
    private HashSet<Tag> tagSet=new HashSet<>();

    /**
     * 非持久化变量,pokemons的map形式，方便查询
     * map形式可以替代list,但由于需要兼容旧版本，需要保留pokemons变量,待重构解决
     * key:pokemonName value: pokemon
     */
    @Transient
    private Map<String, Pokemon> pokemonMap;

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public Team(ArrayList<Pokemon> pokemons) {
        this.pokemons = pokemons;
        pokemonMap = pokemons.stream().collect(Collectors.toMap(Pokemon::getName, Function.identity()));
    }

    public Team() {
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public ArrayList<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(ArrayList<Pokemon> pokemons) {
        this.pokemons = pokemons;
        pokemonMap = pokemons.stream().collect(Collectors.toMap(Pokemon::getName, Function.identity(), (p1, p2) -> p1));
    }

    public HashSet<Tag> getTagSet() {
        return tagSet;
    }

    public void setTagSet(HashSet<Tag> tagSet) {
        this.tagSet = tagSet;
    }

    public Pokemon getPokemon(String pokemonName) {
        return pokemonMap.get(pokemonName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return pokemons == null ? ((Team) o).pokemons == null : CollectionUtils.isEqualCollection(pokemons, team.pokemons);
    }

    @Override
    public int hashCode() {
        return pokemons != null ? pokemons.stream().sorted().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Team{" +
                "playerName='" + playerName + '\'' +
                ", tier='" + tier + '\'' +
                ", pokemons=" + pokemons +
                ", tagSet=" + tagSet +
                '}';
    }
}
