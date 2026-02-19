/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class ThreadReplayProvider implements ReplayProvider{
    private static final Logger log = LoggerFactory.getLogger(ThreadReplayProvider.class);
    private static final String REPLAY_URL_ROOT = "https://replay.pokemonshowdown.com/";
    private static final String REPLAY_POKEMONSHOWDOWN_COM = "replay.pokemonshowdown.com";
    protected static final int CONNECT_TIME_OUT = 60000;

    private boolean initialized = false;
    private final List<String> replayTypes;
    private final String pageUrl;
    private final String format;
    private final Deque<ReplaySource> replaySources = new LinkedList<>();

    public ThreadReplayProvider(List<String> replayTypes, String pageUrl, String format) {
        this.replayTypes = replayTypes;
        this.pageUrl = pageUrl;
        this.format = format;
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
            extractFromUrl();
            initialized = true;
        }
    }

    private void extractFromUrl() {
        if (pageUrl.startsWith(REPLAY_URL_ROOT)) {
            String battleId = extractBattleId(pageUrl);
            replaySources.add(new ReplaySource(replayTypes, Collections.singletonList(new Replay(battleId))));
        } else {
            extractFromReplayThread();
        }
    }

    private void extractFromReplayThread() {
        Set<String> existBattleIds = new HashSet<>();
        try {
            Document doc = Jsoup.connect(pageUrl).timeout(CONNECT_TIME_OUT).get();
            extractFromDoc(doc, existBattleIds);
            extractAnotherPage(pageUrl, doc, existBattleIds);
        } catch (IOException e) {
            log.error("extract replay thread fail", e);
        }
    }

    private void extractFromDoc(Document doc, Set<String> existBattleIds) {
        for (Element element : doc.select("a")) {
            String replay = element.attr("abs:href");
            if (replay.contains(REPLAY_POKEMONSHOWDOWN_COM) && replay.contains(format)) {
                String id = extractBattleId(replay);
                if (existBattleIds.add(id)) {
                    replaySources.add(new ReplaySource(replayTypes, Collections.singletonList(new Replay(id))));
                }
            }
        }
    }

    private void extractAnotherPage(String threadUrl, Document document, Set<String> existBattleIds) {
        int page = 2;
        URI uri = URI.create(threadUrl).resolve(String.format("page-%d", page));
        while (isUriExist(uri, document)) {
            try {
                document = Jsoup.connect(uri.toString()).timeout(CONNECT_TIME_OUT).get();
                extractFromDoc(document, existBattleIds);
                uri = URI.create(threadUrl).resolve(String.format("page-%d", ++page));
            } catch (IOException e) {
                log.error("extract replay from {} fail", uri);
            }
        }
    }

    private boolean isUriExist(URI uri, Document document) {
        Elements hrefs = document.select("a");
        for (Element href : hrefs) {
            String url = href.attr("abs:href");
            if (StringUtils.equals(url, uri.toString())) {
                return true;
            }
        }
        return false;
    }

    private String extractBattleId(String attr) {
        String[] split = attr.split("/");
        String s = split[split.length - 1];
        return s.split("\\?")[0];
    }
}