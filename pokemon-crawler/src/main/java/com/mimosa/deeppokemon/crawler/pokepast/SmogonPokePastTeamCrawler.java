/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.pokepast;

import com.mimosa.deeppokemon.entity.pokepast.PokePastTeam;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SmogonPokePastTeamCrawler {
    private static final Logger log = LoggerFactory.getLogger(SmogonPokePastTeamCrawler.class);
    private static final int TIMEOUT_MS = 60000;
    private static final String HOST_POKEPAST_ES = "https://pokepast.es/";

    private final PokePastTeamCrawler pokePastTeamCrawler;
    private final MongoTemplate mongoTemplate;

    public SmogonPokePastTeamCrawler(PokePastTeamCrawler pokePastTeamCrawler, MongoTemplate mongoTemplate) {
        this.pokePastTeamCrawler = pokePastTeamCrawler;
        this.mongoTemplate = mongoTemplate;
    }

    public List<PokePastTeam> craw(String threadUrl) {
        List<PokePastTeam> pokePastTeamList = new ArrayList<>();
        Set<String> existPokePastTeamIdSet = new HashSet<>();
        try {
            Document document = Jsoup.connect(threadUrl).timeout(TIMEOUT_MS).get();
            pokePastTeamList.addAll(crawPokePastTeamFromPage(document, existPokePastTeamIdSet));
            pokePastTeamList.addAll(extractAnotherPage(threadUrl, document, existPokePastTeamIdSet));
        } catch (Exception e) {
            log.error("craw pokepast from page {} fail", threadUrl, e);
        }
        mongoTemplate.insertAll(pokePastTeamList);
        return pokePastTeamList;
    }

    private List<PokePastTeam> crawPokePastTeamFromPage(Document page, Set<String> existPokePastTeamIdSet) {
        List<PokePastTeam> pokePastTeamList = new ArrayList<>();
        Elements hrefs = page.select("a");
        for (Element href : hrefs) {
            try {
                String url = href.attr("abs:href");
                if (url.startsWith(HOST_POKEPAST_ES)) {
                    String id = getPokePastId(url);
                    Query query = new Query(Criteria.where("_id").is(id));
                    if (!mongoTemplate.exists(query, PokePastTeam.class) && existPokePastTeamIdSet.add(id)) {
                        pokePastTeamList.add(pokePastTeamCrawler.craw(url));
                    }
                }
            } catch (Exception e) {
                log.error("craw pokepast from {} fail", href);
            }
        }
        return pokePastTeamList;
    }

    private String getPokePastId(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (URISyntaxException e) {
            throw new ServerErrorException("get pokepast id from " + url, e);
        }
    }

    private List<PokePastTeam> extractAnotherPage(String threadUrl, Document document, Set<String> existPokePastTeamIdSet) {
        List<PokePastTeam> pokePastTeamList = new ArrayList<>();
        int page = 2;
        URI uri = URI.create(threadUrl).resolve(String.format("page-%d", page));
        while (isUriExist(uri, document)) {
            try {
                document = Jsoup.connect(uri.toString()).timeout(TIMEOUT_MS).get();
                pokePastTeamList.addAll(crawPokePastTeamFromPage(document, existPokePastTeamIdSet));
                uri = URI.create(threadUrl).resolve(String.format("page-%d", ++page));
            } catch (IOException e) {
                log.error("craw pokepast from {} fail", uri);
            }
        }
        return pokePastTeamList;
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
}