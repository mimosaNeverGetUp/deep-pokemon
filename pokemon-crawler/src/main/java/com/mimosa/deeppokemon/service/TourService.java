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
import com.mimosa.deeppokemon.entity.tour.Tour;
import com.mimosa.deeppokemon.provider.SmogonTourReplayProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class TourService {
    private static final Logger log = LoggerFactory.getLogger(TourService.class);

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

        List<Battle> battles = future.join();

        if (!battles.isEmpty()) {
            updateTour(tourName, format);
            updatePlayerRecord(tourName, format);
        } else {
            log.error("craw empty battle of {}", tourName);
        }

        return battles;
    }

    private void updateTour(String tourName, String format) {
        Tour tour = mongoTemplate.findById(tourName, Tour.class);
        if (tour == null) {
            tour = new Tour();
            tour.setId(tourName);
            tour.setTires(Collections.singletonList(format));
            mongoTemplate.insert(tour);
        } else {
            Set<String> tiers = new HashSet<>(tour.getTires());
            if (tiers.add(format)) {
                // update new craw tier
                tour.setTires(tiers.stream().toList());
                mongoTemplate.save(tour);
            }
        }
    }

    private void updatePlayerRecord(String tourName, String format) {
        Criteria criteria = Criteria.where("tourId").is(tourName)
                .and("format").is(format);
        new Query(criteria);
    }

    public List<Battle> crawWcop2024() {
        return crawTour(WCOP_2024_FULL_TOUR_NAME, GEN_9_OU, WCOP_2024_REPLAY_URL, WCOP_REPLAY_STAGES,
                WCOP_FORUMS_URL, WCOP_FORUMS_THREAD_SUFFIX_STAGES);
    }
}