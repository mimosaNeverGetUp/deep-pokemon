package com.mimosa.deeppokemon.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: deep-pokemon
 * @description: 比赛局势变化曲线数据类
 * @author: mimosa
 * @create: 2021//06//11
 */
public class BattleTrend {
    // 数组依次对应的宝可梦
    Map<String,Integer> pokemonOrder;
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
        pokemonOrder = new HashMap<>(12);

        int i = 0;
        int teamIndex = 1;
        for (Team team : teams) {
            for (Pokemon pokemon : team.getPokemons()) {
                String key = teamIndex + "_" + pokemon.getName();
                pokemonOrder.put(key, i);
                healthLineTrend[i][0] = 600;
                ++i;
            }
        }
    }

    public Map<String, Integer> getPokemonOrder() {
        return pokemonOrder;
    }

    public void setPokemonOrder(Map<String,Integer> pokemonOrder) {
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
