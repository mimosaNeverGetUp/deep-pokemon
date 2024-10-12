/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mimosa.deeppokemon.crawler.stat.dto.PokemonAnalyzeDto;
import com.mimosa.deeppokemon.crawler.stat.dto.PokemonSetAnalyzeDto;
import com.mimosa.deeppokemon.entity.stat.PokemonAnalyze;
import com.mimosa.deeppokemon.service.AiService;
import com.mimosa.deeppokemon.service.PokemonTranslationService;
import com.mimosa.deeppokemon.utils.HttpProxy;
import org.apache.commons.lang.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PokemonAnalyzeCrawler {
    private static final Logger log = LoggerFactory.getLogger(PokemonAnalyzeCrawler.class);
    private static final String SMOGON_ANALYZE_BASE_URL = "https://pkmn.github.io/smogon/data/analyses";
    private static final String PATTERN_FORMAT_TXT = "%s.json";
    private static final String prompt = """
            You are a professional translator. You need to translate the given English text into Chinese. The text is about the Pokemon configuration analysis on Pokemon Showdown. The Chinese in the text are the proper names of Pokemon. Please keep the original Chinese text. Pay attention to the accuracy of the sentences when translating, and finally provide a text for human reading.
                      """;

    private final AiService aiService;
    private final HttpProxy httpProxy;
    private final PokemonTranslationService pokemonTranslationService;

    public PokemonAnalyzeCrawler(AiService aiService, HttpProxy httpProxy, PokemonTranslationService pokemonTranslationService) {
        this.aiService = aiService;
        this.httpProxy = httpProxy;
        this.pokemonTranslationService = pokemonTranslationService;
    }

    @RegisterReflectionForBinding(value = {PokemonAnalyzeDto.class, PokemonSetAnalyzeDto.class})
    public List<PokemonAnalyze> craw(String format, String specifyPokemon) {
        try {
            String url = initAnalyzeQuery(format);
            log.debug("analyzes craw request uri: {}", url);
            Map<String, PokemonAnalyzeDto> pokemonAnalyzeMap = httpProxy.get(url, new TypeReference<>() {
            });
            List<PokemonAnalyze> pokemonAnalyzeList = new ArrayList<>();
            for (Map.Entry<String, PokemonAnalyzeDto> entry : pokemonAnalyzeMap.entrySet()) {
                String pokemonName = entry.getKey();
                if (specifyPokemon != null && !StringUtils.equals(specifyPokemon, pokemonName)) {
                    continue;
                }
                parsePokemonAnalyze(format, pokemonName, entry.getValue(), pokemonAnalyzeList);
            }
            return pokemonAnalyzeList;
        } catch (URISyntaxException e) {
            throw new ServerErrorException(e.getLocalizedMessage(), e);
        }
    }

    private void parsePokemonAnalyze(String format, String pokemonName, PokemonAnalyzeDto pokemonAnalyzeDto, List<PokemonAnalyze> pokemonAnalyzeList) {
        try {
            String id = String.join("_", format, pokemonName);
            pokemonAnalyzeList.add(new PokemonAnalyze(id, pokemonName, format,
                    convertPokemonSetAnalyze(pokemonAnalyzeDto), convertPokemonSetChineseAnalyze(pokemonAnalyzeDto)));
        } catch (Exception e) {
            log.error("parse pokemon {} analyze failed", pokemonName, e);
        }
    }

    private Map<String, String> convertPokemonSetAnalyze(PokemonAnalyzeDto pokemonAnalyzeDto) {
        Map<String, String> pokemonSetAnalyzeMap = new HashMap<>();
        if (pokemonAnalyzeDto == null || pokemonAnalyzeDto.outdated()) {
            return pokemonSetAnalyzeMap;
        }

        for (var entry : pokemonAnalyzeDto.sets().entrySet()) {
            String setName = entry.getKey();
            String description = entry.getValue().description();
            if (description != null) {
                description = description.replace("<p>", "").replace("</p>", "\n\n");
            }
            pokemonSetAnalyzeMap.put(setName, description);
        }
        return pokemonSetAnalyzeMap;
    }

    private Map<String, String> convertPokemonSetChineseAnalyze(PokemonAnalyzeDto pokemonAnalyzeDto) throws IOException {
        Map<String, String> pokemonSetAnalyzeMap = new HashMap<>();
        if (pokemonAnalyzeDto == null || pokemonAnalyzeDto.outdated()) {
            return pokemonSetAnalyzeMap;
        }

        for (var entry : pokemonAnalyzeDto.sets().entrySet()) {
            String setName = entry.getKey();
            String description = entry.getValue().description();
            if (description != null) {
                description = description.replace("<p>", "").replace("</p>", "\n\n");
                description = pokemonTranslationService.translateText(description);
                description = aiService.translate(description, prompt);
            }
            pokemonSetAnalyzeMap.put(setName, description);
        }
        return pokemonSetAnalyzeMap;
    }

    private String initAnalyzeQuery(String format) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(SMOGON_ANALYZE_BASE_URL);
        uriBuilder.appendPath(String.format(PATTERN_FORMAT_TXT, format));
        return uriBuilder.build().toString();
    }
}