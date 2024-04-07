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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class Pokemon implements Serializable {
    private String name;
    private HashSet<String> moves = new HashSet<>();

    private String item;
    private String ablity;

    public Pokemon(String name) {
        this.name = name;
    }

    public HashSet<String> getMoves() {
        return moves;
    }

    public void setMoves(HashSet<String> moves) {
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
        if (item == null) {
            // 只可以修改一次，代表原始配置
            this.item = item;
        }
    }

    public String getAblity() {
        return ablity;
    }

    public void setAblity(String ablity) {
        this.ablity = ablity;
    }

    @Override
    public String toString() {
        String m = "\n";
        if (moves != null) {
            for (String move : moves) {
                m += "-" + move + "\n";
            }
            for (int i = 0; i < 4 - moves.size(); i++) {
                m += "-???\n";
            }
        }
        String it = "";
        if (item != null) {
            it = "@"+item;
        } else {
            it = "@???";
        }
        return name + it + m;
        //return String.format("      %s @%s \n       -%s \n \n", name, item, moves);
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
}
