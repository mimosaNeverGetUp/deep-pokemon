/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.service;

import com.mimosa.pokemon.portal.dto.PlayerRankDTO;
import com.mimosa.pokemon.portal.entity.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CacheService {
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    protected static final String MAX_RATING = "maxRating";
    protected static final String UNIQUE_PLAYER_NUM = "uniquePlayerNum";
    protected static final String LATEST_BATTLE_DATE = "latestBattleDate";

    private final BattleService battleService;
    private final PlayerService playerService;

    public CacheService(BattleService battleService, PlayerService playerService) {
        this.battleService = battleService;
        this.playerService = playerService;
    }

    public boolean loadHeatData() {
        boolean result = loadRankAndPlayer();
        result = result && loadTeam();
        return result;
    }

    public boolean loadTeam() {
        log.info("start load team group");
        boolean res = loadTeamGroup("last_3_days");
        res &= loadTeamGroup("last_7_days");
        res &= loadTeamGroup("last_30_days");
        res &= loadTeamGroup("last_90_days");
        return res;
    }

    private boolean loadTeamGroup(String groupName) {
        log.info("start load team group {}", groupName);
        try {
            loadTeamGroupBySort(groupName, MAX_RATING);
            loadTeamGroupBySort(groupName, UNIQUE_PLAYER_NUM);
            loadTeamGroupBySort(groupName, LATEST_BATTLE_DATE);
            loadTeamGroupByTeamType(groupName, "BALANCE");
            loadTeamGroupByTeamType(groupName, "STAFF");
            loadTeamGroupByTeamType(groupName, "ATTACK");
            loadTeamGroupByTeamType(groupName, "BALANCE_ATTACK");
        } catch (Exception e) {
            log.error("load team group error", e);
            return false;
        }
        return true;
    }

    private void loadTeamGroupBySort(String groupName, String sort) {
        battleService.teamGroup(0, 7, null, null, sort, groupName);
        battleService.teamGroup(1, 7, null, null, sort, groupName);
        battleService.teamGroup(2, 7, null, null, sort, groupName);
    }

    private void loadTeamGroupByTeamType(String groupName, String type) {
        List<String> tags = Collections.singletonList(type);
        battleService.teamGroup(0, 7, tags, null, MAX_RATING, groupName);
        battleService.teamGroup(1, 7, tags, null, MAX_RATING, groupName);
        battleService.teamGroup(2, 7, tags, null, MAX_RATING, groupName);
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
}