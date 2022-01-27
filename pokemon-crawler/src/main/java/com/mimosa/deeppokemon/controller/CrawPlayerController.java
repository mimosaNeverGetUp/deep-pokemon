/*
 * The MIT License
 *
 * Copyright (c) [2022] [Xiaocong Huang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.crawler.LadderBattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Player;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
public class CrawPlayerController {

    @Autowired
    LadderBattleCrawler ladderBattleCrawler;

    @Autowired
    BattleService battleService;

    @Autowired
    PlayerService playerService;

    private static Logger logger = LoggerFactory.getLogger(CrawPlayerController.class);

    @RequestMapping("craw")
    @ResponseBody
    public String crawPlyaer(String name) {
        List<Battle> list = ladderBattleCrawler.crawPlayerBattle(name);
        Player player = new Player();
        player.setName(name);
        playerService.save(player);
        battleService.savaAll(list);
        return "success";
    }

    @RequestMapping("crawLadder")
    @ResponseBody
    public String crawLadder()  {
        try {
            battleService.crawLadder();
            return "success";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "fail";
        }

    }

}
