/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class TeamGroupDetail {
    protected static final String BATTLE_TEAM = "battle_team";
    protected static final String TEAM_ID = "teamId";
    protected static final String LATEST_BATTLE_DATE = "latestBattleDate";
    protected static final String BATTLE_DATE = "battleDate";
    protected static final String RATING = "rating";
    protected static final String MAX_RATING = "maxRating";
    protected static final String POKEMONS = "pokemons";
    protected static final String TAG_SET = "tagSet";
    protected static final String TIER = "tier";
    protected static final String PLAYER_NAME = "playerName";
    protected static final String PLAYER_SET = "playerSet";
    protected static final String TEAMS = "teams";
    protected static final String UNIQUE_PLAYER_NUM = "uniquePlayerNum";
    protected static final String REPLAY_NUM = "replayNum";
    protected static final String FEATURE_IDS = "featureIds";

    private final LocalDateTime start;
    private final LocalDateTime end;
    private final String teamGroupCollectionName;
    private final String teamSetCollectionName;

    public TeamGroupDetail(LocalDateTime start, LocalDateTime end, String teamGroupCollectionName,
                           String teamSetCollectionName) {
        this.start = start;
        this.end = end;
        this.teamGroupCollectionName = teamGroupCollectionName;
        this.teamSetCollectionName = teamSetCollectionName;
    }

    public LocalDateTime start() {
        return start;
    }

    public LocalDateTime end() {
        return end;
    }

    public String teamGroupCollectionName() {
        return teamGroupCollectionName;
    }

    public String teamSetCollectionName() {
        return teamSetCollectionName;
    }

    public String getTeamCollectionName() {
        return BATTLE_TEAM;
    }

    public List<AggregationOperation> buildTeamGroupAggregations() {
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where(BATTLE_DATE).gte(start).lte(end));
        GroupOperation groupOperation = Aggregation.group(TEAM_ID)
                .max(BATTLE_DATE).as(LATEST_BATTLE_DATE)
                .max(RATING).as(MAX_RATING)
                .first(POKEMONS).as(POKEMONS)
                .first(TAG_SET).as(TAG_SET)
                .first(FEATURE_IDS).as(FEATURE_IDS)
                .first(TIER).as(TIER)
                .addToSet(PLAYER_NAME).as(PLAYER_SET)
                .push("$$ROOT").as(TEAMS);
        return List.of(matchOperation, groupOperation);
    }

    public MergeOperation.WhenDocumentsMatch getMergeMatchUpdateOperation() {
        return MergeOperation.WhenDocumentsMatch.updateWith(Aggregation.newAggregation(
                SetOperation.set(LATEST_BATTLE_DATE).toValue("$$new.latestBattleDate")
                        .and().set(MAX_RATING).toValue("$$new.maxRating")
                        .and().set(FEATURE_IDS).toValue("$$new.featureIds")
                        .and().set(POKEMONS).toValue("$$new.pokemons")
                        .and().set(TEAMS).toValue("$$new.teams")
                        .and().set(UNIQUE_PLAYER_NUM).toValue("$$new.uniquePlayerNum")
                        .and().set(REPLAY_NUM).toValue("$$new.replayNum")
        ));
    }

    public List<String> getIndexFiled() {
        return Collections.singletonList(MAX_RATING);
    }
}