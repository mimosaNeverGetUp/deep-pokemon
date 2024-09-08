/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.migrate;

import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.service.BattleService;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Component
public class BattleTeamMigrator {
    private static final Logger log = LoggerFactory.getLogger(BattleTeamMigrator.class);
    protected static final String BATTLE_TEAM = "battle_team";
    protected static final String TOUR_TEAM = "tour_team";
    protected static final String FEATURE_IDS = "featureIds";
    private final BattleService battleService;
    private final MongoTemplate mongoTemplate;
    private static final int BATCH_SIZE = 500;

    public BattleTeamMigrator(BattleService battleService, MongoTemplate mongoTemplate) {
        this.battleService = battleService;
        this.mongoTemplate = mongoTemplate;
    }

    public void migrateBattleTeam() {
        log.info("start migrate battle team");
        try {
            migrate(BATTLE_TEAM);
            migrate(TOUR_TEAM);
        } catch (Exception e) {
            log.error("migrate battle team failed", e);
        }
        log.info("end migrate battle team");
    }

    public void migrate(String collectionName) {
        Query query = new Query().cursorBatchSize(BATCH_SIZE);
        Stream<BattleTeam> teamStream = mongoTemplate.stream(query, BattleTeam.class, collectionName);

        List<BattleTeam> teams = new ArrayList<>();

        teamStream.forEach(team -> {
            teams.add(team);
            if (teams.size() >= BATCH_SIZE) {
                try {
                    migrate(teams, collectionName);
                } catch (Exception e) {
                    log.error("migrate battle team error", e);
                }
            }
        });
        if (!teams.isEmpty()) {
            migrate(teams, collectionName);
        }
    }

    private void migrate(List<BattleTeam> battleTeams, String collectionName) {
        log.info("start to migrate team {}", battleTeams.stream().map(BattleTeam::getBattleId).toList());
        setTeamFeatureId(battleTeams, collectionName);
        battleTeams.clear();
    }

    private void setTeamFeatureId(List<BattleTeam> teams, String collectionName) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, collectionName);
        for (BattleTeam team : teams) {
            if (team.getPokemons() == null || team.getPokemons().isEmpty()) {
                log.warn("battle team is empty : {}", team.getBattleId());
                continue;
            }
            List<Binary> featureIds = battleService.calTeamFeatureId(team.getPokemons());
            addUpdateTeamFeatureIdOperations(bulkOperations, team, featureIds);
        }

        try {
            bulkOperations.execute();
        } catch (Exception e) {
            log.error("set team feature id error", e);
        }
    }

    private void addUpdateTeamFeatureIdOperations(BulkOperations bulkOperations, BattleTeam team,
                                                  List<Binary> featureIds) {
        Query query = new Query(Criteria.where("_id").is(team.getId()));
        Update update = new Update().set(FEATURE_IDS, featureIds);
        bulkOperations.updateOne(query, update);
    }
}