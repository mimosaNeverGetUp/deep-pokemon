/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.pokemon.portal.config.MongodbTestConfig;
import com.mimosa.pokemon.portal.matcher.TeamMatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.everyItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
@AutoConfigureMockMvc
class TeamApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @CsvSource(value = {
            "Blissey::0:15",
            "Blissey,Clefable::0:15",
            ":ATTACK:0:15",
            ":ATTACK,UNPOPULAR:0:15"},
            delimiterString = ":"
    )
    void searchTeams_withValidParams(String pokemonNames, String tags, String page, String row) throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/api/teams")
                        .queryParam("pokemons", pokemonNames)
                        .queryParam("tags", tags)
                        .queryParam("page", page)
                        .queryParam("row", row))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.row").value(row))
                .andExpect(jsonPath("$.data[*].team").exists())
                .andExpect(jsonPath("$.data[*].team", everyItem(TeamMatcher.isValidTeam())));

        if (pokemonNames != null) {
            resultActions.andExpect(jsonPath("$.data[*].team",
                    everyItem(TeamMatcher.hasPokemons(pokemonNames.split(",")))));
        }


        if (tags != null) {
            resultActions.andExpect(jsonPath("$.data[*].team",
                    everyItem(TeamMatcher.hasTags(tags.split(",")))));
        }
    }

    @ParameterizedTest()
    @CsvSource(value = {
            "Blissey::-1:15",
            ":ATTACK:0:200"},
            delimiterString = ":")
    void searchTeams_withInvalidParams(String pokemonNames, String tags, String page, String row) throws Exception {
        mockMvc.perform(get("/api/teams")
                        .queryParam("pokemons", pokemonNames)
                        .queryParam("tags", tags)
                        .queryParam("page", page)
                        .queryParam("row", row))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }
}