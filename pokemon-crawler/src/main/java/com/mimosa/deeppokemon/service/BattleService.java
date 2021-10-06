package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.crawler.LadderBattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Service
public class BattleService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    LadderBattleCrawler ladderBattleCrawler;

    @Autowired
    PlayerService playerService;

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
        if (battles.isEmpty()) {
            return;
        }
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

    public String findPlayerBattleIdEarliest(String playerName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("teams.playerName").is(playerName));
        query.with(Sort.by(Sort.Order.asc("date")));
        query.limit(1);
        Battle battle = mongoTemplate.findOne(query, Battle.class, "battle");
        if (battle == null) {
            return null;
        }
        return battle.getBattleID();
    }

    public String findPlayerBattleIdLatest(String playerName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("teams.playerName").is(playerName));
        query.with(Sort.by(Sort.Order.desc("date")));
        query.limit(1);
        Battle battle = mongoTemplate.findOne(query, Battle.class, "battle");
        if (battle == null) {
            return null;
        }
        return battle.getBattleID();
    }

    public List<Battle> find100BattleSortByDate() {
        Query query = new BasicQuery("{}").with(Sort.by(Sort.Order.desc("date"))).limit(100);
        List<Battle> battles = mongoTemplate.find(query, Battle.class, "battle");
        return battles;
    }

    @RequestMapping("crawLadder")
    public void crawLadder( ) throws Exception {
        log.info(String.format("craw start: format:%s pageLimit:%d rankLimit:%d eloLimit:%d gxeLimit:%f dateLimit:%tF",
                ladderBattleCrawler.getFormat(), ladderBattleCrawler.getPageLimit(), ladderBattleCrawler.getRankMoreThan(),
                ladderBattleCrawler.getMinElo(), ladderBattleCrawler.getMinGxe(), ladderBattleCrawler.getDateAfter()));
        List<Player> players = ladderBattleCrawler.crawLadeerName();
        playerService.saveAll(players);
        List<Battle> battles = ladderBattleCrawler.crawLadderBattle();
        savaAll(battles);
    }
}
