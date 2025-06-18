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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ItemInfoProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemInfoProvider.class);

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Value("classpath:pokemon/itemInfo.json")
    private Resource itemInfoData;

    private Map<String, ItemInfo> itemInfos = new HashMap<>();

    public ItemInfo getItemInfo(String move) {
        if (itemInfos.isEmpty()) {
            load();
        }
        return itemInfos.get(move);
    }

    private void load() {
        if (itemInfos.isEmpty()) {
            try {
                itemInfos = OBJECT_MAPPER.readValue(itemInfoData.getInputStream(),
                        new TypeReference<>() {});
            } catch (IOException e) {
                LOGGER.error("Failed to load itemInfos", e);
            }
        }
    }
}