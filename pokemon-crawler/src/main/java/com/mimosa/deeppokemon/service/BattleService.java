/*
 * The MIT License
 *
 * Copyright (c) [2022] [Xiaocong Huang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.analyzer.BattleAnalyzer;
import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.crawler.BattleCrawler;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.analyzer.entity.PlayerStat;
import com.mimosa.deeppokemon.provider.PlayerReplayProvider;
import com.mimosa.deeppokemon.task.CrawBattleTask;
import com.mongodb.bulk.BulkWriteInsert;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("crawBattleService")
public class BattleService {
    private static final Logger log = LoggerFactory.getLogger(BattleService.class);
    private static final String ID = "_id";
    private static final String BATTLE = "battle";
    private static final ThreadPoolExecutor CRAW_BATTLE_EXECUTOR = new ThreadPoolExecutor(12, 12, 0,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private final MongoTemplate mongoTemplate;

    private final BattleCrawler battleCrawler;

    private final BattleAnalyzer battleAnalyzer;

    public BattleService(MongoTemplate mongoTemplate, BattleCrawler battleCrawler, BattleAnalyzer battleAnalyzer) {
        this.mongoTemplate = mongoTemplate;
        this.battleCrawler = battleCrawler;
        this.battleAnalyzer = battleAnalyzer;
    }

    @CacheEvict("battleIds")
    public void save(Battle battle) {
        mongoTemplate.save(battle);
    }

    @CacheEvict("battleIds")
    public List<Battle> savaAll(List<Battle> battles) {
        if (battles.isEmpty()) {
            return battles;
        }

        BulkOperations bulkOperations = null;
        try {
            bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BATTLE);
        } catch (BulkOperationException e) {
            if (!isDuplicateKeyException(e)) {
                throw e;
            }
        }

        return bulkOperations.insert(battles).execute().getInserts().stream().map(BulkWriteInsert::getIndex).map(battles::get)
                .collect(Collectors.toList());
    }

    private boolean isDuplicateKeyException(BulkOperationException exception) {
        return exception.getErrors().stream().allMatch(error -> error.getCode() == 11000);
    }

    public List<Battle> find100BattleSortByDate() {
        Query query = new BasicQuery("{}").with(Sort.by(Sort.Order.desc("date"))).limit(100);
        List<Battle> battles = mongoTemplate.find(query, Battle.class, BATTLE);
        return battles;
    }

    @Cacheable(cacheNames = "battleIds")
    public Set<String> getAllBattleIds() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project(Fields.fields(ID))
        );

        return new HashSet<>(mongoTemplate.aggregate(aggregation, BATTLE, String.class).getMappedResults());
    }

    @NotNull
    public CompletableFuture<List<Battle>> crawBattle(PlayerReplayProvider replayProvider) {
        CrawBattleTask crawBattleTask = new CrawBattleTask(replayProvider, battleCrawler, this);
        CompletableFuture<List<Battle>> future = CompletableFuture.supplyAsync(crawBattleTask::call, CRAW_BATTLE_EXECUTOR);
        future.thenAcceptAsync(battles -> battleAnalyzer.analyze(battles), CRAW_BATTLE_EXECUTOR);
        return future;
    }

    public Collection<BattleStat> savaAll(Collection<BattleStat> battleStats) {
        return mongoTemplate.insertAll(battleStats);
    }
}