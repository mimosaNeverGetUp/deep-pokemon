/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.migrate;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.service.BattleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
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
    private final BattleService battleService;
    private final MongoTemplate mongoTemplate;
    private static final int BATCH_SIZE = 100;

    public BattleTeamMigrator(BattleService battleService, MongoTemplate mongoTemplate) {
        this.battleService = battleService;
        this.mongoTemplate = mongoTemplate;
    }

    @Profile("migrateBattleTeam")
    @EventListener(value = ApplicationReadyEvent.class)
    public void migrateBattleTeam() {
        log.info("start migrate battle team");
        try {
            migrate();
        } catch (Exception e) {
            log.error("migrate battle team failed", e);
        }
        log.info("end migrate battle team");
    }

    public void migrate() {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("battle_team")
                .localField("_id")
                .foreignField("battleId")
                .as("battleTeam");

        MatchOperation matchOperation = Aggregation.match(Criteria.where("battleTeam").size(0));
        ProjectionOperation projectionOperation = Aggregation.project("avageRating", "teams", "date", "_id");
        AggregationOptions aggregationOptions = Aggregation.newAggregationOptions()
                .cursorBatchSize(BATCH_SIZE)
                .build();
        Aggregation aggregation = Aggregation.newAggregation(
                lookupOperation,
                matchOperation,
                projectionOperation
        ).withOptions(aggregationOptions);
        Stream<Battle> battleStream = mongoTemplate.aggregateStream(aggregation, "battle", Battle.class);

        List<Battle> battleList = new ArrayList<>();
        battleStream.forEach(battle -> {
            battleList.add(battle);
            if (battleList.size() >= BATCH_SIZE) {
                try {
                    migrate(battleList);
                } catch (Exception e) {
                    log.error("migrate battle team error", e);
                }
            }
        });
        if (!battleList.isEmpty()) {
            migrate(battleList);
        }
    }

    private void migrate(List<Battle> battleList) {
        log.info("start to migrate battle {}", battleList.stream().map(Battle::getBattleID).toList());
        battleService.insertTeam(battleList);
        setBattlePlayers(battleList);
        battleList.clear();
    }

    private void setBattlePlayers(List<Battle> battleList) {
        for (Battle battle : battleList) {
            if (battle.getTeams().length < 2) {
                log.warn("battle team length less than 2 : {}", battle.getBattleID());
                continue;
            }
            try {
                updateBattlePlayers(battle);
            } catch (Exception e) {
                log.error("set battle {} player error", battle.getBattleID(), e);
            }
        }
    }

    private void updateBattlePlayers(Battle battle) {
        Query query = new Query(Criteria.where("_id").is(battle.getBattleID()));
        Update update = new Update().set("players", List.of(battle.getTeams()[0].getPlayerName(), battle.getTeams()[1].getPlayerName()));
        mongoTemplate.updateFirst(query, update, Battle.class);
    }
}