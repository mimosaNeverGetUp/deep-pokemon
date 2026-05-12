/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat;

import com.mimosa.deeppokemon.crawler.stat.dto.MonthlyBattleStatDto;
import com.mimosa.deeppokemon.crawler.stat.dto.MonthlyMetaGameStatDto;
import com.mimosa.deeppokemon.crawler.stat.dto.MonthlyPokemonStatDto;
import com.mimosa.deeppokemon.crawler.stat.dto.Usage;
import com.mimosa.deeppokemon.utils.HttpProxy;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmogonMonthlyStatCrawlerTest {
    @Autowired
    private SmogonMonthlyStatCrawler crawler;

    @SpyBean
    private HttpProxy httpProxy;

    @Value("classpath:api/stat/smogon/gen9ou-1695.json")
    Resource smogonMonthlyStatResource;

    @Value("classpath:api/stat/smogon/meta-gen9ou-1695.txt")
    Resource smogonMonthlyMetaStatResource;

    @Test
    void craw() throws IOException {
        String statResourceContent = smogonMonthlyStatResource.getContentAsString(StandardCharsets.UTF_8);
        String metaResourceContent = smogonMonthlyMetaStatResource.getContentAsString(StandardCharsets.UTF_8);
        Mockito.doReturn(statResourceContent, metaResourceContent).when(httpProxy).get(Mockito.any());
        MonthlyBattleStatDto monthlyBattleStatDto = crawler.craw("gen9ou", "202601");
        assertNotNull(monthlyBattleStatDto);
        assertEquals(1050903, monthlyBattleStatDto.battles());

        LocalDate date = monthlyBattleStatDto.date();
        assertNotNull(date);
        assertEquals(2026, date.getYear());
        assertEquals(1, date.getMonthValue());
        assertEquals(1, date.getDayOfMonth());

        MonthlyMetaGameStatDto metagame = monthlyBattleStatDto.metagame();
        assertNotNull(metagame);
        LinkedHashMap<String, Double> tags = metagame.tags();
        assertTrue(tags != null && !tags.isEmpty());
        assertTrue(tags.containsKey("offense"));
        assertTrue(tags.containsKey("balance"));
        assertTrue(tags.containsKey("hyperoffense"));
        assertTrue(tags.containsKey("sun"));
        assertTrue(tags.containsKey("stall"));
        assertTrue(tags.containsKey("rain"));
        assertTrue(tags.containsKey("trickroom"));
        for (var tag : tags.entrySet()) {
            assertNotNull(tag.getKey());
            assertNotEquals(0D, tag.getValue().doubleValue());
        }

        Map<String, MonthlyPokemonStatDto> pokemon = monthlyBattleStatDto.pokemon();
        assertTrue(pokemon != null && !pokemon.isEmpty());
        assertTrue(pokemon.containsKey("Great Tusk"));
        MonthlyPokemonStatDto greatTusk = pokemon.get("Great Tusk");
        assertNotNull(greatTusk);
        assertNull(greatTusk.lead());

        LinkedHashMap<String, Double> abilities = greatTusk.abilities();
        assertNotNull(abilities);
        assertEquals(1, abilities.size());
        assertTrue(abilities.containsKey("Protosynthesis"));
        assertEquals(1D, abilities.get("Protosynthesis"));

        LinkedHashMap<String, Double> moves = greatTusk.moves();
        assertNotNull(moves);
        assertTrue(moves.containsKey("Headlong Rush"));
        assertEquals(0.9079D, moves.get("Headlong Rush"));

        LinkedHashMap<String, Double> items = greatTusk.items();
        assertNotNull(items);
        assertTrue(items.containsKey("Heavy-Duty Boots"));
        assertEquals(0.3071D, items.get("Heavy-Duty Boots"));

        LinkedHashMap<String, Double> teraTypes = greatTusk.teraTypes();
        assertNotNull(teraTypes);
        assertTrue(teraTypes.containsKey("Steel"));
        assertEquals(0.2507D, teraTypes.get("Steel"));

        LinkedHashMap<String, Double> spreads = greatTusk.spreads();
        assertNotNull(spreads);
        assertTrue(spreads.containsKey("Jolly:0/252/4/0/0/252"));
        assertEquals(0.2695D, spreads.get("Jolly:0/252/4/0/0/252"));

        LinkedHashMap<String, Double> teammates = greatTusk.teammates();
        assertNotNull(teammates);
        assertTrue(teammates.containsKey("Kingambit"));
        assertEquals(0.2799D, teammates.get("Kingambit"));

        Usage usage = greatTusk.usage();
        assertNotNull(usage);
        assertEquals(0.3458D, usage.weighted());

        assertEquals(0D, greatTusk.weight());
        assertTrue(greatTusk.viability() != null && greatTusk.viability().isEmpty());
        assertTrue(greatTusk.counters() != null && greatTusk.counters().isEmpty());
        assertEquals(0D, greatTusk.weight());

        assertStat(pokemon);
    }

    private static void assertStat(Map<String, MonthlyPokemonStatDto> pokemon) {
        LinkedHashMap<String, Double> spreads;
        LinkedHashMap<String, Double> abilities;
        Usage usage;
        LinkedHashMap<String, Double> moves;
        LinkedHashMap<String, Double> items;
        LinkedHashMap<String, Double> teraTypes;
        LinkedHashMap<String, Double> teammates;
        for (var entry : pokemon.entrySet()) {
            assertNotNull(entry.getKey());
            MonthlyPokemonStatDto pokemonStatDto = entry.getValue();
            assertNotNull(pokemonStatDto);
            assertNull(pokemonStatDto.lead());
            assertTrue(pokemonStatDto.viability() != null && pokemonStatDto.viability().isEmpty());
            assertTrue(pokemonStatDto.counters() != null && pokemonStatDto.counters().isEmpty());

            abilities = pokemonStatDto.abilities();
            assertNotNull(abilities);
            assertFalse(abilities.isEmpty());
            for (var ability : abilities.values()) {
                assertTrue(ability > 0D);
            }

            moves = pokemonStatDto.moves();
            assertNotNull(moves);
            assertFalse(moves.isEmpty());
            for (var move : moves.values()) {
                assertTrue(move > 0D);
            }

            items = pokemonStatDto.items();
            assertNotNull(items);
            assertFalse(items.isEmpty());
            for (var item : items.values()) {
                assertTrue(item > 0D);
            }

            teraTypes = pokemonStatDto.teraTypes();
            assertNotNull(teraTypes);
            assertFalse(teraTypes.isEmpty());
            for (var teraType : teraTypes.values()) {
                assertTrue(teraType > 0D);
            }

            spreads = pokemonStatDto.spreads();
            assertNotNull(spreads);
            assertFalse(spreads.isEmpty());
            for (var spread : spreads.values()) {
                assertTrue(spread > 0D);
            }

            usage = pokemonStatDto.usage();
            assertNotNull(usage);
            assertNotEquals(0D, usage.weighted());
            assertEquals(0D, usage.raw());
            assertEquals(0D, usage.real());

            teammates = pokemonStatDto.teammates();
            assertNotNull(teammates);
            assertFalse(teammates.isEmpty());
            for (var teammate : teammates.values()) {
                assertTrue(teammate > 0D);
            }
        }
    }


    @ParameterizedTest
    @CsvSource(value = {"gen8ou", "gen1ou", "gen6ou", "gen7ou", "gen9uu", "gen9ubers", "gen9ru", "gen9lc",
            "gen9nationaldex", "gen9anythinggoes", "gen9doublesou"})
    void crawOtherformat(String format) throws IOException {
        int cutoff = 1630;
        if (StringUtils.equals(format, "gen9doublesou")) {
            cutoff = 1695;
        }

        String statResourceContent = new ClassPathResource(String.format("api/stat/smogon/%s-%d.json", format, cutoff))
                .getContentAsString(StandardCharsets.UTF_8);
        String metaResourceContent = new ClassPathResource(String.format("api/stat/smogon/%s-%d.txt", format, cutoff))
                .getContentAsString(StandardCharsets.UTF_8);

        Mockito.doReturn(statResourceContent, metaResourceContent).when(httpProxy).get(Mockito.any());
        MonthlyBattleStatDto monthlyBattleStatDto = crawler.craw(format, "202604");
        assertNotNull(monthlyBattleStatDto);
        assertNotEquals(0, monthlyBattleStatDto.battles());

        LocalDate date = monthlyBattleStatDto.date();
        assertNotNull(date);
        Map<String, MonthlyPokemonStatDto> pokemon = monthlyBattleStatDto.pokemon();
        assertTrue(pokemon != null && !pokemon.isEmpty());
        assertStat(pokemon);
    }
}