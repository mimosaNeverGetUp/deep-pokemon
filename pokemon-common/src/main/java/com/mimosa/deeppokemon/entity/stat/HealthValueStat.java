/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat;

import java.math.BigDecimal;

public class HealthValueStat {
    private BigDecimal healthValue;
    private BigDecimal lossHealthValue;
    private BigDecimal opponentLossHealthValue;
    private String opponentPokemon;

    public BigDecimal getHealthValue() {
        return healthValue;
    }

    public void setHealthValue(BigDecimal healthValue) {
        this.healthValue = healthValue;
    }

    public BigDecimal getLossHealthValue() {
        return lossHealthValue;
    }

    public void setLossHealthValue(BigDecimal lossHealthValue) {
        this.lossHealthValue = lossHealthValue;
    }

    public BigDecimal getOpponentLossHealthValue() {
        return opponentLossHealthValue;
    }

    public void setOpponentLossHealthValue(BigDecimal opponentLossHealthValue) {
        this.opponentLossHealthValue = opponentLossHealthValue;
    }

    public String getOpponentPokemon() {
        return opponentPokemon;
    }

    public void setOpponentPokemon(String opponentPokemon) {
        this.opponentPokemon = opponentPokemon;
    }
}