/*
 * The MIT License
 *
 * Copyright (c) [2022] [Xiaocong Huang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mimosa.deeppokemon.crawler;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.entity.*;
import com.mimosa.deeppokemon.matcher.BattleMatcher;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest
class BattleReplayExtractorTest {
    @Autowired
    private BattleReplayExtractor battleReplayExtractor;

    @Value("classpath:api/battleReplay.json")
    Resource battleReplay;

    @Value("classpath:api/battleReplay_magic_bounce.json")
    Resource battleReplayWithMagicBounce;

    @Value("classpath:api/battleReplay_drag.json")
    Resource testReplay;

    @Autowired
    private final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    void extraBattle() throws IOException {
        BattleReplayData battleReplayData =
                OBJECT_MAPPER.readValue(battleReplay.getContentAsString(StandardCharsets.UTF_8), BattleReplayData.class);
        Battle battle = battleReplayExtractor.extract(battleReplayData);

        MatcherAssert.assertThat(battle, BattleMatcher.BATTLE_MATCHER);
        for (BattleTeam team : battle.getBattleTeams()) {
            Assertions.assertNotEquals(0F, team.getRating());
        }
    }

    @Test
    void extractDragPokemonMove() throws IOException {
        BattleReplayData battleReplayData =
                OBJECT_MAPPER.readValue(testReplay.getContentAsString(StandardCharsets.UTF_8), BattleReplayData.class);
        Battle battle = battleReplayExtractor.extract(battleReplayData);

        MatcherAssert.assertThat(battle, BattleMatcher.BATTLE_MATCHER);
        boolean hasIronCrown = false;
        for (var pokemon : battle.getBattleTeams().get(1).getPokemons()) {
            if (pokemon.getName().equals("Iron Crown")) {
                hasIronCrown = true;
                Assertions.assertFalse(pokemon.getMoves().isEmpty());
            }
        }
        Assertions.assertTrue(hasIronCrown);
    }


    @Test
    void extraBattleWithMagicBounce() throws IOException {
        BattleReplayData battleReplayData =
                OBJECT_MAPPER.readValue(battleReplayWithMagicBounce.getContentAsString(StandardCharsets.UTF_8), BattleReplayData.class);
        Battle battle = battleReplayExtractor.extract(battleReplayData);

        MatcherAssert.assertThat(battle, BattleMatcher.BATTLE_MATCHER);
        for (BattleTeam team : battle.getBattleTeams()) {
            Assertions.assertNotEquals(0F, team.getRating());
            for(Pokemon pokemon : team.getPokemons()) {
                if (StringUtils.equals(pokemon.getName(), "Hatterene")) {
                    Assertions.assertFalse(pokemon.getMoves().contains("Stealth Rock"));
                }
            }
        }
    }
}