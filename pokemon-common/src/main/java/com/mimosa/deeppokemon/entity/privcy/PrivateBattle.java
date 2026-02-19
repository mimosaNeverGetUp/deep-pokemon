/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon.entity.privcy;

import com.mimosa.deeppokemon.entity.Battle;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Objects;

@Document(collection = "private_battle")
public class PrivateBattle extends Battle implements Serializable {
    protected String sourceName;
    protected String describe;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrivateBattle battle = (PrivateBattle) o;

        return Objects.equals(battleID, battle.battleID);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}