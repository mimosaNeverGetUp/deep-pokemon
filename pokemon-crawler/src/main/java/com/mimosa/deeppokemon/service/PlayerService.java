package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.crawler.LadderBattleCrawler;
import com.mimosa.deeppokemon.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    @Autowired
    private MongoTemplate mongoTemplate;
    private static Logger log = LoggerFactory.getLogger(PlayerService.class);

    public void save(Player player) {
        log.info("save player:"+player.getName());
        try {
            mongoTemplate.save(player);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error(e.getMessage());
        }
    }

    public void saveAll(List<Player> players) {
        log.info("save players:"+players.get(0).getName());
        try {
            mongoTemplate.insertAll(players);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error(e.getMessage());
        }

    }
}
