/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.pokemon.portal.service.BattleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BattleApiController {
    private final BattleService battleService;

    public BattleApiController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping("/battle/{battleid}/stat")
    public BattleStat battleStat(@PathVariable("battleid") String battleId) {
        return battleService.battleStat(battleId);
    }

    @GetMapping("/battle/{battleid}")
    public Battle battle(@PathVariable("battleid") String battleId) {
        return battleService.battle(battleId);
    }
}