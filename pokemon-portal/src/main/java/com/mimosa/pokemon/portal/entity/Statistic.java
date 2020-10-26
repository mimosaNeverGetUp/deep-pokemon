package com.mimosa.pokemon.portal.entity;


import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

/**
 * @program: deep-pokemon
 * @description: pokemon static
 * @author: mimosa
 * @create: 2020//10//17
 */

@Document
public class Statistic {
    private float use;
    private float win;
    private float total;
    private float winDiff;
    private float useDiff;
    private HashMap<String, Float> moves;


    public Statistic(float u, float w, float t) {
        this.use = u;
        this.win = w;
        this.total = t;
    }

    public Statistic() {
    }

    public float getUse() {
        return use;
    }

    public void setUse(float u) {
        this.use = u;
    }

    public Statistic(float u) {
        this.use = u;
    }

    public float getWin() {
        return win;
    }


    public HashMap<String, Float> getMoves() {
        return moves;
    }

    public void setMoves(HashMap<String, Float> moves) {
        this.moves = moves;
    }

    public void setWin(float win) {
        this.win = win;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float t) {
        this.total = t;
    }

    public float getWinDiff() {
        return winDiff;
    }

    public void setWinDiff(float winDiff) {
        this.winDiff = winDiff;
    }

    public float getUseDiff() {
        return useDiff;
    }

    public void setUseDiff(float useDiff) {
        this.useDiff = useDiff;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "use=" + use +
                ", win=" + win +
                ", total=" + total +
                ", moves=" + moves +
                '}';
    }
}
