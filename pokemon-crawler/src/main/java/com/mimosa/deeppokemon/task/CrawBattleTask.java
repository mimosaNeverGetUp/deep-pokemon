/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.task;

import com.google.common.collect.Lists;
import com.mimosa.deeppokemon.analyzer.BattleAnalyzer;
import com.mimosa.deeppokemon.crawler.BattleCrawler;
import com.mimosa.deeppokemon.crawler.lock.CrawLock;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.provider.FixedReplayProvider;
import com.mimosa.deeppokemon.provider.ReplayProvider;
import com.mimosa.deeppokemon.service.BattleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class CrawBattleTask implements Callable<List<Battle>> {
    private static final Logger log = LoggerFactory.getLogger(CrawBattleTask.class);
    protected static final int DEFAULT_CRAW_PERIOD = 1000;
    protected static final int BATCH_SIZE = 50;

    private final ReplayProvider replayProvider;
    private final BattleCrawler battleCrawler;
    private final BattleAnalyzer battleAnalyzer;
    private final BattleService battleService;
    private final List<String> holdBattleLocks;

    private final boolean update;
    private long crawPeriod;

    public CrawBattleTask(ReplayProvider replayProvider, BattleCrawler battleCrawler,
                          BattleAnalyzer battleAnalyzer, BattleService battleService,
                          boolean update) {
        this(replayProvider, battleCrawler, battleAnalyzer, battleService, update, DEFAULT_CRAW_PERIOD);
    }

    public CrawBattleTask(ReplayProvider replayProvider, BattleCrawler battleCrawler,
                          BattleAnalyzer battleAnalyzer, BattleService battleService,
                          boolean update, long crawPeriod) {
        this.replayProvider = replayProvider;
        this.battleCrawler = battleCrawler;
        this.battleAnalyzer = battleAnalyzer;
        this.battleService = battleService;
        this.update = update;
        this.holdBattleLocks = new ArrayList<>();
        this.crawPeriod = crawPeriod;
    }

    public CrawBattleTask(ReplayProvider replayProvider, BattleCrawler battleCrawler,
                          BattleAnalyzer battleAnalyzer, BattleService battleService) {
        this(replayProvider, battleCrawler, battleAnalyzer, battleService, false, DEFAULT_CRAW_PERIOD);
    }

    @Override
    public List<Battle> call() {
        List<ReplaySource> replaySources = new ArrayList<>();
        try {
            while (replayProvider.hasNext()) {
                replaySources.add(replayProvider.next());
            }

            List<Battle> battles;
            if (replaySources.size() > BATCH_SIZE) {
                return batchCraw(replaySources);
            } else {
                battles = crawBattle(replaySources);
            }
            log.debug("craw battle finished, battle size: {}, start save", battles.size());
            return save(battles);
        } catch (Exception e) {
            log.error("craw battle fail", e);
        } finally {
            CrawLock.unlock(holdBattleLocks);
        }
        return Collections.emptyList();
    }

    private List<Battle> save(List<Battle> battles) {
        List<Battle> saveSuccessBattles = battleService.save(battles, update);
        try {
            battleService.insertTeam(saveSuccessBattles);
        } catch (Exception e) {
            log.error("save battle team fail", e);
        }

        try {
            battleService.insertBattleStat(saveSuccessBattles);
        } catch (Exception e) {
            log.error("save battle stat fail", e);
        }
        return saveSuccessBattles;
    }

    private List<Battle> batchCraw(List<ReplaySource> replaySources) {
        List<List<ReplaySource>> partition = Lists.partition(replaySources, BATCH_SIZE);
        List<CompletableFuture<List<Battle>>> futures = new ArrayList<>();
        for (List<ReplaySource> batch : partition) {
            FixedReplayProvider fixedReplayProvider = new FixedReplayProvider(batch);
            futures.add(battleService.crawBattle(fixedReplayProvider, battleCrawler, battleAnalyzer, true));
        }
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();
        return futures.stream().map(CompletableFuture::join).flatMap(List::stream).toList();
    }

    private List<Battle> crawBattle(List<ReplaySource> replaySources) {
        return crawBattleFromReplay(replaySources);
    }

    private List<Battle> crawBattleFromReplay(List<ReplaySource> replaySources) {
        List<Battle> allbattles = new ArrayList<>();
        for (ReplaySource replaySource : replaySources) {
            try {
                log.debug("check craw replay {} is need to craw", replaySource.replayList());
                if (!isNeedCraw(replaySource)) {
                    log.debug("not need to craw{}", replaySource.replayList());
                    continue;
                }

                log.debug("start craw replay {}", replaySource.replayList());
                List<Battle> battles = battleCrawler.craw(replaySource);
                if (battleAnalyzer != null) {
                    battleAnalyzer.analyze(battles);
                }
                allbattles.addAll(battles);
                Thread.sleep(crawPeriod);
                log.debug("end craw replay {}, sleep {} ms", replaySource.replayList(), crawPeriod);
            } catch (Exception e) {
                log.error("craw battle {} fail", replaySource.replayList(), e);
            }
        }
        return allbattles;
    }

    private boolean isNeedCraw(ReplaySource replaySource) {
        for (Replay replay : replaySource.replayList()) {
            if (replay.getId() == null) {
                continue;
            }

            if (!update && battleService.getAllBattleIds().contains(replay.getId())) {
                // battle is exist and not need to update
                continue;
            }

            if (!CrawLock.tryLock(replay.getId())) {
                // another thread is craw ,skip
                log.debug("replay {} is locked, skip", replay.getId());
            } else {
                holdBattleLocks.add(replay.getId());
                return true;
            }
        }
        return false;
    }
}