/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.pokemon.portal.config.MongodbTestConfig;
import com.mimosa.pokemon.portal.matcher.PlayerMatcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
@AutoConfigureMockMvc
class PlayerApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDataUpdateDate() throws Exception {
        mockMvc.perform(get("/api/rank/update-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("date").exists())
                .andDo(print())
                .andReturn();
    }

    @Test
    void rankList() throws Exception {
        mockMvc.perform(get("/api/rank")
                        .queryParam("page", "1")
                        .queryParam("row", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.row").value(15))
                .andExpect(jsonPath("$.data", Matchers.everyItem(PlayerMatcher.isValidPlayer())))
                .andDo(print());
    }

    @Test
    void getPlayerRank() throws Exception {
        mockMvc.perform(get(String.format("/api/player/%s", "Stads")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", PlayerMatcher.isValidPlayer()))
                .andDo(print());
    }

    @Test
    void getPlayerBattleRecord() throws Exception {
        mockMvc.perform(get(String.format("/api/player/%s", "Stads")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", PlayerMatcher.isValidPlayer()))
                .andDo(print());
    }
}