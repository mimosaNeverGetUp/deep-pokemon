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
import java.util.stream.Collectors;

public class CrawBattleTask implements Callable<List<Battle>> {
    private static final Logger log = LoggerFactory.getLogger(CrawBattleTask.class);

    private final ReplayProvider replayProvider;

    private final BattleCrawler battleCrawler;

    private final BattleService battleService;

    private final List<String> holdBattleLocks;

    private final boolean update;

    public CrawBattleTask(ReplayProvider replayProvider, BattleCrawler battleCrawler, BattleService battleService,
                          boolean update) {
        this.replayProvider = replayProvider;
        this.battleCrawler = battleCrawler;
        this.battleService = battleService;
        this.update = update;
        holdBattleLocks = new ArrayList<>();
    }

    public CrawBattleTask(ReplayProvider replayProvider, BattleCrawler battleCrawler, BattleService battleService) {
        this(replayProvider, battleCrawler, battleService, false);
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
                    log.error("craw battle from replay fail, battles id: {}",
                            replays.stream().map(Replay::id).collect(Collectors.toSet()), e);
                }
            }

            battleService.save(battles, update);
        } finally {
            CrawLock.unlock(holdBattleLocks);
        }
        return battles;
    }

    private List<Battle> crawBattleFromReplay(List<Replay> replays) {
        List<Battle> battles = new ArrayList<>();
        for (Replay replay : replays) {
            if (!isNeedCraw(replay)) {
                continue;
            }

            battles.add(battleCrawler.craw(replay));
        }
        return battles;
    }

    private boolean isNeedCraw(Replay replay) {
        // check replay is need to craw
        if (replay.id() == null) {
            return false;
        }

        if (!CrawLock.tryLock(replay.id())) {
            // another thread is craw ,skip
            return false;
        } else {
            holdBattleLocks.add(replay.id());
        }
        return update || !battleService.getAllBattleIds().contains(replay.id());
    }
}