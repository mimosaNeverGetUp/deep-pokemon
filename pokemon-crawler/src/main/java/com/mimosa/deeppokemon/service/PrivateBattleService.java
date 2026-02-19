/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon.service;


import com.mimosa.deeppokemon.PrivateBattleCrawler;
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

    public PrivateBattleService(BattleService battleService, ReplayBattleCrawler replayBattleCrawler) {
        this.battleService = battleService;
        this.replayBattleCrawler = replayBattleCrawler;
    }

    public CompletableFuture<List<Battle>> crawPrivateBattle(String thread, String sourceName, String describe,
                                                              String format) {
        ThreadReplayProvider threadReplayProvider = new ThreadReplayProvider(Collections.singletonList(sourceName), thread,
                format);
        PrivateBattleCrawler privateBattleCrawler = new PrivateBattleCrawler(replayBattleCrawler, sourceName, describe);
        return battleService.crawBattle(threadReplayProvider, privateBattleCrawler, null, false);
    }
}