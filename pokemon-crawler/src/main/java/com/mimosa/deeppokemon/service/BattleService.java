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
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service("crawBattleService")
public class BattleService {
    private static final Logger log = LoggerFactory.getLogger(BattleService.class);
    private static final String ID = "_id";
    private static final String BATTLE = "battle";
    protected static final String TEAM_ID = "teamId";
    protected static final String LATEST_BATTLE_DATE = "latestBattleDate";
    protected static final String BATTLE_DATE = "battleDate";
    protected static final String RATING = "rating";
    protected static final String MAX_RATING = "maxRating";
    protected static final String POKEMONS = "pokemons";
    protected static final String TAG_SET = "tagSet";
    protected static final String TIER = "tier";
    protected static final String PLAYER_NAME = "playerName";
    protected static final String PLAYER_SET = "playerSet";
    protected static final String TEAMS = "teams";
    protected static final String UNIQUE_PLAYER_NUM = "uniquePlayerNum";
    protected static final String BATTLE_TEAM = "battle_team";
    protected static final String REPLAY_NUM = "replayNum";
    protected static final String POKEMONS_NAME = "pokemons.name";
    protected static final String TOUR_BATTLE = "tour_battle";

    private final int crawPeriodMillisecond;
    private final ThreadPoolExecutor crawBattleExecutor;
    private final ThreadPoolExecutor analyzeBattleExecutor;

    private final MongoTemplate mongoTemplate;
    private final BattleCrawler battleCrawler;
    private final BattleAnalyzer battleAnalyzer;
    private final PokemonInfoCrawler pokemonInfoCrawler;
    private final TeamService teamService;

    private final Set<String> battleIds = ConcurrentHashMap.newKeySet();

    public BattleService(MongoTemplate mongoTemplate, BattleCrawler battleCrawler,
                         BattleAnalyzer battleAnalyzer, PokemonInfoCrawler pokemonInfoCrawler,
                         TeamService teamService,
                         @Value("${CRAW_BATTLE_POOL_SIZE:8}") int crawBattlePoolSize,
                         @Value("${ANALYZE_BATTLE_POOL_SIZE:3}") int analyzeBattlePoolSize,
                         @Value("${CRAW_PERIOD_MILLISECOND:1000}") int crawPeriodMillisecond) {
        this.mongoTemplate = mongoTemplate;
        this.battleCrawler = battleCrawler;
        this.battleAnalyzer = battleAnalyzer;
        this.pokemonInfoCrawler = pokemonInfoCrawler;
        this.teamService = teamService;
        crawBattleExecutor = new ThreadPoolExecutor(crawBattlePoolSize, crawBattlePoolSize, 0,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        analyzeBattleExecutor = new ThreadPoolExecutor(analyzeBattlePoolSize, analyzeBattlePoolSize, 0,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        this.crawPeriodMillisecond = crawPeriodMillisecond;
    }

    @RegisterReflectionForBinding({Battle.class, Pokemon.class})
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
        }
        return saveResult;
    }

    public void insertTeam(Collection<Battle> battles) {
        List<BattleTeam> battleTeams = new ArrayList<>(battles.size() * 2);
        for (Battle battle : battles) {
            int index = 0;
            for (BattleTeam team : battle.getBattleTeams()) {
                String battleTeamId = String.format("%s_%d", battle.getBattleID(), index);
                byte[] teamId = calTeamId(team.getPokemons());
                float rating = Math.max(battle.getAvageRating(), team.getRating());
                team.setId(battleTeamId);
                team.setRating(rating);
                team.setTeamId(teamId);
                team.setBattleId(battle.getBattleID());
                team.setBattleDate(battle.getDate());
                team.setBattleType(battle.getType());
                battleTeams.add(team);
                index++;
            }
        }

        try {
            mongoTemplate.insertAll(battleTeams);
        } catch (Exception e) {
            throw new ServerErrorException("save battle team fail", e);
        }
    }

    public byte[] calTeamId(List<Pokemon> pokemons) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Integer> pokemonNumbers = new ArrayList<>();
        for (Pokemon pokemon : pokemons) {
            PokemonInfo pokemonInfo = pokemonInfoCrawler.getPokemonInfo(pokemon.getName());
            if (pokemonInfo == null) {
                log.warn("can't find pokemon info for {}", pokemon.getName());
                continue;
            }
            pokemonNumbers.add(pokemonInfo.getNumber());
        }
        pokemonNumbers.sort(Integer::compareTo);
        for (int pokemonNumber : pokemonNumbers) {
            stringBuilder.append(String.format("%04d", pokemonNumber));
        }
        return stringBuilder.toString().getBytes();
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

        List<Battle> battleList = new ArrayList<>();
        battleList.addAll(mongoTemplate.aggregate(aggregation, BATTLE, Battle.class).getMappedResults());
        battleList.addAll(mongoTemplate.aggregate(aggregation, TOUR_BATTLE, Battle.class).getMappedResults());

        return battleList.stream().map(Battle::getBattleID).collect(Collectors.toSet());
    }

    public CrawAnalyzeBattleFuture crawBattleAndAnalyze(ReplayProvider replayProvider) {
        CompletableFuture<List<Battle>> crawFuture = crawBattle(replayProvider);
        CompletableFuture<List<BattleStat>> analyzeFuture = analyzeBattleAfterCraw(crawFuture);
        return new CrawAnalyzeBattleFuture(crawFuture, analyzeFuture);
    }

    public CompletableFuture<List<Battle>> crawBattle(ReplayProvider replayProvider) {
        return crawBattle(replayProvider, battleCrawler);
    }

    public CompletableFuture<List<Battle>> crawBattle(ReplayProvider replayProvider, BattleCrawler crawler) {
        CrawBattleTask crawBattleTask = new CrawBattleTask(replayProvider, crawler, this,
                false, crawPeriodMillisecond);
        return CompletableFuture.supplyAsync(crawBattleTask::call, crawBattleExecutor);
    }

    public CompletableFuture<List<BattleStat>> analyzeBattleAfterCraw(CompletableFuture<List<Battle>> crawBattleFuture) {
        return crawBattleFuture.thenApplyAsync(battleAnalyzer::analyze, analyzeBattleExecutor)
                .thenApplyAsync(battles -> {
                    try {
                        insertTeam(battles);
                    } catch (Exception e) {
                        log.error("save battle team fail", e);
                    }

                    return insertBattleStat(battles);
                });
    }

    private List<BattleStat> insertBattleStat(Collection<Battle> battles) {
        return insert(battles.stream().map(Battle::getBattleStat).filter(Objects::nonNull).toList());
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

        battleAnalyzer.analyze(Collections.singletonList(battle));
        try {
            insert(Collections.singletonList(battle.getBattleStat()));
        } catch (Exception e) {
            log.warn("save battle stat fail", e);
        }
        return battle.getBattleStat();
    }


    @CacheEvict(value = "teamGroup", allEntries = true)
    public synchronized void updateTeam() {
        updateTeam(new TeamGroupDetail(LocalDate.now().minusDays(3), LocalDate.now()
                , "team_group_last_3_days", "team_set_last_3_days"));
        updateTeam(new TeamGroupDetail(LocalDate.now().minusDays(7), LocalDate.now()
                , "team_group_last_7_days", "team_set_last_7_days"));
        updateTeam(new TeamGroupDetail(LocalDate.now().minusDays(30), LocalDate.now()
                , "team_group_last_30_days", "team_set_last_30_days"));
        updateTeam(new TeamGroupDetail(LocalDate.now().minusDays(90), LocalDate.now()
                , "team_group_last_90_days", "team_set_last_90_days"));
    }

    public synchronized void updateTeam(TeamGroupDetail teamGroupDetail) {
        log.info("start update team group {}", teamGroupDetail.teamGroupCollectionName());
        try {
            updateTeamGroup(teamGroupDetail);
            teamService.updateTeamSet(teamGroupDetail);

            ensureTeamCollectionIndex(teamGroupDetail);
        } catch (Exception e) {
            log.error("update team fail, teamGroupDetail={}", teamGroupDetail, e);
        }
    }

    private void ensureTeamCollectionIndex(TeamGroupDetail teamGroupDetail) {
        createIndex(teamGroupDetail.teamGroupCollectionName(), LATEST_BATTLE_DATE, Sort.Direction.DESC);
        createIndex(teamGroupDetail.teamGroupCollectionName(), MAX_RATING, Sort.Direction.DESC);
        createIndex(teamGroupDetail.teamGroupCollectionName(), UNIQUE_PLAYER_NUM, Sort.Direction.DESC);
        createCompoundIndex(teamGroupDetail.teamGroupCollectionName(), List.of(TAG_SET, LATEST_BATTLE_DATE),
                Sort.Direction.DESC);
        createCompoundIndex(teamGroupDetail.teamGroupCollectionName(), List.of(TAG_SET, UNIQUE_PLAYER_NUM),
                Sort.Direction.DESC);
        createCompoundIndex(teamGroupDetail.teamGroupCollectionName(), List.of(TAG_SET, MAX_RATING),
                Sort.Direction.DESC);
        createCompoundIndex(teamGroupDetail.teamGroupCollectionName(), List.of(POKEMONS_NAME, LATEST_BATTLE_DATE),
                Sort.Direction.DESC);
        createCompoundIndex(teamGroupDetail.teamGroupCollectionName(), List.of(POKEMONS_NAME, MAX_RATING),
                Sort.Direction.DESC);
        createCompoundIndex(teamGroupDetail.teamGroupCollectionName(), List.of(POKEMONS_NAME, UNIQUE_PLAYER_NUM),
                Sort.Direction.DESC);

        createIndex(teamGroupDetail.teamSetCollectionName(), "minReplayDate", Sort.Direction.DESC);
    }

    private void createIndex(String collectionName, String indexName, Sort.Direction direction) {
        Index index = new Index(indexName, direction);
        mongoTemplate.indexOps(collectionName).ensureIndex(index);
    }

    private void createCompoundIndex(String collectionName, List<String> indexNames, Sort.Direction direction) {
        if (indexNames.isEmpty()) {
            return;
        }

        int sort = direction == Sort.Direction.ASC ? 1 : -1;
        Document document = new Document();
        for (String indexName : indexNames) {
            document.append(indexName, sort);
        }
        mongoTemplate.indexOps(collectionName).ensureIndex(new CompoundIndexDefinition(document));
    }

    private void updateTeamGroup(TeamGroupDetail teamGroupDetail) {
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where(BATTLE_DATE).gte(teamGroupDetail.start()).lte(teamGroupDetail.end()));
        GroupOperation groupOperation = Aggregation.group(TEAM_ID)
                .max(BATTLE_DATE).as(LATEST_BATTLE_DATE)
                .max(RATING).as(MAX_RATING)
                .first(POKEMONS).as(POKEMONS)
                .first(TAG_SET).as(TAG_SET)
                .first(TIER).as(TIER)
                .addToSet(PLAYER_NAME).as(PLAYER_SET)
                .push("$$ROOT").as(TEAMS);

        AddFieldsOperation addFieldsOperationBuilder = Aggregation.addFields()
                .addFieldWithValue(UNIQUE_PLAYER_NUM, ArrayOperators.arrayOf(PLAYER_SET).length())
                .addFieldWithValue(REPLAY_NUM, ArrayOperators.arrayOf(TEAMS).length())
                .build();

        MergeOperation mergeOperation = Aggregation.merge()
                .intoCollection(teamGroupDetail.teamGroupCollectionName())
                .whenDocumentsMatch(MergeOperation.WhenDocumentsMatch.updateWith(Aggregation.newAggregation(
                        SetOperation.set(LATEST_BATTLE_DATE).toValue("$$new.latestBattleDate")
                                .and().set(MAX_RATING).toValue("$$new.maxRating")
                                .and().set(POKEMONS).toValue("$$new.pokemons")
                                .and().set(TEAMS).toValue("$$new.teams")
                                .and().set(UNIQUE_PLAYER_NUM).toValue("$$new.uniquePlayerNum")
                                .and().set(REPLAY_NUM).toValue("$$new.replayNum")
                )))
                .whenDocumentsDontMatch(MergeOperation.WhenDocumentsDontMatch.insertNewDocument())
                .build();

        Aggregation aggregation = Aggregation.newAggregation(matchOperation,
                groupOperation,
                addFieldsOperationBuilder,
                Aggregation.stage("{ $project : { 'playerSet': 0, 'pokemons.moves': 0, 'pokemons.item': 0," +
                        " 'pokemons.ability': 0 } }"),
                mergeOperation);
        AggregationOptions options = AggregationOptions.builder()
                .allowDiskUse(true)
                .skipOutput()
                .build();
        mongoTemplate.aggregate(aggregation.withOptions(options), BATTLE_TEAM,
                TeamGroup.class);
        Query query = new Query(Criteria.where(LATEST_BATTLE_DATE).lt(teamGroupDetail.start()));
        mongoTemplate.remove(query, teamGroupDetail.teamGroupCollectionName());
    }

    public void updateMonthTeam(LocalDate month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String yearMonth = formatter.format(month);
        log.info("start craw monthly team,month: {}", yearMonth);
        String teamGroupCollectionName = String.format("team_group_%s", yearMonth);
        String teamSetCollectionName = String.format("team_set_%s", yearMonth);
        TeamGroupDetail teamGroupDetail = new TeamGroupDetail(month.with(firstDayOfMonth()), month.with(lastDayOfMonth()),
                teamGroupCollectionName, teamSetCollectionName);
        updateTeam(teamGroupDetail);
    }
}