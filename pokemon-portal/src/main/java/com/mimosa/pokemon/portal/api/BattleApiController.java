/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.pokemon.portal.service.BattleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
@ResponseBody
public class BattleApiController {
    private final BattleService battleService;

    public BattleApiController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping("/battle/{battleid}/stat")
    @ResponseBody
    public BattleStat battleStat(@PathVariable("battleid") String battleId) {
        return battleService.battleStat(battleId);
    }
}