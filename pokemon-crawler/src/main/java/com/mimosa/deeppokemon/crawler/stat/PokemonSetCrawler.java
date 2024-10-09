/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mimosa.deeppokemon.crawler.stat.dto.PokemonSetDto;
import com.mimosa.deeppokemon.entity.stat.PokemonSet;
import com.mimosa.deeppokemon.utils.HttpProxy;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class PokemonSetCrawler {
    private static final Logger log = LoggerFactory.getLogger(PokemonSetCrawler.class);
    private static final String SMOGON_SET_BASE_URL = "https://pkmn.github.io/smogon/data/sets/";
    private static final String PATTERN_FORMAT_TXT = "%s.json";
    private static final String POKEMON_SET_TEXT_TEMPLATE = """
            %s @ %s
            Ability: %s
            Tera Type: %s
            EVs: %s
            %s Nature
            - %s
            - %s
            - %s
            - %s
            """;
    protected static final String EVS_DELIMITER = "/";
    protected static final String SET_LIST_JOIN = EVS_DELIMITER;
    protected static final String EVS_SET_DELIMITER = " | ";

    private final HttpProxy httpProxy;

    public PokemonSetCrawler(HttpProxy httpProxy) {
        this.httpProxy = httpProxy;
    }

    @RegisterReflectionForBinding(PokemonSetDto.class)
    public List<PokemonSet> craw(String format) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM");
            String statId = dateTimeFormatter.format(LocalDate.now().minusMonths(1)) + format;
            String url = initSetQuery(format);
            log.debug("set craw request uri: {}", url);
            Map<String, Map<String, PokemonSetDto>> pokemonSetMap = httpProxy.get(url, new TypeReference<>() {});
            return convertPokemonSets(statId, pokemonSetMap);
        } catch (URISyntaxException e) {
            throw new ServerErrorException(e.getLocalizedMessage(), e);
        }
    }

    private List<PokemonSet> convertPokemonSets(String statId, Map<String, Map<String, PokemonSetDto>> pokemonSetMap) {
        List<PokemonSet> pokemonSets = new ArrayList<>();
        for (Map.Entry<String, Map<String, PokemonSetDto>> entry : pokemonSetMap.entrySet()) {
            String name = entry.getKey();
            Map<String, String> setMap = new LinkedHashMap<>();
            try {
                for (Map.Entry<String, PokemonSetDto> setEntry : entry.getValue().entrySet()) {
                    String setName = setEntry.getKey();
                    PokemonSetDto set = setEntry.getValue();
                    setMap.put(setName, convertPokemonSetText(name, set));
                }
                PokemonSet pokemonSet = new PokemonSet(statId + name, name, statId, setMap);
                pokemonSets.add(pokemonSet);
            } catch (Exception e) {
                log.error("error occurred while parsing pokemon set {}", name, e);
            }
        }

        return pokemonSets;
    }

    private String convertPokemonSetText(String name, PokemonSetDto set) {
        List<Object> moves = set.moves();
        return String.format(POKEMON_SET_TEXT_TEMPLATE, name,
                convertCommonSetText(set.item(), SET_LIST_JOIN),
                convertCommonSetText(set.ability(), SET_LIST_JOIN),
                convertCommonSetText(set.teratypes(), SET_LIST_JOIN),
                convertEvsSetText(set.evs()),
                convertCommonSetText(set.nature(), SET_LIST_JOIN),
                convertCommonSetText(set.moves().get(0), SET_LIST_JOIN),
                moves.size() < 2 ? null : convertCommonSetText(moves.get(1), SET_LIST_JOIN),
                moves.size() < 3 ? null : convertCommonSetText(set.moves().get(2), SET_LIST_JOIN),
                moves.size() < 4 ? null : convertCommonSetText(set.moves().get(3), SET_LIST_JOIN)
        );
    }

    private String convertEvsSetText(Object evs) {
        if (evs instanceof Map<?, ?>) {
            Map<String, Integer> evsMap = (Map<String, Integer>) evs;
            return convertEvsSetTextByMap(evsMap);
        } else if (evs instanceof List<?> list) {
            List<String> evsSetTextList = new ArrayList<>(list.size());
            for (Object o : list) {
                Map<String, Integer> evsMap = (Map<String, Integer>) o;
                evsSetTextList.add(convertEvsSetTextByMap(evsMap));
            }
            return String.join(EVS_SET_DELIMITER, evsSetTextList);
        } else if (evs == null) {
            log.warn("evs is null");
            return null;
        }
        throw new IllegalArgumentException("unknown evs type: " + evs);
    }

    private String convertEvsSetTextByMap(Map<String, Integer> evs) {
        List<String> evsItemTexts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : evs.entrySet()) {
            String evsItem = entry.getKey();
            int evsValue = entry.getValue();
            evsItemTexts.add(String.format(" %d %s ", evsValue, evsItem.toLowerCase(Locale.ROOT)));
        }
        return String.join(EVS_DELIMITER, evsItemTexts).strip();
    }

    private String convertCommonSetText(Object set, String join) {
        if (set instanceof String s) {
            return s;
        } else if (set instanceof List<?>) {
            List<String> setList = (List<String>) set;
            return String.join(join, setList);
        } else if (set == null) {
            log.warn("set type is null");
            return null;
        }
        throw new IllegalArgumentException("unknown set type: " + set);
    }

    private String initSetQuery(String format) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(SMOGON_SET_BASE_URL);
        uriBuilder.appendPath(String.format(PATTERN_FORMAT_TXT, format));
        return uriBuilder.build().toString();
    }
}