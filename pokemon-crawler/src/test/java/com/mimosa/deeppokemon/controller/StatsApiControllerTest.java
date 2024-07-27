/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.service.StatsService;
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
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    StatsService statsService;

    @Test
    void craw_existData() throws Exception {
        Mockito.doReturn("202406gen9ou").when(statsService).getLatestStatId("gen9ou");
        mockMvc.perform(get("/api/stats/craw")
                        .queryParam("format", "gen9ou"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true))
                .andDo(print())
                .andReturn();
    }

}