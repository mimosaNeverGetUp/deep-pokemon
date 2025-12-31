/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.crawler.pokepast.PokePastTeamCrawler;
import com.mimosa.deeppokemon.crawler.pokepast.SmogonPokePastTeamCrawler;
import com.mimosa.deeppokemon.entity.Tag;
import com.mimosa.deeppokemon.entity.pokepast.PokePastTeam;
import com.mimosa.deeppokemon.migrate.BattleTeamMigrator;
import com.mimosa.deeppokemon.service.PokePasteService;
import com.mimosa.deeppokemon.service.TeamService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class TeamApiController {
    protected static final String SUCCESS = "success";
    private final TeamService teamService;
    private final BattleTeamMigrator battleTeamMigrator;
    private final SmogonPokePastTeamCrawler smogonPokePastTeamCrawler;
    private final PokePastTeamCrawler pokePastTeamCrawler;
    private final PokePasteService pokePasteService;
    private final MongoTemplate mongoTemplate;

    public TeamApiController(TeamService teamService, BattleTeamMigrator battleTeamMigrator,
                             SmogonPokePastTeamCrawler smogonPokePastTeamCrawler, PokePastTeamCrawler pokePastTeamCrawler,
                             MongoTemplate mongoTemplate, PokePasteService pokePasteService) {
        this.teamService = teamService;
        this.battleTeamMigrator = battleTeamMigrator;
        this.smogonPokePastTeamCrawler = smogonPokePastTeamCrawler;
        this.pokePastTeamCrawler = pokePastTeamCrawler;
        this.mongoTemplate = mongoTemplate;
        this.pokePasteService = pokePasteService;
    }

    @GetMapping("/team/tag")
    public Set<Tag> updateTeam(@RequestParam("teamId") String teamId,
                               @RequestParam("collectionName") String collectionName) {
        return teamService.tagTeam(teamId, collectionName);
    }

    @PostMapping("/team/migrate")
    public String updateTeamFeatureId() {
        battleTeamMigrator.migrateBattleTeam();
        return SUCCESS;
    }

    @PostMapping("/team/thread")
    public Map<String, String> crawPokepastFromThread(@RequestParam("url") String url) {
        List<PokePastTeam> pokePastTeams = smogonPokePastTeamCrawler.craw(url, true);
        Map<String, String> res = new HashMap<>();
        res.put("result", SUCCESS);
        res.putIfAbsent("insertSize", String.valueOf(pokePastTeams.size()));
        return res;
    }

    @PostMapping("/team/pokepast")
    public PokePastTeam crawPokepast(@RequestParam("url") String url) {
        PokePastTeam pokePastTeam = pokePastTeamCrawler.craw(url);
        mongoTemplate.save(pokePastTeam);
        return pokePastTeam;
    }

    @PostMapping("/team/rmt")
    public Map<String, String> crawGen9OuRmt() {
        List<PokePastTeam> pokePastTeams = pokePasteService.crawGen9OuRmtByRss();

        Map<String, String> res = new HashMap<>();
        res.put("result", SUCCESS);
        res.putIfAbsent("insertSize", String.valueOf(pokePastTeams.size()));
        return res;
    }
}