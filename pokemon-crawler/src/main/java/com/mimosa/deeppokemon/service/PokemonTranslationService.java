/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PokemonTranslationService {
    private final Pattern PROPER_NOUNS_PATTERN = Pattern.compile("\\b([A-Z][a-z\\-]*\\s?)+\\b");

    private final Map<String, String> translationMaps;

    public PokemonTranslationService(@Value("classpath:pokemon/translation.json") Resource translationResource) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        translationMaps = objectMapper.readValue(translationResource.getContentAsString(StandardCharsets.UTF_8),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
    }

    public String getTranslation(String context) {
        if (translationMaps.get(context) != null) {
            return translationMaps.get(context);
        }
        return context;
    }

    public String translateText(String text) {
        String output = String.valueOf(text);
        Matcher matcher = PROPER_NOUNS_PATTERN.matcher(text);
        while (matcher.find()) {
            String properNouns = matcher.group(0).trim();
            output = output.replace(properNouns, getTranslation(properNouns));
        }
        output = output.replace("Entry hazards", "进场障碍");
        output = output.replace("entry hazards", "进场障碍");
        output = output.replace("entry hazard ", "进场障碍 ");
        return output;
    }
}