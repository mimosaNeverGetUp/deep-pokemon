/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class TourTeamGroupDetail extends TeamGroupDetail {
    protected static final String TOUR_TEAM = "tour_team";
    private static final String ID = "_id";
    protected static final String TOUR_ID = "tourId";
    protected static final String TOUR_PLAYER_RECORD = "tour_player_record";
    protected static final String PLAYER_ID = "player.tourPlayerId";
    protected static final String MAX_PLAYER_WIN_RATE = "maxPlayerWinRate";
    protected static final String MAX_PLAYER_WIN_DIF = "maxPlayerWinDif";
    protected static final String PLAYER_RECORD_WIN_RATE = "playerRecord.winRate";
    protected static final String PLAYER_RECORD_WIN_DIF = "playerRecord.winDif";
    protected static final String PLAYER_RECORDS = "playerRecords";
    protected static final String PLAYER_RECORD = "playerRecord";

    private final String tourId;
    private final String format;

    public TourTeamGroupDetail(String teamGroupCollectionName, String teamSetCollectionName, String tourId, String format) {
        super(null, null, teamGroupCollectionName, teamSetCollectionName);
        this.tourId = tourId;
        this.format = format;
    }

    public String tourId() {
        return tourId;
    }

    public String format() {
        return format;
    }

    @Override
    public String getTeamCollectionName() {
        return TOUR_TEAM;
    }

    @Override
    public List<AggregationOperation> buildTeamGroupAggregations() {
        MatchOperation matchOperation = Aggregation.match(Criteria.where(TOUR_ID).is(tourId())
                .and(TIER).is(format));

        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from(TOUR_PLAYER_RECORD)
                .localField(PLAYER_ID)
                .foreignField(ID)
                .as(PLAYER_RECORDS);
        AddFieldsOperation addFieldsOperation =
                Aggregation.addFields().addFieldWithValue(PLAYER_RECORD, ArrayOperators.arrayOf(PLAYER_RECORDS).first()).build();
        AggregationOperation projectOperation = Aggregation.stage("{ $project : { 'playerRecords': 0} }");
        GroupOperation groupOperation = Aggregation.group(TEAM_ID)
                .max(BATTLE_DATE).as(LATEST_BATTLE_DATE)
                .max(PLAYER_RECORD_WIN_RATE).as(MAX_PLAYER_WIN_RATE)
                .max(PLAYER_RECORD_WIN_DIF).as(MAX_PLAYER_WIN_DIF)
                .first(POKEMONS).as(POKEMONS)
                .first(TAG_SET).as(TAG_SET)
                .first(FEATURE_IDS).as(FEATURE_IDS)
                .first(TIER).as(TIER)
                .addToSet(PLAYER_NAME).as(PLAYER_SET)
                .push("$$ROOT").as(TEAMS);
        return List.of(matchOperation, lookupOperation, addFieldsOperation, projectOperation, groupOperation);
    }

    @Override
    public MergeOperation.WhenDocumentsMatch getMergeMatchUpdateOperation() {
        return MergeOperation.WhenDocumentsMatch.updateWith(Aggregation.newAggregation(
                SetOperation.set(LATEST_BATTLE_DATE).toValue("$$new.latestBattleDate")
                        .and().set(MAX_PLAYER_WIN_RATE).toValue("$$new.maxPlayerWinRate")
                        .and().set(MAX_PLAYER_WIN_DIF).toValue("$$new.maxPlayerWinDif")
                        .and().set(POKEMONS).toValue("$$new.pokemons")
                        .and().set(TEAMS).toValue("$$new.teams")
                        .and().set(UNIQUE_PLAYER_NUM).toValue("$$new.uniquePlayerNum")
                        .and().set(REPLAY_NUM).toValue("$$new.replayNum")
        ));
    }

    @Override
    public List<String> getIndexFiled() {
        return List.of(MAX_PLAYER_WIN_DIF, MAX_PLAYER_WIN_RATE);
    }
}