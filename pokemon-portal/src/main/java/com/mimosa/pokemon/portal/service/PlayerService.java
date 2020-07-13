package com.mimosa.pokemon.portal.service;

import com.mimosa.deeppokemon.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class PlayerService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public Player findPlayerByName(String playerName) {
        Query query = new BasicQuery(String.format("{ name : \"%s\" }", playerName))
                .with(Sort.by(Sort.Order.desc("infoDate")))
                .limit(1);
        Player player = mongoTemplate.findOne(query, Player.class, "player");
        return player;
    }

    public List<Player> ListPlayerByName(String playerName) {
        Query query = new BasicQuery(String.format("{ name : %s }", playerName));
        List<Player> playerList = mongoTemplate.find(query, Player.class, "player");
        return playerList;
    }

    public List<Player> listPlayerRank()  {
        //查询数据库里储存的最新的日期
        Query query = new BasicQuery("{}")
                .with(Sort.by(Sort.Order.desc("infoDate")))
                .limit(1);
        Player player = mongoTemplate.findOne(query, Player.class, "player");
        LocalDate date = player.getInfoDate();
        //查询最新日期的列表玩家
        query = new BasicQuery("{}")
                .with(Sort.by(Sort.Order.asc("rank")));
        Criteria criteria = Criteria.where("infoDate").gte(date);
        query.addCriteria(criteria);
        List<Player> playerList = mongoTemplate.find(query, Player.class, "player");
        //去除列表里重复
        for (int i = 0; i < playerList.size(); ++i) {
           for (int j = playerList.size() - 1; j > i; --j) {
               if (playerList.get(i).equals(playerList.get(j))) {
                  playerList.remove(j);
              }
            }
      }
        return playerList;
    }


}
