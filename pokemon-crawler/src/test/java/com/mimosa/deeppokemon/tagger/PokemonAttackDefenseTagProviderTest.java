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
import com.mimosa.deeppokemon.entity.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PokemonAttackDefenseTagProviderTest {
    @Autowired
    private PokemonInfoCrawler pokemonInfoCrawler;

    @Autowired
    private PokemonAttackDefenseTagProvider pokemonAttackDefenseTagProvider;

    @Test
    void tag() throws Exception {
        List<PokemonInfo> pokemonInfoList = pokemonInfoCrawler.craw();
        for (PokemonInfo pokemonInfo : pokemonInfoList) {
            pokemonAttackDefenseTagProvider.tag(pokemonInfo);
            Assertions.assertEquals(1, pokemonInfo.getTags().size());
        }
    }

    @Test
    void tagHighUsage() {
        assertTag("Kingambit", Tag.BALANCE_ATTACK);
        assertTag("Great Tusk", Tag.BALANCE);
        assertTag("Gholdengo", Tag.BALANCE_ATTACK);
        assertTag("Zamazenta", Tag.BALANCE_ATTACK);
        assertTag("Landorus-Therian", Tag.BALANCE_ATTACK);
        assertTag("Dragapult", Tag.ATTACK);
        assertTag("Raging Bolt", Tag.BALANCE_ATTACK);
        assertTag("Iron Valiant", Tag.ATTACK);
        assertTag("Iron Moth", Tag.ATTACK);
        assertTag("Darkrai", Tag.ATTACK);
        assertTag("Slowking-Galar", Tag.BALANCE_STAFF);
        assertTag("Dragonite", Tag.BALANCE);
        assertTag("Ogerpon-Wellspring", Tag.BALANCE_ATTACK);
        assertTag("Samurott-Hisui", Tag.BALANCE_ATTACK);
    }

    public void assertTag(String name, Tag tag) {
        PokemonInfo pokemonInfo = pokemonInfoCrawler.getPokemonInfo(name);
        pokemonAttackDefenseTagProvider.tag(pokemonInfo);
        Assertions.assertEquals(1, pokemonInfo.getTags().size());
        Assertions.assertTrue(pokemonInfo.getTags().contains(tag));
    }
}