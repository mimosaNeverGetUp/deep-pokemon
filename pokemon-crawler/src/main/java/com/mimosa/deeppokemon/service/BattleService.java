package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.entity.Battle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BattleService {
    @Autowired
    private MongoTemplate mongoTemplate;
    private static Logger log = LoggerFactory.getLogger(BattleService.class);

    public void save(Battle battle) {
        log.info("save a battle:"+battle.getBattleID());
        try {
            mongoTemplate.save(battle);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error(e.getMessage());
        }


    }

    public void savaAll(List<Battle> battles) {
        log.info("save battles:"+battles.get(0).getBattleID());
        try {
            for (Battle battle : battles) {
                mongoTemplate.save(battle);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            log.error(e.getMessage());
        }
    }

    public LocalDate findPlayerBattleEarliest(String playerName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("teams.playerName").is(playerName));
        query.with(Sort.by(Sort.Order.asc("date")));
        query.limit(1);
        Battle battle = mongoTemplate.findOne(query, Battle.class, "battle");
        if (battle == null) {
            return null;
        }
        return battle.getDate();
    }

    public LocalDate findPlayerBattleLatest(String playerName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("teams.playerName").is(playerName));
        query.with(Sort.by(Sort.Order.desc("date")));
        query.limit(1);
        Battle battle = mongoTemplate.findOne(query, Battle.class, "battle");
        if (battle == null) {
            return null;
        }
        return battle.getDate();
    }
}
