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
import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.provider.ReplayProvider;
import com.mimosa.deeppokemon.service.BattleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class CrawBattleTask implements Callable<List<Battle>> {
    private static final Logger log = LoggerFactory.getLogger(CrawBattleTask.class);
    protected static final int DEFAULT_CRAW_PERIOD = 1000;

    private final ReplayProvider replayProvider;

    private final BattleCrawler battleCrawler;

    private final BattleService battleService;

    private final List<String> holdBattleLocks;

    private final boolean update;

    private long crawPeriod;

    public CrawBattleTask(ReplayProvider replayProvider, BattleCrawler battleCrawler, BattleService battleService,
                          boolean update) {
        this(replayProvider, battleCrawler, battleService, update, DEFAULT_CRAW_PERIOD);
    }

    public CrawBattleTask(ReplayProvider replayProvider, BattleCrawler battleCrawler, BattleService battleService,
                          boolean update, long crawPeriod) {
        this.replayProvider = replayProvider;
        this.battleCrawler = battleCrawler;
        this.battleService = battleService;
        this.update = update;
        holdBattleLocks = new ArrayList<>();
        this.crawPeriod = crawPeriod;
    }

    public CrawBattleTask(ReplayProvider replayProvider, BattleCrawler battleCrawler, BattleService battleService) {
        this(replayProvider, battleCrawler, battleService, false, DEFAULT_CRAW_PERIOD);
    }

    @Override
    public List<Battle> call() {
        List<Battle> battles = new ArrayList<>();
        try {
            while (replayProvider.hasNext()) {
                ReplaySource replaySource = replayProvider.next();
                battles.addAll(crawBattle(replaySource.replayList(), replaySource.replayType()));
            }
            log.debug("craw battle finished, battle size: {}, start save", battles.size());
            battleService.save(battles, update);
            log.debug("save battle finished");
        } catch (Exception e) {
            log.error("craw battle fail", e);
        } finally {
            CrawLock.unlock(holdBattleLocks);
        }
        return battles;
    }

    private List<Battle> crawBattle(List<Replay> replays, List<String> replayType) {
        try {
            List<Battle> battles = crawBattleFromReplay(replays);
            battles.forEach(battle -> battle.setType(replayType));
            return battles;
        } catch (InterruptedException e) {
            log.error("Thread interrupted", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            if (replays != null) {
                log.error("craw battle from replay fail, battles id: {}",
                        replays.stream().map(Replay::getId).collect(Collectors.toSet()), e);
            } else {
                log.error("craw battle replay fail", e);
            }
        }
        return Collections.emptyList();
    }

    private List<Battle> crawBattleFromReplay(List<Replay> replays) throws InterruptedException {
        List<Battle> battles = new ArrayList<>();
        for (Replay replay : replays) {
            log.debug("check craw replay {} is need to craw", replay.getId());
            if (!isNeedCraw(replay)) {
                log.debug("not need to craw{}", replay.getId());
                continue;
            }

            log.debug("start craw replay {}", replay.getId());
            Battle craw = battleCrawler.craw(replay);
            battles.add(craw);
            log.debug("end craw replay {}, sleep {} ms", replay.getId(), crawPeriod);
            Thread.sleep(crawPeriod);
        }
        return battles;
    }

    private boolean isNeedCraw(Replay replay) {
        // check replay is need to craw
        if (replay.getId() == null) {
            return false;
        }

        if (!CrawLock.tryLock(replay.getId())) {
            // another thread is craw ,skip
            log.debug("replay {} is locked, skip", replay.getId());
            return false;
        } else {
            holdBattleLocks.add(replay.getId());
        }
        return update || !battleService.getAllBattleIds().contains(replay.getId());
    }
}