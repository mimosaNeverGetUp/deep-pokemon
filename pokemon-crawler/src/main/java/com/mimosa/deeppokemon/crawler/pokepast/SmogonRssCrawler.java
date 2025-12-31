/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.crawler.pokepast;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Component
public class SmogonRssCrawler {
    private static final Logger logger = LoggerFactory.getLogger(SmogonRssCrawler.class);
    private static final Map<String, Set<String>> guidMap = new HashMap<>();

    public List<String> getLatestThreads(URL rssUrl) {
        logger.info("Getting latest threads for {}", rssUrl);
        List<String> latestThreads = new ArrayList<>();
        guidMap.putIfAbsent(rssUrl.toString(), new HashSet<>());
        Set<String> guids = guidMap.get(rssUrl.toString());
        try {
            try (InputStream stream = rssUrl.openStream()) {
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(stream));
                feed.getEntries().forEach(entry -> {
                    if (guids.add(entry.getUri())) {
                        latestThreads.add(entry.getLink());
                    } else {
                        logger.info("thread {} is not latest", entry.getLink());
                    }
                });
            }
        } catch (IOException | FeedException e) {
            logger.error("Error while getting latest threads for {}", rssUrl, e);
        }
        return latestThreads;
    }
}