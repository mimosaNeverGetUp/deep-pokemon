/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.migrate;

import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.service.BattleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Component
@Profile("migrateBattleTeam")
public class BattleTeamMigrator {
    private static final Logger log = LoggerFactory.getLogger(BattleTeamMigrator.class);
    private final BattleService battleService;
    private final MongoTemplate mongoTemplate;
    private static final int BATCH_SIZE = 100;

    public BattleTeamMigrator(BattleService battleService, MongoTemplate mongoTemplate) {
        this.battleService = battleService;
        this.mongoTemplate = mongoTemplate;
    }

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
        Query query = new Query().cursorBatchSize(BATCH_SIZE);
        Stream<BattleTeam> teamStream = mongoTemplate.stream(query, BattleTeam.class);

        List<BattleTeam> teams = new ArrayList<>();
        teamStream.forEach(team -> {
            teams.add(team);
            if (teams.size() >= BATCH_SIZE) {
                try {
                    migrate(teams);
                } catch (Exception e) {
                    log.error("migrate battle team error", e);
                }
            }
        });
        if (!teams.isEmpty()) {
            migrate(teams);
        }
    }

    private void migrate(List<BattleTeam> battleList) {
        log.info("start to migrate team {}", battleList.stream().map(BattleTeam::id).toList());
        setBattleTeamId(battleList);
        battleList.clear();
    }

    private void setBattleTeamId(List<BattleTeam> teams) {
        for (BattleTeam team : teams) {
            if (team.pokemons() == null || team.pokemons().isEmpty()) {
                log.warn("battle team is empty : {}", team.id());
                continue;
            }
            byte[] teamId = battleService.calTeamId(team.pokemons());
            try {
                updateTeamId(team, teamId);
            } catch (Exception e) {
                log.error("set team {} teamId error", team.id(), e);
            }
        }
    }

    private void updateTeamId(BattleTeam team, byte[] teamId) {
        Query query = new Query(Criteria.where("_id").is(team.id()));
        Update update = new Update().set("teamId", teamId);
        mongoTemplate.updateFirst(query, update, BattleTeam.class);
    }
}