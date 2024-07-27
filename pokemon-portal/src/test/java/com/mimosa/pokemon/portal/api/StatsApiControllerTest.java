/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.pokemon.portal.config.MongodbTestConfig;
import com.mimosa.pokemon.portal.service.StatsService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
@AutoConfigureMockMvc
class StatsApiControllerTest {
    @SpyBean
    StatsService statsService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void usages() throws Exception {
        Mockito.doReturn("202406gen9ou").when(statsService).getLatestStatId("gen9ou");
        mockMvc.perform(get("/api/stats/gen9ou/usage")
                        .queryParam("page", "0")
                        .queryParam("row", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords", Matchers.not(0)))
                .andDo(print())
                .andReturn();
    }

    @Test
    void meta() throws Exception {
        Mockito.doReturn("202406gen9ou").when(statsService).getLatestStatId("gen9ou");
        mockMvc.perform(get("/api/stats/gen9ou/meta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", Matchers.not(0)))
                .andExpect(jsonPath("$.tags").isMap())
                .andExpect(jsonPath("$.tags", Matchers.not(Matchers.anEmptyMap())))
                .andDo(print())
                .andReturn();
    }

    @Test
    void moveset() throws Exception {
        Mockito.doReturn("202406gen9ou").when(statsService).getLatestStatId("gen9ou");
        mockMvc.perform(get("/api/stats/gen9ou/moveset/Kingambit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.abilities").isMap())
                .andExpect(jsonPath("$.abilities", Matchers.not(Matchers.anEmptyMap())))
                .andExpect(jsonPath("$.items").isMap())
                .andExpect(jsonPath("$.items", Matchers.not(Matchers.anEmptyMap())))
                .andExpect(jsonPath("$.spreads").isMap())
                .andExpect(jsonPath("$.spreads", Matchers.not(Matchers.anEmptyMap())))
                .andExpect(jsonPath("$.moves", Matchers.not(Matchers.anEmptyMap())))
                .andExpect(jsonPath("$.moves", Matchers.not(Matchers.anEmptyMap())))
                .andExpect(jsonPath("$.teammates", Matchers.not(Matchers.anEmptyMap())))
                .andExpect(jsonPath("$.teammates", Matchers.not(Matchers.anEmptyMap())))
                .andDo(print())
                .andReturn();
    }

    @Test
    void set() throws Exception {
        Mockito.doReturn("202406gen9ou").when(statsService).getLatestStatId("gen9ou");
        mockMvc.perform(get("/api/stats/gen9ou/set/Kingambit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sets").isMap())
                .andExpect(jsonPath("$.sets", Matchers.not(Matchers.anEmptyMap())))
                .andExpect(jsonPath("$.sets", Matchers.hasEntry(
                        Matchers.notNullValue(), Matchers.notNullValue()
                )))
                .andReturn();
    }
}