/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.crawler.pokepast.SmogonPokePastTeamCrawler;
import com.mimosa.deeppokemon.crawler.pokepast.SmogonRssCrawler;
import com.mimosa.deeppokemon.entity.pokepast.PokePastTeam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PokePasteService {
    private static final Logger log = LoggerFactory.getLogger(PokePasteService.class);
    protected static final String SV_OU_RMT_RSS_URL = "https://www.smogon.com/forums/forums/sv-ou-teams.748.rss";

    private final SmogonPokePastTeamCrawler smogonPokePastTeamCrawler;
    private final SmogonRssCrawler smogonRssCrawler;

    public PokePasteService(SmogonPokePastTeamCrawler smogonPokePastTeamCrawler, SmogonRssCrawler smogonRssCrawler) {
        this.smogonPokePastTeamCrawler = smogonPokePastTeamCrawler;
        this.smogonRssCrawler = smogonRssCrawler;
    }

    public List<PokePastTeam> crawRmtByRss(URL rssUrl) {
        List<PokePastTeam> pokePastTeams = new ArrayList<>();
        List<String> latestThreads = smogonRssCrawler.getLatestThreads(rssUrl);
        for (String thread : latestThreads) {
            log.info("Getting poke past teams for {}", thread);
            pokePastTeams.addAll(smogonPokePastTeamCrawler.craw(thread, false));
        }
        return pokePastTeams;
    }

    public List<PokePastTeam> crawGen9OuRmtByRss() {
        try {
            return crawRmtByRss(new URL(SV_OU_RMT_RSS_URL));
        } catch (MalformedURLException e) {
            log.error("sv ou rmt url {} exception", SV_OU_RMT_RSS_URL, e);
            return Collections.emptyList();
        }
    }
}