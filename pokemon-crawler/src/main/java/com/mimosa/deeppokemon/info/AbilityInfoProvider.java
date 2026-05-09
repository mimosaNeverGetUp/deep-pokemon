/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.info;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AbilityInfoProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbilityInfoProvider.class);

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    @Value("classpath:pokemon/abilityInfo.json")
    private Resource abilityInfoData;

    private final Map<String, AbilityInfo> abilityInfo = new HashMap<>();
    private final Map<String, AbilityInfo> shortNameMap = new HashMap<>();

    @RegisterReflectionForBinding(value = AbilityInfo.class)
    public AbilityInfo getAbilityInfo(String ability) {
        if (abilityInfo.isEmpty()) {
            load();
        }
        AbilityInfo abilityeInfo = abilityInfo.get(ability);
        if (abilityeInfo == null) {
            abilityeInfo = shortNameMap.get(ability);
        }
        return abilityeInfo;
    }

    private void load() {
        if (abilityInfo.isEmpty()) {
            try {
                Map<String, AbilityInfo> tmp = OBJECT_MAPPER.readValue(abilityInfoData.getInputStream(),
                        new TypeReference<>() {
                        });
                for (var entry : tmp.entrySet()) {
                    abilityInfo.put(entry.getValue().name(), entry.getValue());
                    shortNameMap.put(entry.getKey(), entry.getValue());
                }
            } catch (IOException e) {
                LOGGER.error("Failed to load abilityeInfo", e);
            }
        }
    }

    public String getName(String shortName) {
        AbilityInfo abilityeInfo = getAbilityInfo(shortName);
        return abilityeInfo == null ? null : abilityeInfo.name();
    }
}