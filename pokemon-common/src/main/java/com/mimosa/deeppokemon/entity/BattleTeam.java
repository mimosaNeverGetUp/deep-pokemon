package com.mimosa.deeppokemon.entity;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document("battle_team")
public class BattleTeam implements Serializable {
    @MongoId
    protected String id;
    protected String battleId;
    protected byte[] teamId;
    protected LocalDate battleDate;
    protected List<String> battleType;
    protected float rating;
    protected String playerName;
    protected String tier;
    protected List<Pokemon> pokemons;
    protected Set<Tag> tagSet;
    protected List<Binary> featureIds;

    public BattleTeam() {
        battleType = new ArrayList<>();
        pokemons = new ArrayList<>();
        tagSet = new HashSet<>();
        featureIds = new ArrayList<>();
    }

    public BattleTeam(String id, String battleId, byte[] teamId, LocalDate battleDate, List<String> battleType,
                      float rating, String playerName, String tier, List<Pokemon> pokemons, Set<Tag> tagSet) {
        this.id = id;
        this.battleId = battleId;
        this.teamId = teamId;
        this.battleDate = battleDate;
        this.battleType = battleType;
        this.rating = rating;
        this.playerName = playerName;
        this.tier = tier;
        this.pokemons = pokemons;
        this.tagSet = tagSet;
        featureIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBattleId() {
        return battleId;
    }

    public void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    public byte[] getTeamId() {
        return teamId;
    }

    public void setTeamId(byte[] teamId) {
        this.teamId = teamId;
    }

    public LocalDate getBattleDate() {
        return battleDate;
    }

    public void setBattleDate(LocalDate battleDate) {
        this.battleDate = battleDate;
    }

    public List<String> getBattleType() {
        return battleType;
    }

    public void setBattleType(List<String> battleType) {
        this.battleType = battleType;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    public Set<Tag> getTagSet() {
        return tagSet;
    }

    public void setTagSet(Set<Tag> tagSet) {
        this.tagSet = tagSet;
    }

    public List<Binary> getFeatureIds() {
        return featureIds;
    }

    public void setFeatureIds(List<Binary> featureIds) {
        this.featureIds = featureIds;
    }

    public Pokemon findPokemon(String pokemonName) {
        for (Pokemon p : pokemons) {
            if (p.getName().equals(pokemonName)) {
                return p;
            }
        }
        return null;
    }
}