/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.entity.ReplaySource;
import com.mimosa.deeppokemon.entity.SmogonTourReplay;
import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CsvTourReplayProvider implements ReplayProvider {
    private static final Logger log = LoggerFactory.getLogger(CsvTourReplayProvider.class);
    protected static final int TOUR_REPLAY_PARAM_SIZE = 9;
    protected static final String PLAYER_ID_FORMAT = "%s_%s_%s";
    protected static final String TOUR = "tour";

    private final String[] csvContents;
    private final Deque<ReplaySource> replaySources = new LinkedList<>();
    private boolean initialized = false;

    public CsvTourReplayProvider(String csvContents) {
        this.csvContents = csvContents.split("\n");
    }

    @Override
    public ReplaySource next() {
        return replaySources.isEmpty() ? null : replaySources.pollFirst();
    }

    @Override
    public boolean hasNext() {
        init();
        return !replaySources.isEmpty();
    }

    private synchronized void init() {
        if (!initialized) {
            extractFromCsv();
            initialized = true;
        }
    }

    private void extractFromCsv() {
        for (String line : csvContents) {
            String[] contents = line.split("\\|");
            if (contents.length < TOUR_REPLAY_PARAM_SIZE) {
                log.error("Invalid CSV line: {}", line);
                continue;
            }

            // build replay
            String tourName = contents[0].trim();
            String tourStage = contents[1].trim();
            String replayId = contents[2].trim();
            String replayTier = contents[3].trim();
            String replayWinnerSmogonName = contents[4].trim().toLowerCase();
            List<TourPlayer> tourPlayers = new ArrayList<>();
            TourPlayer tourPlayerA = buildTourPlayer(tourName, replayTier, contents[5].trim().toLowerCase(), contents[6].trim());
            TourPlayer tourPlayerB = buildTourPlayer(tourName, replayTier, contents[7].trim().toLowerCase(), contents[8].trim());
            tourPlayers.add(tourPlayerA);
            tourPlayers.add(tourPlayerB);
            TourPlayer winPlayer = null;
            if (StringUtils.equals(replayWinnerSmogonName, tourPlayerA.getName())) {
                winPlayer = tourPlayerA;
            } else if (StringUtils.equals(replayWinnerSmogonName, tourPlayerB.getName())) {
                winPlayer = tourPlayerB;
            } else {
                log.error("Invalid replay winner: {}", replayWinnerSmogonName);
            }

            SmogonTourReplay smogonTourReplay = new SmogonTourReplay(replayId);
            smogonTourReplay.setTourName(tourName);
            smogonTourReplay.setTourPlayers(tourPlayers);
            smogonTourReplay.setStage(tourStage);
            smogonTourReplay.setWinPlayer(winPlayer);
            replaySources.add(new ReplaySource(List.of(TOUR, tourName), Collections.singletonList(smogonTourReplay)));
        }
    }


    private TourPlayer buildTourPlayer(String tourName, String format, String name, String team) {
        String tourPlayerId = String.format(PLAYER_ID_FORMAT, tourName, format, name);
        return new TourPlayer(name, tourPlayerId, team, null);
    }
}