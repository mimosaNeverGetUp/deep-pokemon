/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.info;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemInfoProviderTest {

    @Autowired
    ItemInfoProvider itemInfoProvider;

    @Test
    void getItemInfo() {
        ItemInfo itemInfo = itemInfoProvider.getItemInfo("wateriumz");
        assertNotNull(itemInfo);
        assertEquals("Waterium Z", itemInfo.name());
    }
}