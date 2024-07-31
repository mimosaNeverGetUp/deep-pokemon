package com.mimosa.deeppokemon.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Document("battle_team")
public record BattleTeam(@MongoId String id, String battleId, byte[] teamId, LocalDate battleDate, List<String> battleType,
                         String playerName, List<Pokemon> pokemons, Set<Tag> tagSet) implements Serializable {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattleTeam that = (BattleTeam) o;
        return Objects.equals(id, that.id) && Objects.deepEquals(teamId, that.teamId)
                && Objects.equals(battleId, that.battleId) && Objects.equals(tagSet, that.tagSet)
                && Objects.equals(playerName, that.playerName) && Objects.equals(battleDate, that.battleDate)
                && Objects.equals(pokemons, that.pokemons) && Objects.equals(battleType, that.battleType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, battleId, Arrays.hashCode(teamId), battleDate, battleType, playerName, pokemons, tagSet);
    }

    @Override
    public String toString() {
        return "BattleTeam{" +
                "id='" + id + '\'' +
                ", battleId='" + battleId + '\'' +
                ", teamId=" + Arrays.toString(teamId) +
                ", battleDate=" + battleDate +
                ", battleType=" + battleType +
                ", playerName='" + playerName + '\'' +
                ", pokemons=" + pokemons +
                ", tagSet=" + tagSet +
                '}';
    }
}