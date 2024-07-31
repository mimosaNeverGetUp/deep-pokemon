package com.mimosa.deeppokemon.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Document("battle_team")
public record BattleTeam(@MongoId String id, String battleId, byte[] teamId, LocalDate battleDate, List<String> battleType,
                         String playerName, List<Pokemon> pokemons, Set<Tag> tagSet) implements Serializable {
}