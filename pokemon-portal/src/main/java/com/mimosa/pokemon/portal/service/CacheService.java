/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.service;

import com.mimosa.pokemon.portal.dto.MonthlyPokemonUsageDto;
import com.mimosa.pokemon.portal.dto.PlayerRankDTO;
import com.mimosa.pokemon.portal.entity.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CacheService {
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    protected static final String MAX_RATING = "maxRating";
    protected static final String UNIQUE_PLAYER_NUM = "uniquePlayerNum";
    protected static final String LATEST_BATTLE_DATE = "latestBattleDate";
    protected static final String MAX_PLAYER_WIN_DIF = "maxPlayerWinDif";

    private final BattleService battleService;
    private final PlayerService playerService;
    private final StatsService statsService;

    public CacheService(BattleService battleService, PlayerService playerService, StatsService statsService) {
        this.battleService = battleService;
        this.playerService = playerService;
        this.statsService = statsService;
    }

    public boolean loadHeatData() {
        boolean result = loadRankAndPlayer();
        result = result && loadTeam();
        result = result && loadMonthlyStat();
        return result;
    }

    public boolean loadTeam() {
        log.info("start load team group");
        boolean res = loadTeamGroup("last_3_days", false);
        res &= loadTeamGroup("last_7_days", false);
        res &= loadTeamGroup("last_30_days", false);
        res &= loadTeamGroup("last_90_days", false);
        res &= loadTeamGroup("tour_wcop_2024", true);
        res &= loadTeamGroup("tour_olt_xi", true);
        return res;
    }

    private boolean loadTeamGroup(String groupName, boolean isTour) {
        log.info("start load team group {}", groupName);
        try {
            if (isTour) {
                loadTeamGroupBySort(groupName, MAX_PLAYER_WIN_DIF);
            } else {
                loadTeamGroupBySort(groupName, MAX_RATING);
            }
            loadTeamGroupBySort(groupName, UNIQUE_PLAYER_NUM);
            loadTeamGroupBySort(groupName, LATEST_BATTLE_DATE);
            loadTeamGroupByTeamType(groupName, Collections.singletonList("BALANCE"), isTour);
            loadTeamGroupByTeamType(groupName, List.of("STAFF", "BALANCE_STAFF"), isTour);
            loadTeamGroupByTeamType(groupName, Collections.singletonList("ATTACK"), isTour);
            loadTeamGroupByTeamType(groupName, Collections.singletonList("BALANCE_ATTACK"), isTour);
        } catch (Exception e) {
            log.error("load team group error", e);
            return false;
        }
        return true;
    }

    private void loadTeamGroupBySort(String groupName, String sort) {
        battleService.teamGroup(0, 7, null, null, null, sort, groupName);
        battleService.teamGroup(1, 7, null, null, null, sort, groupName);
        battleService.teamGroup(2, 7, null, null, null, sort, groupName);
    }

    private void loadTeamGroupByTeamType(String groupName, List<String> tags, boolean isTour) {
        if (isTour) {
            battleService.teamGroup(0, 7, tags, null, null, MAX_PLAYER_WIN_DIF, groupName);
            battleService.teamGroup(1, 7, tags, null, null, MAX_PLAYER_WIN_DIF, groupName);
            battleService.teamGroup(2, 7, tags, null, null, MAX_PLAYER_WIN_DIF, groupName);
        } else {
            battleService.teamGroup(0, 7, tags, null, null, MAX_RATING, groupName);
            battleService.teamGroup(1, 7, tags, null, null, MAX_RATING, groupName);
            battleService.teamGroup(2, 7, tags, null, null, MAX_RATING, groupName);
        }
    }

    public boolean loadRankAndPlayer() {
        log.info("start load rank and player");
        boolean res = loadRankAndPlayer(0, 20);
        res &= loadRankAndPlayer(1, 20);
        res &= loadRankAndPlayer(2, 20);
        return res;
    }

    public boolean loadRankAndPlayer(int page, int row) {
        try {
            log.info("load rank, page {},row {}", page, row);
            PageResponse<PlayerRankDTO> players = playerService.rank(page, row);
            for (var player : players.data()) {
                playerService.queryPlayerLadderRank(player.getName());
                battleService.listBattleByName(player.getName(), 0, 25);
            }
        } catch (Exception e) {
            log.error("load rank and player fail", e);
            return false;
        }
        return true;
    }

    public boolean loadMonthlyStat() {
        log.info("start load monthly stat");
        boolean res = loadMonthlyStat("gen9ou");
        res &= loadMonthlyStat("gen9uu");
        res &= loadMonthlyStat("gen9ubers");
        res &= loadMonthlyStat("gen9vgc2024");
        res &= loadMonthlyStat("gen9nationaldex");
        return res;
    }

    public boolean loadMonthlyStat(String format) {
        try {
            statsService.queryMeta(format);
            loadMonthlyStat(format, 0, 20, true);
            loadMonthlyStat(format, 1, 20, false);
            loadMonthlyStat(format, 2, 20, false);
            loadMonthlyStat(format, 0, 100, false);
        } catch (Exception e) {
            log.error("load monthly stat fail", e);
            return false;
        }
        return true;
    }

    public void loadMonthlyStat(String format, int page, int row, boolean loadTeam) {
        PageResponse<MonthlyPokemonUsageDto> usages = statsService.queryUsage(format, page, row);
        for (var usage : usages.data()) {
            statsService.queryMoveSet(format, usage.getName());
            statsService.queryPokemonSet(format, usage.getName());
            if (loadTeam) {
                List<String> pokemons = new ArrayList<>();
                pokemons.add(usage.getName());
                battleService.teamGroup(0, 5, null, pokemons, null, MAX_RATING, "last_90_days");
            }
        }
    }
}