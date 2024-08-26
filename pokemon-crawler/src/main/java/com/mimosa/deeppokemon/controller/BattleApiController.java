/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.crawler.LadderCrawler;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.service.BattleService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class BattleApiController {
    private final BattleService battleService;

    private final LadderCrawler ladderCrawler;

    public BattleApiController(BattleService battleService, LadderCrawler ladderCrawler) {
        this.battleService = battleService;
        this.ladderCrawler = ladderCrawler;
    }

    @GetMapping("/battle/{battleid}/stat")
    public BattleStat battleStat(@PathVariable("battleid") String battleId) {
        return battleService.getBattleStat(battleId);
    }

    @PostMapping("/ladder/craw")
    public String crawLadder() {
        ladderCrawler.crawLadder(true);
        return "success trigger";
    }

    @PostMapping("/team/update")
    public String updateTeam() {
        battleService.updateTeam();
        return "success";
    }

    @PostMapping("/team/month/update")
    public String updateMonthTeam(@RequestParam(name = "year") int year, @RequestParam(name = "month") int month) {
        battleService.updateMonthTeam(LocalDate.of(year, month, 1));
        return "success";
    }
}