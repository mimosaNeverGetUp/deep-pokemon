package com.mimosa.deeppokemon.entity;

import java.util.ArrayList;
import java.util.HashSet;

public class Pokemon {
    private String name;
    private HashSet<String> moves;

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

    public void setItem(String item) {
        this.item = item;
    }

    public String getAblity() {
        return ablity;
    }

    public void setAblity(String ablity) {
        this.ablity = ablity;
    }

    @Override
    public String toString() {
        return String.format("      %s @%s \n       -%s \n \n", name, item, moves);
    }
}
