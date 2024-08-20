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
import com.mimosa.deeppokemon.entity.PokemonBuildSet;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
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
            pokemonAttackDefenseTagProvider.tag(pokemonInfo, null);
            Assertions.assertEquals(1, pokemonInfo.getTags().size());
        }
    }

    @Test
    void tagHighUsage() {
        assertTag("Kingambit", Tag.BALANCE_ATTACK, null);
        assertTag("Great Tusk", Tag.BALANCE_STAFF, null);
        assertTag("Gholdengo", Tag.BALANCE_ATTACK, null);
        assertTag("Zamazenta", Tag.BALANCE_ATTACK, null);
        assertTag("Landorus-Therian", Tag.BALANCE_ATTACK, null);
        assertTag("Dragapult", Tag.BALANCE_ATTACK, null);
        assertTag("Raging Bolt", Tag.BALANCE_ATTACK, null);
        assertTag("Iron Valiant", Tag.BALANCE_ATTACK, null);
        assertTag("Iron Moth", Tag.BALANCE_ATTACK, null);
        assertTag("Darkrai", Tag.ATTACK, null);
        assertTag("Slowking-Galar", Tag.BALANCE_STAFF, null);
        assertTag("Dragonite", Tag.BALANCE, null);
        assertTag("Ogerpon-Wellspring", Tag.BALANCE_ATTACK, null);
        assertTag("Samurott-Hisui", Tag.BALANCE_ATTACK, null);

        assertTag("Kingambit", Tag.BALANCE_ATTACK, buildSet("Kingambit", "Black Glasses", "Swords Dance"));
        assertTag("Great Tusk", Tag.BALANCE_ATTACK, buildSet("Great Tusk", "Booster Energy", "Bulk Up"));
        assertTag("Great Tusk", Tag.BALANCE, buildSet("Great Tusk", "Booster Energy", ""));
        assertTag("Great Tusk", Tag.BALANCE_STAFF, buildSet("Great Tusk", "Heavy-Duty Boots", ""));
        assertTag("Iron Valiant", Tag.ATTACK, buildSet("Iron Valiant", "Booster Energy", ""));
        assertTag("Iron Valiant", Tag.ATTACK, buildSet("Iron Valiant", "Choice Specs", ""));
        assertTag("Iron Moth", Tag.ATTACK, buildSet("Iron Moth", "Booster Energy", ""));
        assertTag("Gholdengo", Tag.BALANCE, buildSet("Gold Glasses", "Heavy-Duty Boots", "Recover"));
        assertTag("Raging Bolt", Tag.ATTACK, buildSet("Raging Bolt", "Booster Energy", ""));
        assertTag("Raging Bolt", Tag.BALANCE_ATTACK, buildSet("Raging Bolt", "Leftovers", "Calm Mind"));
        assertTag("Gouging Fire", Tag.BALANCE_ATTACK, buildSet("Gouging Fire", "Booster Energy", "Dragon Dance"));
        assertTag("Gouging Fire", Tag.BALANCE, buildSet("Gouging Fire", "Booster Energy", "Dragon Dance", "Morning Sun"));
        assertTag("Roaring Moon", Tag.ATTACK, buildSet("Roaring Moon", "Booster Energy", "Dragon Dance"));
        assertTag("Roaring Moon", Tag.BALANCE_ATTACK, buildSet("Roaring Moon", "", "Dragon Dance", "Roost"));
        assertTag("Iron Treads", Tag.BALANCE_STAFF, buildSet("Iron Treads", "Booster Energy",""));
        assertTag("Hatterene", Tag.BALANCE_ATTACK, buildSet("Hatterene", "Leftovers",""));
        assertTag("Hatterene", Tag.ATTACK, buildSet("Hatterene", "Grassy Seed","Calm Mind"));
        assertTag("Iron Crown", Tag.BALANCE_ATTACK, buildSet("Iron Crown", "Booster Energy", "Calm Mind"));
        assertTag("Iron Crown", Tag.BALANCE_STAFF, buildSet("Iron Crown", "Assault Vest", ""));
        assertTag("Dragapult", Tag.BALANCE_ATTACK, buildSet("Dragapult", "Heavy-Duty Boots", ""));
        assertTag("Dragapult", Tag.ATTACK, buildSet("Dragapult", "Heavy-Duty Boots", "Dragon Dance"));
        assertTag("Dragapult", Tag.ATTACK, buildSet("Dragapult", "Choice Specs", ""));
    }

    public void assertTag(String name, Tag tag, PokemonBuildSet pokemonBuildSet) {
        PokemonInfo pokemonInfo = pokemonInfoCrawler.getPokemonInfo(name);
        pokemonAttackDefenseTagProvider.tag(pokemonInfo, pokemonBuildSet);
        Assertions.assertEquals(1, pokemonInfo.getTags().size());
        Assertions.assertTrue(pokemonInfo.getTags().contains(tag));
    }

    public PokemonBuildSet buildSet(String name, String item, String... moves) {
        return new PokemonBuildSet(name, List.of(moves), null, Collections.singletonList(item), null);
    }
}