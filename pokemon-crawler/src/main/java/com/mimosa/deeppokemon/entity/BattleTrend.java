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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: deep-pokemon
 * @description: 比赛局势变化曲线数据类
 * @author: mimosa
 * @create: 2021//06//11
 */
public class BattleTrend {

    /**
     * 记录trend数组每一列索引对应的宝可梦
     * map:记录每个队伍宝可梦对应的数组索引
     *     key 宝可梦名
     *     valeue 对应的trend数组索引
     * list:储存所有队伍的数组索引map
     */
    List<Map<String,Integer>> pokemonOrder;

    private Short[][] healthLineTrend;
    private Short[][] valueLineTrend;
    private Short[][] attackValueTrend;
    private Short[][] statusLineTrend;
    private Boolean[][] stealthRockTrend;
    private Boolean[][] spikeTrend;
    private Boolean[][] toxicSpikeTrend;


    public BattleTrend(int count, Team[] teams) {
        healthLineTrend = new Short[12][count];
        valueLineTrend = new Short[12][count];
        attackValueTrend = new Short[12][count];
        statusLineTrend = new Short[2][count];
        spikeTrend = new Boolean[2][count];
        toxicSpikeTrend = new Boolean[2][count];
        stealthRockTrend = new Boolean[2][count];
        pokemonOrder = new ArrayList<>(2);

        int i = 0;
        for (Team team : teams) {
            // 记录宝可梦对应的数组索引,初始化每个宝可梦的初始血量
            Map<String, Integer> pokemonNameMap = new HashMap<>(6);
            for (Pokemon pokemon : team.getPokemons()) {
                pokemonNameMap.put(pokemon.getName(), i);
                healthLineTrend[i][0] = 100;
                ++i;
            }
            pokemonOrder.add(pokemonNameMap);
        }
    }

    public List<Map<String,Integer>> getPokemonOrder() {
        return pokemonOrder;
    }

    /**
     * 获取宝可梦对应的trend数组索引
     *
     * @param playerIndex 玩家索引
     * @param pokemonName 宝可梦名
     * @return Integer 对应的trend数组索引
     * @author huangxiaocong(779032284@qq.com)
     */
    public Integer getPokemonIndex(int playerIndex,String pokemonName) {
        return pokemonOrder.get(playerIndex).get(pokemonName);
    }

    public void setPokemonOrder(List<Map<String,Integer>> pokemonOrder) {
        this.pokemonOrder = pokemonOrder;
    }

    public Short[][] getHealthLineTrend() {
        return healthLineTrend;
    }

    public void setHealthLineTrend(Short[][] healthLineTrend) {
        this.healthLineTrend = healthLineTrend;
    }

    public Short[][] getValueLineTrend() {
        return valueLineTrend;
    }

    public void setValueLineTrend(Short[][] valueLineTrend) {
        this.valueLineTrend = valueLineTrend;
    }

    public Short[][] getAttackValueTrend() {
        return attackValueTrend;
    }

    public void setAttackValueTrend(Short[][] attackValueTrend) {
        this.attackValueTrend = attackValueTrend;
    }

    public Short[][] getStatusLineTrend() {
        return statusLineTrend;
    }

    public void setStatusLineTrend(Short[][] statusLineTrend) {
        this.statusLineTrend = statusLineTrend;
    }

    public Boolean[][]  getStealthRockTrend() {
        return stealthRockTrend;
    }

    public void setStealthRockTrend(Boolean[][]  stealthRockTrend) {
        this.stealthRockTrend = stealthRockTrend;
    }

    public Boolean[][]  getSpikeTrend() {
        return spikeTrend;
    }

    public void setSpikeTrend(Boolean[][]  spikeTrend) {
        this.spikeTrend = spikeTrend;
    }

    public Boolean[][]  getToxicSpikeTrend() {
        return toxicSpikeTrend;
    }

    public void setToxicSpikeTrend(Boolean[][]  toxicSpikeTrend) {
        this.toxicSpikeTrend = toxicSpikeTrend;
}
}
