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
import com.mimosa.deeppokemon.service.PrivateBattleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class BattleApiController {
    private final BattleService battleService;
    private final PrivateBattleService privateBattleService;

    private final LadderCrawler ladderCrawler;
    private final LadderCrawler gen9NdladderCrawler;
    private final LadderCrawler gen9UUladderCrawler;

    public BattleApiController(BattleService battleService, LadderCrawler ladderCrawler,
                               PrivateBattleService privateBattleService,
                               @Qualifier("gen9NdLadderCrawler") LadderCrawler gen9NdladderCrawler,
                               @Qualifier("gen9UULadderCrawler") LadderCrawler gen9UUladderCrawler) {
        this.battleService = battleService;
        this.ladderCrawler = ladderCrawler;
        this.privateBattleService = privateBattleService;
        this.gen9NdladderCrawler = gen9NdladderCrawler;
        this.gen9UUladderCrawler = gen9UUladderCrawler;
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
        } else if (StringUtils.equals("gen9uu", format)) {
            gen9UUladderCrawler.crawLadder(true);
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

    @PostMapping("/thread/craw")
    public Map<String, String> crawPrivateBattle(@RequestParam String thread, @RequestParam String sourceName,
                                                 @RequestParam String describe, @RequestParam String format) {
        CompletableFuture<List<Battle>> future = privateBattleService.crawPrivateBattle(thread, sourceName, describe, format);
        List<Battle> battles = future.join();

        Map<String, String> map = new HashMap<>();
        map.put("insertSize", Integer.toString(battles.size()));
        map.put("result", "success");
        return map;
    }
}