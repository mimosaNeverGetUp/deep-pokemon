/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.pokemon.portal.config.MongodbTestConfig;
import com.mimosa.pokemon.portal.matcher.TeamMatcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.everyItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
@AutoConfigureMockMvc
class TeamApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @CsvSource(value = {
            "Kingambit::0:7",
            "Zamazenta::0:7",
            "Kingambit,Slowking-Galar::0:7",
            ":ATTACK:0:7",
            ":STAFF,BALANCE_STAFF:0:7"},
            delimiterString = ":"
    )
    void searchTeams_withValidParams(String pokemonNames, String tags, String page, String row) throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/api/v2/teams")
                        .queryParam("pokemons", pokemonNames)
                        .queryParam("tags", tags)
                        .queryParam("page", page)
                        .queryParam("row", row))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.row").value(row))
                .andExpect(jsonPath("$.totalRecords", Matchers.not(0)))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data", Matchers.everyItem(Matchers.allOf(
                        Matchers.hasEntry(Matchers.equalTo("teamSet"), Matchers.notNullValue()),
                        Matchers.hasEntry(Matchers.equalTo("latestBattleDate"), Matchers.notNullValue()),
                        Matchers.hasEntry(Matchers.equalTo("maxRating"), Matchers.not(0)),
                        Matchers.hasEntry(Matchers.equalTo("uniquePlayerNum"), Matchers.not(0)),
                        Matchers.hasEntry(Matchers.equalTo("pokemons"), Matchers.notNullValue()),
                        Matchers.hasEntry(Matchers.equalTo("teams"), Matchers.notNullValue())
                ))));
        if (pokemonNames != null) {
            resultActions.andExpect(jsonPath("$.data",
                    everyItem(TeamMatcher.hasPokemons(pokemonNames.split(",")))));
        }


        if (tags != null) {
            resultActions.andExpect(jsonPath("$.data",
                    everyItem(TeamMatcher.hasTags(tags.split(",")))));
        }
    }

    @ParameterizedTest()
    @CsvSource(value = {
            "Blissey::-1:15",
            ":ATTACK:0:200"},
            delimiterString = ":")
    void searchTeams_withInvalidParams(String pokemonNames, String tags, String page, String row) throws Exception {
        mockMvc.perform(get("/api/v2/teams")
                        .queryParam("pokemons", pokemonNames)
                        .queryParam("tags", tags)
                        .queryParam("page", page)
                        .queryParam("row", row))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }

    @Test
    void searchTourTeam() throws Exception {
        mockMvc.perform(get("/api/v2/teams")
                        .queryParam("page", "0")
                        .queryParam("row", "7")
                        .queryParam("groupName", "tour_wcop_2024"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.row").value(7))
                .andExpect(jsonPath("$.totalRecords", Matchers.not(0)))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data", Matchers.everyItem(Matchers.allOf(
                        Matchers.hasEntry(Matchers.equalTo("maxPlayerWinRate"), Matchers.notNullValue()),
                        Matchers.hasEntry(Matchers.equalTo("maxPlayerWinDif"), Matchers.notNullValue()),
                        Matchers.hasEntry(Matchers.equalTo("uniquePlayerNum"), Matchers.not(0)),
                        Matchers.hasEntry(Matchers.equalTo("pokemons"), Matchers.notNullValue()),
                        Matchers.hasEntry(Matchers.equalTo("teams"), Matchers.notNullValue()
                        )))));
    }
}