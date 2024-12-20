/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity.stat;

import java.math.BigDecimal;
import java.util.Objects;

public class BattleDamageStat {
    private String damageTarget;
    private String damageOf;
    private String damageFrom;
    private BigDecimal damage;
    private int triggerCount;

    public String getDamageFrom() {
        return damageFrom;
    }

    public void setDamageFrom(String damageFrom) {
        this.damageFrom = damageFrom;
    }

    public String getDamageTarget() {
        return damageTarget;
    }

    public void setDamageTarget(String damageTarget) {
        this.damageTarget = damageTarget;
    }

    public String getDamageOf() {
        return damageOf;
    }

    public void setDamageOf(String damageOf) {
        this.damageOf = damageOf;
    }

    public BigDecimal getDamage() {
        return damage;
    }

    public void setDamage(BigDecimal damage) {
        this.damage = damage;
    }

    public int getTriggerCount() {
        return triggerCount;
    }

    public void setTriggerCount(int triggerCount) {
        this.triggerCount = triggerCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattleDamageStat that = (BattleDamageStat) o;
        return Objects.equals(damageFrom, that.damageFrom) && Objects.equals(damageTarget, that.damageTarget) && Objects.equals(damageOf, that.damageOf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(damageFrom, damageTarget, damageOf);
    }
}