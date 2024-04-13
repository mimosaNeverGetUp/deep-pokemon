/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Tag;
import com.mimosa.pokemon.portal.config.MongodbTestConfig;
import com.mimosa.pokemon.portal.dto.BattleTeamDto;
import com.mimosa.pokemon.portal.entity.PageResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
@AutoConfigureMockMvc
class TeamApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public static Stream<Arguments> queryTeamParam() {
        return Stream.of(
                Arguments.of(Collections.singletonList("Blissey"), Collections.emptyList(), 0, 15),
                Arguments.of(Collections.emptyList(), Collections.singletonList("ATTACK"), 0, 15)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Blissey::0:15",
            ":ATTACK:0:15"},
            delimiterString = ":"
    )
    void searchTeams_withValidParams(String pokemonNames, String tags, String page, String row) throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/api/teams")
                        .queryParam("pokemons", pokemonNames)
                        .queryParam("tags", tags)
                        .queryParam("page", page)
                        .queryParam("row", row))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        var typeRef = new TypeReference<PageResponse<BattleTeamDto>>() {
        };
        PageResponse<BattleTeamDto> pageResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                typeRef);

        // data not null
        Assertions.assertAll(
                () -> Assertions.assertNotNull(pageResponse.data()),
                () -> Assertions.assertFalse(pageResponse.data().isEmpty()),
                () -> Assertions.assertTrue(pageResponse.totalRecords() > 0),
                () -> Assertions.assertEquals(0, pageResponse.page()),
                () -> Assertions.assertEquals(15, pageResponse.row())
        );

        // team match search condition
        Set<String> exceptPokemonNames = pokemonNames == null ? new HashSet<>() :
                Arrays.stream(pokemonNames.split(",")).collect(Collectors.toSet());
        Set<String> exceptTags = tags == null ? new HashSet<>() :
                Arrays.stream(tags.split(",")).collect(Collectors.toSet());

        for (BattleTeamDto battleTeam : pageResponse.data()) {
            Set<String> teamPokemonNames = battleTeam.team().getPokemons().stream().map(Pokemon::getName)
                    .collect(Collectors.toSet());
            Assertions.assertTrue(teamPokemonNames.containsAll(exceptPokemonNames),
                    String.format("%s not contain pokemon %s", teamPokemonNames, exceptPokemonNames));


            Set<String> teamTags = battleTeam.team().getTagSet().stream().map(Tag::getName)
                    .collect(Collectors.toSet());
            Assertions.assertTrue(teamTags.containsAll(exceptTags),
                    String.format("%s not contain tag %s", teamTags, exceptTags));
        }
    }

    @ParameterizedTest()
    @CsvSource(value = {
            "Blissey::-1:15",
            ":ATTACK:0:200"},
            delimiterString = ":")
    void searchTeams_withInValidParams(String pokemonNames, String tags, String page, String row) throws Exception {
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