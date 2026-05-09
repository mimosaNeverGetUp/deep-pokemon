/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon.info;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AbilityInfoProviderTest {

    @Autowired
    AbilityInfoProvider abilityInfoProvider;

    @Test
    void getAbilityInfo() {
        AbilityInfo abilityInfo = abilityInfoProvider.getAbilityInfo("protosynthesis");
        assertNotNull(abilityInfo);
        assertEquals("Protosynthesis", abilityInfo.name());
    }

    @Test
    void getName() {
        assertEquals("Protosynthesis", abilityInfoProvider.getName("protosynthesis"));
        assertNull(abilityInfoProvider.getName("test"));
    }
}