/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.service.microservice;

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("pokemon-crawler")
public interface CrawlerApi {
    @GetMapping("/api/battle/{battleid}/stat")
    BattleStat battleStat(@PathVariable("battleid") String battleId);

    @GetMapping("/api/stats/craw")
    boolean crawMonthlyStats(@RequestParam("format") String format);
}