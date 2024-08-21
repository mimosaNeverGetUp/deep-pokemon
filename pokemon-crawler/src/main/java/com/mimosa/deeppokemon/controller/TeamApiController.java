/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.entity.Tag;
import com.mimosa.deeppokemon.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class TeamApiController {
    private final TeamService teamService;

    public TeamApiController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/team/tag")
    public Set<Tag> updateTeam(@RequestParam("teamId") String teamId,
                               @RequestParam("collectionName") String collectionName) {
        return teamService.tagTeam(teamId, collectionName);
    }
}