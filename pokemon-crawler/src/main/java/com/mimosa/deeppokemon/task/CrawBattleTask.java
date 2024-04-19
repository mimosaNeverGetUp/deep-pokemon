/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.task;

import com.mimosa.deeppokemon.crawler.BattleCrawler;
import com.mimosa.deeppokemon.crawler.lock.CrawLock;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.provider.ReplayProvider;
import com.mimosa.deeppokemon.service.BattleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

public class CrawBattleTask implements Callable<List<Battle>> {
    private static final Logger log = LoggerFactory.getLogger(CrawBattleTask.class);

    private final ReplayProvider replayProvider;

    private final BattleCrawler battleCrawler;

    private final BattleService battleService;
    private final List<Lock> holdBattleLocks;

    public CrawBattleTask(ReplayProvider replayProvider, BattleCrawler battleCrawler, BattleService battleService) {
        this.replayProvider = replayProvider;
        this.battleCrawler = battleCrawler;
        this.battleService = battleService;
        holdBattleLocks = new ArrayList<>();
    }

    @Override
    public List<Battle> call() {
        List<Battle> battles = new ArrayList<>();
        try {
            while (replayProvider.hasNext()) {
                List<Replay> replays = replayProvider.next().replayList();
                try {
                    battles.addAll(crawBattleFromReplay(replays));
                } catch (Exception e) {
                    log.error("craw battle from replay fail", e);
                }
            }
            battleService.savaAll(battles);
        } finally {
            for (Lock lock : holdBattleLocks) {
                lock.unlock();
            }
        }
        return battles;
    }

    private List<Battle> crawBattleFromReplay(List<Replay> replays) {
        List<Battle> battles = new ArrayList<>();
        for (Replay replay : replays) {

            // check replay is need to craw
            if (replay.id() == null) {
                continue;
            }
            Lock battleLock = CrawLock.getBattleLock(replay.id());
            if (!battleLock.tryLock()) {
                // another thread is craw ,skip
                continue;
            }
            holdBattleLocks.add(battleLock);
            if (battleService.getAllBattleIds().contains(replay.id())) {
                continue;
            }

            battles.add(battleCrawler.craw(replay));
        }
        return battles;
    }
}