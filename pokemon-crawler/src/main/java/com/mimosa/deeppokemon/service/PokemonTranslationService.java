/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PokemonTranslationService {
    private final Pattern PROPER_NOUNS_PATTERN = Pattern.compile("\\b([A-Z][a-z\\-]*\\s?)+\\b");

    private final Map<String, String> translationMaps;
    private final Map<String, String> shortNameMap;

    public PokemonTranslationService(@Value("classpath:pokemon/translation.json") Resource translationResource) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        translationMaps = objectMapper.readValue(translationResource.getContentAsString(StandardCharsets.UTF_8),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
        // put short name of multi form pokemon
         shortNameMap = new HashMap();
        for (String key : translationMaps.keySet()) {
            int index = key.indexOf("-");
            if (index != -1 && index + 2 <= key.length()) {
                String shortName = key.substring(0, index + 2);
                shortNameMap.put(shortName, translationMaps.get(key));

                String formName = key.substring(index + 1);
                if (StringUtils.equals(formName, "Galar")) {
                    shortName = String.format("%s %s", "Galarian", key.substring(0, index));
                    shortNameMap.put(shortName, translationMaps.get(key));
                } else if (StringUtils.equals(formName, "Hisui")) {
                    shortName = String.format("%s %s", "Hisuian", key.substring(0, index));
                    shortNameMap.put(shortName, translationMaps.get(key));
                } else if (StringUtils.equals(formName, "Alola")) {
                    shortName = String.format("%s %s", "Alolan", key.substring(0, index));
                    shortNameMap.put(shortName, translationMaps.get(key));
                }
            }
        }
    }

    public String getTranslation(String context) {
        if (translationMaps.get(context) != null) {
            return translationMaps.get(context);
        }else if(shortNameMap.containsKey(context)){
            return shortNameMap.get(context);
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