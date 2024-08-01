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
import com.mimosa.deeppokemon.crawler.BattleCrawler;
import com.mimosa.deeppokemon.crawler.PokemonInfoCrawler;
import com.mimosa.deeppokemon.entity.*;
import com.mimosa.deeppokemon.entity.stat.*;
import com.mimosa.deeppokemon.provider.FixedReplayProvider;
import com.mimosa.deeppokemon.provider.ReplayProvider;
import com.mimosa.deeppokemon.task.CrawBattleTask;
import com.mimosa.deeppokemon.task.entity.CrawAnalyzeBattleFuture;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.bulk.BulkWriteInsert;
import com.mongodb.bulk.BulkWriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service("crawBattleService")
public class BattleService {
    private static final Logger log = LoggerFactory.getLogger(BattleService.class);
    private static final String ID = "_id";
    private static final String BATTLE = "battle";
    protected static final int POKEMONS_BITS = 10000;

    private final int crawPeriodMillisecond;
    private final ThreadPoolExecutor crawBattleExecutor;
    private final ThreadPoolExecutor analyzeBattleExecutor;

    private final MongoTemplate mongoTemplate;
    private final BattleCrawler battleCrawler;
    private final BattleAnalyzer battleAnalyzer;
    private final PokemonInfoCrawler pokemonInfoCrawler;

    private final Set<String> battleIds = ConcurrentHashMap.newKeySet();

    public BattleService(MongoTemplate mongoTemplate, BattleCrawler battleCrawler,
                         BattleAnalyzer battleAnalyzer, PokemonInfoCrawler pokemonInfoCrawler,
                         @Value("${CRAW_BATTLE_POOL_SIZE:8}") int crawBattlePoolSize,
                         @Value("${ANALYZE_BATTLE_POOL_SIZE:3}") int analyzeBattlePoolSize,
                         @Value("${CRAW_PERIOD_MILLISECOND:1000}") int crawPeriodMillisecond) {
        this.mongoTemplate = mongoTemplate;
        this.battleCrawler = battleCrawler;
        this.battleAnalyzer = battleAnalyzer;
        this.pokemonInfoCrawler = pokemonInfoCrawler;
        crawBattleExecutor = new ThreadPoolExecutor(crawBattlePoolSize, crawBattlePoolSize, 0,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        analyzeBattleExecutor = new ThreadPoolExecutor(analyzeBattlePoolSize, analyzeBattlePoolSize, 0,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        this.crawPeriodMillisecond = crawPeriodMillisecond;
    }

    @RegisterReflectionForBinding({Battle.class, Team.class, Pokemon.class})
    public Battle findBattle(String battleId) {
        return mongoTemplate.findById(battleId, Battle.class, BATTLE);
    }

    public List<Battle> insert(List<Battle> battles) {
        if (battles.isEmpty()) {
            return battles;
        }
        try {
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BATTLE);
            BulkWriteResult result = bulkOperations.insert(battles).execute();
            battleIds.addAll(battles.stream().map(Battle::getBattleID).toList());
            return result.getInserts().stream().map(BulkWriteInsert::getIndex).map(battles::get)
                    .toList();
        } catch (BulkOperationException e) {
            log.error("save battle fail", e);
            Set<Integer> errorIndexs = e.getErrors().stream().map(BulkWriteError::getIndex).collect(Collectors.toSet());
            List<Battle> battleList = IntStream.range(0, battles.size())
                    .filter(i -> !errorIndexs.contains(i))
                    .mapToObj(battles::get)
                    .toList();
            battleIds.addAll(battleList.stream().map(Battle::getBattleID).toList());
            return battleList;
        }
    }

    /**
     * update battles
     * since can not update at once, performance is not good when battle size is big
     */
    public List<Battle> update(List<Battle> battles) {
        for (Battle battle : battles) {
            mongoTemplate.save(battle, BATTLE);
        }
        return battles;
    }

    public List<Battle> save(List<Battle> battles, boolean overwrite) {
        List<Battle> saveResult;
        if (battles.isEmpty()) {
            return battles;
        }

        if (overwrite) {
            saveResult = update(battles);
        } else {
            saveResult = insert(battles);
            insertTeam(battles);
        }
        return saveResult;
    }

    public void insertTeam(List<Battle> battles) {
        List<BattleTeam> battleTeams = new ArrayList<>(battles.size() * 2);
        for (Battle battle : battles) {
            int index = 0;
            for (Team team : battle.getTeams()) {
                String battleTeamId = String.format("%s_%d", battle.getBattleID(), index);
                byte[] teamId = calTeamId(team);
                BattleTeam battleTeam = new BattleTeam(battleTeamId, battle.getBattleID(), teamId, battle.getDate(),
                        battle.getType(), battle.getAvageRating(), team.getPlayerName(), team.getTier(),
                        team.getPokemons(), team.getTagSet());
                battleTeams.add(battleTeam);
                index++;
            }
        }

        try {
            mongoTemplate.insertAll(battleTeams);
        } catch (Exception e) {
            throw new ServerErrorException("save battle team fail", e);
        }
    }

    public byte[] calTeamId(Team team) {
        BitSet bitSet = new BitSet(POKEMONS_BITS);
        for (Pokemon pokemon : team.getPokemons()) {
            PokemonInfo pokemonInfo = pokemonInfoCrawler.getPokemonInfo(pokemon.getName());
            if (pokemonInfo == null) {
                log.warn("can't find pokemon info for {}", pokemon.getName());
                continue;
            }
            bitSet.set(pokemonInfo.getNumber());
        }
        return bitSet.toByteArray();
    }

    public List<Battle> find100BattleSortByDate() {
        Query query = new BasicQuery("{}").with(Sort.by(Sort.Order.desc("date"))).limit(100);
        return mongoTemplate.find(query, Battle.class, BATTLE);
    }

    public Set<String> getAllBattleIds() {
        if (battleIds.isEmpty()) {
            battleIds.addAll(queryAllBattleIds());
        }
        return battleIds;
    }

    private Set<String> queryAllBattleIds() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project(Fields.fields(ID))
        );

        List<Battle> battles = mongoTemplate.aggregate(aggregation, BATTLE, Battle.class).getMappedResults();

        return battles.stream().map(Battle::getBattleID).collect(Collectors.toSet());
    }

    public CrawAnalyzeBattleFuture crawBattleAndAnalyze(ReplayProvider replayProvider) {
        CompletableFuture<List<Battle>> crawFuture = crawBattle(replayProvider);
        CompletableFuture<List<BattleStat>> analyzeFuture = analyzeBattleAfterCraw(crawFuture);
        return new CrawAnalyzeBattleFuture(crawFuture, analyzeFuture);
    }

    private CompletableFuture<List<Battle>> crawBattle(ReplayProvider replayProvider) {
        CrawBattleTask crawBattleTask = new CrawBattleTask(replayProvider, battleCrawler, this,
                false, crawPeriodMillisecond);
        return CompletableFuture.supplyAsync(crawBattleTask::call, crawBattleExecutor);
    }

    public CompletableFuture<List<BattleStat>> analyzeBattleAfterCraw(CompletableFuture<List<Battle>> crawBattleFuture) {
        return crawBattleFuture.thenApplyAsync(battleAnalyzer::analyze, analyzeBattleExecutor)
                .thenApplyAsync(this::insert);
    }

    public List<BattleStat> insert(Collection<BattleStat> battleStats) {
        return new ArrayList<>(mongoTemplate.insertAll(battleStats));
    }

    @RegisterReflectionForBinding({BattleStat.class, PlayerStat.class, PokemonBattleStat.class, TurnStat.class,
            TurnPlayerStat.class, TurnPokemonStat.class})
    public BattleStat getBattleStat(String battleId) {
        Battle battle = findBattle(battleId);
        if (battle == null) {
            // craw and save
            CrawBattleTask crawBattleTask = new CrawBattleTask(new FixedReplayProvider(Collections.singletonList(battleId)),
                    battleCrawler, this);
            battle = crawBattleTask.call().get(0);
        } else if (battle.getLog() == null) {
            // craw and update
            CrawBattleTask crawBattleTask = new CrawBattleTask(new FixedReplayProvider(Collections.singletonList(battleId)),
                    battleCrawler, this, true);
            battle = crawBattleTask.call().get(0);
        }

        List<BattleStat> battleStats = battleAnalyzer.analyze(Collections.singletonList(battle));
        try {
            insert(battleStats);
        } catch (Exception e) {
            log.warn("save battle stat fail", e);
        }
        return battleStats.get(0);
    }
}