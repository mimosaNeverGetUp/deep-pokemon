/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.entity.ReplaySource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class OltTourReplayProvider implements ReplayProvider {
    private static final Logger log = LoggerFactory.getLogger(OltTourReplayProvider.class);
    protected static final String THREAD_REPLAY_STAGE_CLASS = "div.bbWrapper";

    private boolean initialized = false;
    private final String tourName;
    private final String replayThreadUrl;
    private final String format;
    private final Set<String> stageTitles;
    private final Deque<ReplaySource> replaySources = new LinkedList<>();

    public OltTourReplayProvider(String tourName, String replayThreadUrl, String format, Set<String> stageTitles) {
        this.tourName = tourName;
        this.replayThreadUrl = replayThreadUrl;
        this.format = format;
        this.stageTitles = stageTitles;
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
            extractFromReplayThread();
            initialized = true;
        }
    }

    private void extractFromReplayThread() {
        try {
            Document doc = Jsoup.connect(replayThreadUrl).timeout(60000).get();
            Element replayComment = doc.select(THREAD_REPLAY_STAGE_CLASS).first();
            if (replayComment == null) {
                log.error("Replay thread comment is null");
                return;
            }

            String currentStage = null;
            String firstPlayerName = null;
            String secondPlayerName = null;
            for (var ele : replayComment.getAllElements()) {
                if (ele.is("b") && stageTitles.contains(ele.ownText().trim())) {
                    currentStage = ele.text().trim();
                }

                if (ele.is("a")) {
                    if (currentStage == null) {
                        // logo, host, and help members href
                        continue;
                    }

                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}