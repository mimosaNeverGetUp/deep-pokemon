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
