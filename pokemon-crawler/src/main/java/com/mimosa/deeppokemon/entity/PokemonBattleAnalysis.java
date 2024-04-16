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

/**
 * @program: deep-pokemon
 * @description: 宝可梦在一局比赛里的表现及度量分析
 * @author: mimosa
 * @create: 2020//12//07
 */
public class PokemonBattleAnalysis {
    protected String pokemonName;

    // 击杀数
    protected Integer kill;
    // 换上次数
    protected Integer switchCount;
    // 宝可梦在该局比赛的血线变化贡净献值，从该精灵的上场回合后开始计算，直到换下回合为止
    protected Float healLineValue;
    // 有效伤害,不计算我方血线
    protected Float effectiveDamage;
    // 出招回合数
    protected Integer moveCount;
    // 有效操作数
    protected Integer effectiveMoveCount;
    // 对局评分
    protected Float score;

    public PokemonBattleAnalysis(String pokemonName) {
        this.pokemonName = pokemonName;
        kill = 0;
        switchCount = 0;
        healLineValue = 0.0F;
        effectiveDamage = 0.0F;
        moveCount = 0;
        effectiveMoveCount = 0;
        score = 0.0F;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public void setPokemonName(String pokemonName) {
        this.pokemonName = pokemonName;
    }

    public Integer getKill() {
        return kill;
    }

    public void setKill(Integer kill) {
        this.kill = kill;
    }

    public Integer getSwitchCount() {
        return switchCount;
    }

    public void setSwitchCount(Integer switchCount) {
        this.switchCount = switchCount;
    }

    public Float getHealLineValue() {
        return healLineValue;
    }

    public void setHealLineValue(Float healLineValue) {
        this.healLineValue = healLineValue;
    }

    public Float getEffectiveDamage() {
        return effectiveDamage;
    }

    public void setEffectiveDamage(Float effectiveDamage) {
        this.effectiveDamage = effectiveDamage;
    }

    public Integer getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(Integer moveCount) {
        this.moveCount = moveCount;
    }

    public Integer getEffectiveMoveCount() {
        return effectiveMoveCount;
    }

    public void setEffectiveMoveCount(Integer effectiveMoveCount) {
        this.effectiveMoveCount = effectiveMoveCount;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

}