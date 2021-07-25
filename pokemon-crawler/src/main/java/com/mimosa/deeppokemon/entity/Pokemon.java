package com.mimosa.deeppokemon.entity;

import java.util.ArrayList;
import java.util.HashSet;

public class Pokemon {
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
