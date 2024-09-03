/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.pokemon.portal.config.MongodbTestConfig;
import com.mimosa.pokemon.portal.matcher.PlayerMatcher;
import com.mimosa.pokemon.portal.matcher.TeamMatcher;
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
                .andExpect(jsonPath("$.totalRecords", Matchers.not(0)))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data", Matchers.everyItem(PlayerMatcher.isValidPlayer())))
                .andExpect(jsonPath("$.data", Matchers.hasItems(
                        Matchers.hasEntry(Matchers.equalTo("recentTeam"), Matchers.iterableWithSize(2)))))
                .andDo(print());
    }

    @Test
    void getPlayerRank() throws Exception {
        mockMvc.perform(get(String.format("/api/player/%s", "switchbladess")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", PlayerMatcher.isValidPlayer()))
                .andDo(print());
    }

    @Test
    void getPlayerBattleRecord() throws Exception {
        mockMvc.perform(get(String.format("/api/player/%s/battle", "switchbladess"))
                        .queryParam("page", "0")
                        .queryParam("row", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.row").value(15))
                .andExpect(jsonPath("$.totalRecords", Matchers.not(0)))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data",
                        Matchers.everyItem(Matchers.allOf(
                                Matchers.hasEntry(Matchers.equalTo("id"), Matchers.notNullValue()),
                                Matchers.hasEntry(Matchers.equalTo("date"), Matchers.notNullValue()),
                                Matchers.hasEntry(Matchers.equalTo("avageRating"), Matchers.notNullValue()),
                                Matchers.hasEntry(Matchers.equalTo("teams"), Matchers.iterableWithSize(2)),
                                Matchers.hasEntry(Matchers.equalTo("winner"), Matchers.notNullValue())
                        ))))
                .andExpect(jsonPath("$.data",
                        Matchers.everyItem(Matchers.allOf(
                                Matchers.hasEntry(Matchers.equalTo("teams"), Matchers.everyItem(TeamMatcher.isValidTeam())
                        )))))
                .andDo(print());

    }

    @Test
    void getTourPlayerBattleRecord() throws Exception {
        mockMvc.perform(get(String.format("/api/tour/player/%s/battle", "srn"))
                        .queryParam("page", "0")
                        .queryParam("row", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.row").value(15))
                .andExpect(jsonPath("$.totalRecords", Matchers.not(0)))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data",
                        Matchers.everyItem(Matchers.allOf(
                                Matchers.hasEntry(Matchers.equalTo("id"), Matchers.notNullValue()),
                                Matchers.hasEntry(Matchers.equalTo("date"), Matchers.notNullValue()),
                                Matchers.hasEntry(Matchers.equalTo("avageRating"), Matchers.notNullValue()),
                                Matchers.hasEntry(Matchers.equalTo("tourId"), Matchers.notNullValue()),
                                Matchers.hasEntry(Matchers.equalTo("stage"), Matchers.notNullValue()),
                                Matchers.hasEntry(Matchers.equalTo("winSmogonPlayerName"), Matchers.notNullValue()),
                                Matchers.hasEntry(Matchers.equalTo("teams"), Matchers.iterableWithSize(2)),
                                Matchers.hasEntry(Matchers.equalTo("winner"), Matchers.notNullValue())
                        ))))
                .andExpect(jsonPath("$.data",
                        Matchers.everyItem(Matchers.allOf(
                                Matchers.hasEntry(Matchers.equalTo("teams"), Matchers.everyItem(TeamMatcher.isValidTeam())
                                )))))
                .andDo(print());

    }
}