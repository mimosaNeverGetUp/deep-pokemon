/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.pokemon.portal.dto.BattleTeamDto;
import com.mimosa.pokemon.portal.entity.PageResponse;
import com.mimosa.pokemon.portal.service.BattleService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Validated
@Controller
@RequestMapping("/api")
public class TeamApiController {
    private final BattleService battleService;

    public TeamApiController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping("/teams")
    @ResponseBody
    public PageResponse<BattleTeamDto> teams(@RequestParam(required = false, name = "pokemons") List<String> pokemons,
                                    @RequestParam(required = false, name = "tags") List<String> tags,
                                    @RequestParam(name = "page") @Min(0) int page,
                                    @RequestParam(name = "row") @Min(1) @Max(100) int row) {
        return battleService.team(page, row, tags, pokemons, null, null);
    }
}