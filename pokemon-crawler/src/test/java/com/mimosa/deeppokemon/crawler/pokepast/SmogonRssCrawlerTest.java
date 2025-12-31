/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.crawler.pokepast;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmogonRssCrawlerTest {

    @Autowired
    private SmogonRssCrawler smogonRssCrawler;

    @Value("classpath:api/sv-ou-teams.748.rss")
    private Resource rssResource;

    @Test
    void getLatestThreads() throws IOException {
        List<String> latestThreads = smogonRssCrawler.getLatestThreads(rssResource.getURL());
        assertTrue(latestThreads.size() > 0);
    }
}