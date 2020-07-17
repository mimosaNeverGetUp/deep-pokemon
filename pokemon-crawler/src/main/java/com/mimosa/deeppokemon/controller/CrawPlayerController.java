package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.crawler.LadderBattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Player;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class CrawPlayerController {

    @Autowired
    LadderBattleCrawler ladderBattleCrawler;

    @Autowired
    BattleService battleService;

    @Autowired
    PlayerService playerService;

    @RequestMapping("craw")
    public String crawPlyaer(String name) {
        List<Battle> list = ladderBattleCrawler.crawPlayerBattle(name);
        Player player = new Player();
        player.setName(name);
        playerService.save(player);
        battleService.savaAll(list);
        return "success";
    }

}
