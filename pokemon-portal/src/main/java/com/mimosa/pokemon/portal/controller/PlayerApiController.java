/*
 * The MIT License
 *
 * Copyright (c) [2023]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mimosa.pokemon.portal.controller;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Ladder;
import com.mimosa.deeppokemon.entity.LadderRank;
import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.pokemon.portal.dto.PlayerRankDTO;
import com.mimosa.pokemon.portal.entity.PageResponse;
import com.mimosa.pokemon.portal.service.BattleService;
import com.mimosa.pokemon.portal.service.PlayerService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Validated
@Controller
@RequestMapping("/api")
public class PlayerApiController {

    private final BattleService battleService;

    private final PlayerService playerService;

    public PlayerApiController(BattleService battleService, PlayerService playerService) {
        this.battleService = battleService;
        this.playerService = playerService;
    }

    @GetMapping("/rank/update-time")
    @ResponseBody
    public Map<String, String> getDataUpdateDate() {
        return Collections.singletonMap("date",
                DateTimeFormatter.ISO_LOCAL_DATE.format(playerService.getLatestLadder().getDate()));
    }

    @ResponseBody
    @GetMapping("/rank")
    public PageResponse<PlayerRankDTO> rankList(@Min(0) int page, @Min(1) int row) {

        int start = page * row;
        int end = start + row;
        Ladder ladder = playerService.getLatestLadder();
        List<LadderRank> ladderRank = ladder.getLadderRankList();
        ladderRank.sort(Comparator.comparingInt(LadderRank::getRank));
        List<LadderRank> segmentLadderRank = new ArrayList<>(ladderRank.subList(start, end));
        List<Team> teamList = battleService.listTeamByLadderRank(segmentLadderRank);

        List<PlayerRankDTO> playerRankDTOS = new ArrayList<>();

        int i = 0;
        for (var rank : segmentLadderRank) {
            var playerRankDTO = new PlayerRankDTO();
            playerRankDTO.setRank(rank.getRank());
            playerRankDTO.setElo(rank.getElo());
            playerRankDTO.setName(rank.getName());
            playerRankDTO.setGxe(rank.getGxe());
            playerRankDTO.setRecentTeam(teamList.subList(2 * i, 2 * i + 2));
            playerRankDTOS.add(playerRankDTO);
            ++i;
        }
        return new PageResponse<>(ladderRank.size(), page, row, playerRankDTOS);
    }


    @ResponseBody
    @GetMapping("/player/{username}")
    public PlayerRankDTO getPlayerRank(@PathVariable("username") @NotNull String name) {
        return playerService.queryPlayerLadderRank(name);
    }

    @ResponseBody
    @GetMapping("/player/{username}/battle")
    public PageResponse<Battle> getPlayerBattleRecord(@PathVariable("username") @NotNull String name,
                                               @Min(0) int page, @Min(1) int row) {
        return battleService.listBattleByName(name, page, row);
    }
}
