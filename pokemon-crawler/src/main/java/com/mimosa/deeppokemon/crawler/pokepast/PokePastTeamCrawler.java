/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.pokepast;

import com.mimosa.deeppokemon.crawler.PokemonInfoCrawler;
import com.mimosa.deeppokemon.entity.pokepast.PokePastTeam;
import com.mimosa.deeppokemon.service.BattleService;
import org.bson.types.Binary;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PokePastTeamCrawler {
    private static final Logger log = LoggerFactory.getLogger(PokePastTeamCrawler.class);

    private static final String POKEPAST_ES = "https://pokepast.es/";
    private static final int TIMEOUT_MS = 60000;
    private static final Pattern AUTHOR_PATTERN = Pattern.compile(Pattern.quote("by ") + "(.+)");
    private static final Pattern FORMAT_PATTERN = Pattern.compile(Pattern.quote("Format: ") + "(.+)");

    private final PokemonInfoCrawler pokemonInfoCrawler;
    private final BattleService battleService;

    public PokePastTeamCrawler(PokemonInfoCrawler pokemonInfoCrawler, BattleService battleService) {
        this.pokemonInfoCrawler = pokemonInfoCrawler;
        this.battleService = battleService;
    }

    PokePastTeam craw(String uriStr) {
        if (uriStr == null || !uriStr.startsWith(POKEPAST_ES)) {
            throw new IllegalArgumentException("Invalid URL: " + uriStr);
        }

        URI uri;
        Document doc;
        try {
            uri = new URI(uriStr);
            doc = Jsoup.connect(uri.toString()).timeout(TIMEOUT_MS).get();
        } catch (URISyntaxException | IOException e) {
            throw new ServerErrorException("connect to pokepast fail", e);
        }
        return extractTeamFromHtml(doc, uri);
    }

    private PokePastTeam extractTeamFromHtml(Document doc, URI uri) {
        String pokePastId = getPokePastId(uri);
        Elements pokemonSetDocs = doc.select("pre");
        if (pokemonSetDocs.size() != 6) {
            log.error("pokepast team {} is invalid", pokePastId);
            throw new IllegalArgumentException("pokepast team " + pokePastId + " is invalid");
        }
        Map<String, String> pokemonSets = new HashMap<>();
        for (Element pokemonSetDoc : pokemonSetDocs) {
            String name = extratPokemonName(pokemonSetDoc);
            if (name == null) {
                log.error("can not extract pokemon name from {}", pokemonSetDoc.wholeText());
                throw new IllegalArgumentException("pokepast team " + pokePastId + " is invalid");
            }
            pokemonSets.put(name, pokemonSetDoc.wholeText());
        }

        // extract team info
        Element pokePastInfo = doc.selectFirst("aside");
        String author = extractAuthor(pokePastInfo);
        String format = extractFormat(pokePastInfo);
        Binary teamId = new Binary(battleService.calTeamId(pokemonSets.keySet()));
        return new PokePastTeam(pokePastId, uri.toString(), format, author, teamId, pokemonSets);
    }

    private String extratPokemonName(Element pokemonSetDoc) {
        for (Element ele : pokemonSetDoc.getAllElements()) {
            String text = ele.ownText().trim();
            if (text.contains("@")) {
                text = text.substring(0, text.indexOf("@")).trim();
            }
            if (text.contains("(")) {
                // when pokemon hava nickname, pokemon name is put at bucket
                String bucketContext = text.substring(text.indexOf("(") + 1, text.indexOf(")")).trim();
                if (pokemonInfoCrawler.getPokemonInfo(bucketContext) != null) {
                    return bucketContext;
                }
                text = text.substring(0, text.indexOf("(")).trim();
            }
            if (pokemonInfoCrawler.getPokemonInfo(text) != null) {
                return text;
            }
        }

        return null;
    }

    private String extractFormat(Element pokePastInfo) {
        if (pokePastInfo == null) {
            return null;
        }
        for (Element element : pokePastInfo.getAllElements()) {
            String text = element.ownText();
            Matcher matcher = FORMAT_PATTERN.matcher(text);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return null;
    }

    private String extractAuthor(Element pokePastInfo) {
        if (pokePastInfo == null) {
            return null;
        }
        for (Element element : pokePastInfo.getAllElements()) {
            String text = element.ownText();
            Matcher matcher = AUTHOR_PATTERN.matcher(text);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return null;
    }

    private String getPokePastId(URI uri) {
        String path = uri.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
}