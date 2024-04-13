/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

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