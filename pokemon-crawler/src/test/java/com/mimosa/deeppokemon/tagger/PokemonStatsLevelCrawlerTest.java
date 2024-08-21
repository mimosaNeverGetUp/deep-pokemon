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

package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.crawler.PokemonInfoCrawler;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class PokemonStatsLevelCrawlerTest {

    @Autowired
    private PokemonStatsLevelCrawler pokemonStatsLevelCrawler;

    @Autowired
    private PokemonInfoCrawler pokemonInfoCrawler;

    @Test
    void tag() {
        assertLevel("Kingambit", 4.5F, 1, 3, 4);
        assertLevel("Great Tusk", 4, 1, 1.0F, 4.5F);
        assertLevel("Gholdengo", 1, 4, 2.5F, 3F);
        assertLevel("Slowking-Galar" , 1, 3, 3.5F, 2.5F);
    }

    private void assertLevel(String name, float atk, float spa, float spd, float def) {
        PokemonInfo pokemonInfo = pokemonInfoCrawler.getPokemonInfo(name);
        Assertions.assertEquals(atk, pokemonStatsLevelCrawler.getAtkLevel(pokemonInfo));
        Assertions.assertEquals(spa, pokemonStatsLevelCrawler.getSatkLevel(pokemonInfo));
        Assertions.assertEquals(spd, pokemonStatsLevelCrawler.getSpdLevel(pokemonInfo));
        Assertions.assertEquals(def, pokemonStatsLevelCrawler.getDefLevel(pokemonInfo));
    }
}