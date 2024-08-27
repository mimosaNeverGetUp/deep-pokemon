/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class TourReplayProvider implements ReplayProvider {

    private static final Logger log = LoggerFactory.getLogger(TourReplayProvider.class);
    private static final String REPLAY_POKEMONSHOWDOWN_COM = "replay.pokemonshowdown.com";
    protected static final String TOUR = "tour";
    protected static final String GEN_9_OU = "gen9ou";

    private boolean initialized = false;
    private final String tourName;
    private final String replayThreadUrl;
    private final Stack<ReplaySource> replaySources;

    public TourReplayProvider(String tourName, String replayThreadUrl) {
        this.tourName = tourName;
        this.replayThreadUrl = replayThreadUrl;
        this.replaySources = new Stack<>();
    }

    @Override
    public ReplaySource next() {
        return replaySources.isEmpty() ? null : replaySources.pop();
    }

    @Override
    public boolean hasNext() {
        init();
        return !replaySources.isEmpty();
    }

    private synchronized void init() {
        if (!initialized) {
            extractFromReplayThread();
            initialized = true;
        }
    }

    private void extractFromReplayThread() {
        List<Replay> replayUrls = new ArrayList<>();
        Set<String> existBattleIds = new HashSet<>();
        try {
            Document doc = Jsoup.connect(replayThreadUrl).get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String attr = link.attr("abs:href");
                if (attr.contains(REPLAY_POKEMONSHOWDOWN_COM) && attr.contains(GEN_9_OU)) {
                    String id = extractBattleId(attr);
                    if (existBattleIds.add(id)) {
                        replayUrls.add(new Replay(id));
                    }
                }
            }
            int startIndex = 0;
            int endIndex = 3;
            while (startIndex < replayUrls.size()) {
                replaySources.add(new ReplaySource(List.of(TOUR, tourName), replayUrls.subList(startIndex, endIndex)));
                startIndex = endIndex;
                endIndex += 3;
                if (endIndex > replayUrls.size()) {
                    endIndex = replayUrls.size();
                }
            }

        } catch (IOException e) {
            log.error("extract replay thread fail", e);
        }
    }

    private String extractBattleId(String attr) {
        String[] split = attr.split("/");
        String s = split[split.length - 1];
        return s.split("\\?")[0];
    }
}