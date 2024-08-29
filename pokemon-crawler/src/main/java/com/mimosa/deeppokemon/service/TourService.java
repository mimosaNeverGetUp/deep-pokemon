/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.crawler.ReplayBattleCrawler;
import com.mimosa.deeppokemon.crawler.SmogonTourReplayBattleCrawler;
import com.mimosa.deeppokemon.crawler.SmogonTourWinPlayerExtractor;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.provider.SmogonTourReplayProvider;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TourService {
    protected static final String GEN_9_OU = "gen9ou";
    protected static final String WCOP_2024_FULL_TOUR_NAME = "The World Cup of Pokémon 2024";
    protected static final String WCOP_2024_REPLAY_URL =
            "https://www.smogon.com/forums/threads/the-world-cup-of-pok%C3%A9mon-2024-replays.3742226/";
    protected static final String WCOP_FORUMS_URL = "https://www.smogon.com/forums/forums/world-cup-of-pokemon.234/";

    protected static final List<String> WCOP_FORUMS_THREAD_SUFFIX_STAGES = List.of("Qualifiers", "Round 1",
            "Quarterfinals", "Semifinals", "Finals");
    protected static final List<String> WCOP_REPLAY_STAGES = List.of("Qualifiers", "Qualifiers Round 2", "Round 1",
            "Quarterfinals", "Semifinals", "Finals");

    private final BattleService battleService;
    private final MongoTemplate mongoTemplate;
    private final ReplayBattleCrawler replayBattleCrawler;

    public TourService(BattleService battleService, MongoTemplate mongoTemplate, ReplayBattleCrawler replayBattleCrawler) {
        this.battleService = battleService;
        this.mongoTemplate = mongoTemplate;
        this.replayBattleCrawler = replayBattleCrawler;
    }

    public List<Battle> crawTour(String tourName, String format, String replayUrl, List<String> replayStages,
                                 String forumsUrl, List<String> forumsThreadSuffixStages) {
        SmogonTourReplayProvider provider = new SmogonTourReplayProvider(tourName, replayUrl, format, replayStages);
        SmogonTourWinPlayerExtractor winPlayerExtractor = new SmogonTourWinPlayerExtractor(forumsUrl, tourName,
                forumsThreadSuffixStages);
        SmogonTourReplayBattleCrawler crawler = new SmogonTourReplayBattleCrawler(replayBattleCrawler, winPlayerExtractor);
        CompletableFuture<List<Battle>> future = battleService.crawBattle(provider, crawler, false);
        return future.join();
    }

    public List<Battle> crawWcop2024() {
        return crawTour(WCOP_2024_FULL_TOUR_NAME, GEN_9_OU, WCOP_2024_REPLAY_URL, WCOP_REPLAY_STAGES,
                WCOP_FORUMS_URL, WCOP_FORUMS_THREAD_SUFFIX_STAGES);
    }
}