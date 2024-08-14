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

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Pokemon implements Serializable, Comparable<Pokemon> {
    private String name;
    private Set<String> moves = new HashSet<>();

    private String item;
    private String ability;
    private String teraType;

    public Pokemon() {

    }

    public Pokemon(String name) {
        this.name = name;
    }

    public Set<String> getMoves() {
        return moves;
    }

    public void setMoves(Set<String> moves) {
        this.moves = moves;
    }

    public String getItem() {
        return item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getTeraType() {
        return teraType;
    }

    public void setTeraType(String teraType) {
        this.teraType = teraType;
    }

    @Override
    public String toString() {
        StringBuilder m = new StringBuilder("\n");
        if (moves != null) {
            for (String move : moves) {
                m.append("-").append(move).append("\n");
            }
            for (int i = 0; i < 4 - moves.size(); i++) {
                m.append("-???\n");
            }
        }
        String it;
        if (item != null) {
            it = "@" + item;
        } else {
            it = "@???";
        }
        return name + it + m;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pokemon pokemon = (Pokemon) o;

        return name != null ? name.equals(pokemon.name) : pokemon.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public int compareTo(@NotNull Pokemon o) {
        if (name == null) {
            return o.name == null ? 0 : 1;
        }
        return name.compareTo(o.name);
    }
}