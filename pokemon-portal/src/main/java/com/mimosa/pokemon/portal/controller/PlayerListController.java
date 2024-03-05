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

package com.mimosa.pokemon.portal.controller;

import com.mimosa.deeppokemon.entity.Ladder;
import com.mimosa.deeppokemon.entity.LadderRank;
import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.pokemon.portal.entity.JsonArrayResponse;
import com.mimosa.pokemon.portal.service.BattleService;
import com.mimosa.pokemon.portal.service.PlayerService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Validated
@Controller
public class PlayerListController {
    @Autowired
    private BattleService battleService;

    @Autowired
    private PlayerService playerService;

    @RequestMapping("/list")
    public String list() {
        return "redirect:/rank?page=1";
    }

    @RequestMapping("/rank")
    public String list(Model model, @Min(1) int page) {
        int start = (page - 1) * 25;
        int end = start + 25;
        // TODO: 2022/2/4 rank列表去重，根本解决可能需要清楚当天多次爬取造成的重复数据
        Ladder ladder = playerService.getLatestLadder();
        List<LadderRank> ladderRank = ladder.getLadderRankList();
        List<LadderRank> segmentLadderRank = new ArrayList<>(ladderRank.subList(start, end));
        ladderRank.sort(Comparator.comparingInt(LadderRank::getRank).reversed());

        List<Team> teamList = battleService.listTeamByLadderRank(segmentLadderRank);
        model.addAttribute("rankList", segmentLadderRank);
        model.addAttribute("ladderDate", ladder.getDate());
        model.addAttribute("teamList", teamList);
        model.addAttribute("page", page);
        return "playerRank";
    }

    @ResponseBody
    @RequestMapping("/json/rank")
    public JsonArrayResponse rankList(int page, int limit) {
        return playerService.listPlayerDTORank(page, limit);
    }


    @RequestMapping("/test/rank")
    public String testRank(int page, int limit) {
        return "layuitest";
    }
}
