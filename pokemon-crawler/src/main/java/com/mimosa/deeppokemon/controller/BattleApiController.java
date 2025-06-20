/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.crawler.LadderCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.service.BattleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class BattleApiController {
    private final BattleService battleService;

    private final LadderCrawler ladderCrawler;
    private final LadderCrawler gen9NdladderCrawler;

    public BattleApiController(BattleService battleService, LadderCrawler ladderCrawler,
                               @Qualifier("gen9NdLadderCrawler") LadderCrawler gen9NdladderCrawler) {
        this.battleService = battleService;
        this.ladderCrawler = ladderCrawler;
        this.gen9NdladderCrawler = gen9NdladderCrawler;
    }

    @GetMapping("/battle/{battleid}")
    public Battle battle(@PathVariable("battleid") String battleId) {
        return battleService.getBattle(battleId);
    }

    @GetMapping("/battle/{battleid}/stat")
    public BattleStat battleStat(@PathVariable("battleid") String battleId) {
        return battleService.getBattleStat(battleId);
    }

    @PostMapping("/ladder/craw")
    public String crawLadder(@RequestParam String format) {
        if (StringUtils.equals("gen9nationaldex", format)) {
            gen9NdladderCrawler.crawLadder(true);
        } else {
            ladderCrawler.crawLadder(true);
        }
        return "success trigger";
    }

    @PostMapping("/team/update")
    public String updateTeam(@RequestParam String format) {
        battleService.updateTeam(format);
        return "success";
    }

    @PostMapping("/team/month/update")
    public String updateMonthTeam(@RequestParam(name = "year") int year, @RequestParam(name = "month") int month) {
        battleService.updateMonthTeam(LocalDate.of(year, month, 1));
        return "success";
    }
}