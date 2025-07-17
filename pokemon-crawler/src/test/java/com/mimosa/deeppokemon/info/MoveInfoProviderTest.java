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
class MoveInfoProviderTest {
    @Autowired
    MoveInfoProvider moveInfoProvider;

    @Test
    void getMove() {
        MoveInfo moveInfo = moveInfoProvider.getMoveInfo("Devastating Drake");
        assertNotNull(moveInfo);
        assertEquals("Devastating Drake", moveInfo.name());
        assertEquals(1, moveInfo.basePower());
        assertEquals("dragoniumz", moveInfo.isZ());
        assertEquals("Dragon", moveInfo.type());

        assertNull(moveInfoProvider.getMoveInfo("Absorb").isZ());
    }

    @Test
    void isZMove() {
        assertTrue(moveInfoProvider.isZMove("Devastating Drake"));
        assertTrue(moveInfoProvider.isZMove("Z-Haze"));
        assertFalse(moveInfoProvider.isZMove("Haze"));
        assertFalse(moveInfoProvider.isZMove("Absorb"));
    }

    @Test
    void getZMoveItem() {
        assertEquals("Dragonium Z",moveInfoProvider.getZMoveItem("Devastating Drake"));
        assertEquals("Icium Z", moveInfoProvider.getZMoveItem("Z-Haze"));
        assertNull(moveInfoProvider.getZMoveItem("Haze"));
    }
}