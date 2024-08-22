/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.pokemon.portal.dto.TeamGroupDto;
import com.mimosa.pokemon.portal.entity.PageResponse;
import com.mimosa.pokemon.portal.service.BattleService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
public class TeamApiController {
    private final BattleService battleService;

    public TeamApiController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping("/v2/teams")
    public PageResponse<TeamGroupDto> teamGroup(@RequestParam(required = false, name = "pokemons") List<String> pokemons,
                                                @RequestParam(required = false, name = "tags") List<String> tags,
                                                @RequestParam(required = false, name = "sort", defaultValue = "maxRating") String sort,
                                                @RequestParam(required = false, name = "groupName") String groupName,
                                                @RequestParam(name = "page") @Min(0) int page,
                                                @RequestParam(name = "row") @Min(1) @Max(20) int row) {
        if (pokemons != null) {
            // 避免顺序不一样不走缓存
            Collections.sort(pokemons);
        }
        return battleService.teamGroup(page, row, tags, pokemons, sort, groupName);
    }
}