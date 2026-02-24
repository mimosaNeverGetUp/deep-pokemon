/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon.service;


import com.mimosa.deeppokemon.PrivateBattleCrawler;
import com.mimosa.deeppokemon.analyzer.BattleAnalyzer;
import com.mimosa.deeppokemon.crawler.ReplayBattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.provider.ThreadReplayProvider;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PrivateBattleService {
    private final BattleService battleService;
    private final ReplayBattleCrawler replayBattleCrawler;
    private final BattleAnalyzer battleAnalyzer;

    public PrivateBattleService(BattleService battleService, ReplayBattleCrawler replayBattleCrawler, BattleAnalyzer battleAnalyzer) {
        this.battleService = battleService;
        this.replayBattleCrawler = replayBattleCrawler;
        this.battleAnalyzer = battleAnalyzer;
    }

    public CompletableFuture<List<Battle>> crawPrivateBattle(String thread, String sourceName, String describe,
                                                             String format) {
        ThreadReplayProvider threadReplayProvider = new ThreadReplayProvider(Collections.singletonList(sourceName), thread,
                format);
        PrivateBattleCrawler privateBattleCrawler = new PrivateBattleCrawler(replayBattleCrawler, sourceName, describe);
        return battleService.crawBattle(threadReplayProvider, privateBattleCrawler, battleAnalyzer, false, false);
    }
}