/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.utils;

import com.mimosa.deeppokemon.crawler.stat.dto.MonthlyBattleStatDto;
import org.junit.jupiter.api.Assertions;

//@SpringBootTest
class HttpProxyTest {
//    @Autowired
    HttpProxy httpProxy;

//    @Test
    void scrape() {
        MonthlyBattleStatDto monthlyBattleStatDto = httpProxy.get("https://pkmn.github.io/smogon/data/stats/gen9ou.json", MonthlyBattleStatDto.class);
        Assertions.assertNotNull(monthlyBattleStatDto);
        System.out.println(monthlyBattleStatDto);
    }
}