package com.mimosa.deeppokemon.entity;

import java.util.List;

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
    // 有效伤害
    protected Float effectiveDamage;
    // 出招回合数
    protected Integer moveCount;
    // 有效操作数
    protected Integer effectiveMCount;
    // 有效伤害变化表
    protected List<Integer> effectiveDamageTrend;
    // 贡献值变化表
    protected List<Integer> healLineValueTrend;

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

    public Integer getEffectiveMCount() {
        return effectiveMCount;
    }

    public void setEffectiveMCount(Integer effectiveMCount) {
        this.effectiveMCount = effectiveMCount;
    }

    public List<Integer> getEffectiveDamageTrend() {
        return effectiveDamageTrend;
    }

    public void setEffectiveDamageTrend(List<Integer> effectiveDamageTrend) {
        this.effectiveDamageTrend = effectiveDamageTrend;
    }

    public List<Integer> getHealLineValueTrend() {
        return healLineValueTrend;
    }

    public void setHealLineValueTrend(List<Integer> healLineValueTrend) {
        this.healLineValueTrend = healLineValueTrend;
    }
}
