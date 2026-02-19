/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon.entity.privcy;

import com.mimosa.deeppokemon.entity.BattleTeam;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("private_team")
public class PrivateTeam extends BattleTeam {
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
}