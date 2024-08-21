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
        assertTag("Kingambit", Tag.ATTACK_BULK_SET, null);
        assertTag("Great Tusk", Tag.DEFENSE_MIX_SET, null);
        assertTag("Gholdengo", Tag.ATTACK_MIX_SET, null);
        assertTag("Zamazenta", Tag.DEFENSE_MIX_SET, null);
        assertTag("Landorus-Therian", Tag.ATTACK_MIX_SET, null);
        assertTag("Dragapult", Tag.ATTACK_SET, null);
        assertTag("Raging Bolt", Tag.ATTACK_MIX_SET, null);
        assertTag("Iron Valiant", Tag.ATTACK_SET, null);
        assertTag("Iron Moth", Tag.ATTACK_SET, null);
        assertTag("Darkrai", Tag.ATTACK_SET, null);
        assertTag("Slowking-Galar", Tag.DEFENSE_SET, null);
        assertTag("Dragonite", Tag.ATTACK_MIX_SET, null);
        assertTag("Ogerpon-Wellspring", Tag.ATTACK_SET, null);
        assertTag("Samurott-Hisui", Tag.ATTACK_SET, null);

        assertTag("Kingambit", Tag.ATTACK_BULK_SET, buildSet("Kingambit", "Black Glasses", "Swords Dance"));
        assertTag("Landorus-Therian", Tag.DEFENSE_MIX_SET, buildSet("Landorus-Therian", "Rocky Helmet", ""));
        assertTag("Landorus-Therian", Tag.DEFENSE_MIX_SET, buildSet("Landorus-Therian", "", "Earth Power"));
        assertTag("Great Tusk", Tag.ATTACK_BULK_SET, buildSet("Great Tusk", "Booster Energy", "Bulk Up"));
        assertTag("Great Tusk", Tag.ATTACK_BULK_SET, buildSet("Great Tusk", "Booster Energy", ""));
        assertTag("Great Tusk", Tag.DEFENSE_MIX_SET, buildSet("Great Tusk", "Heavy-Duty Boots", ""));
        assertTag("Iron Valiant", Tag.ATTACK_SET, buildSet("Iron Valiant", "Booster Energy", ""));
        assertTag("Iron Valiant", Tag.ATTACK_SET, buildSet("Iron Valiant", "Choice Specs", ""));
        assertTag("Iron Moth", Tag.ATTACK_SET, buildSet("Iron Moth", "Booster Energy", ""));
        assertTag("Gholdengo", Tag.BALANCE_BULK_SET, buildSet("Gold Glasses", "Heavy-Duty Boots", "Recover"));
        assertTag("Gholdengo", Tag.ATTACK_BULK_SET, buildSet("Gold Glasses", "Heavy-Duty Boots", "Recover", "Nasty Plot"));
        assertTag("Raging Bolt", Tag.ATTACK_MIX_SET, buildSet("Raging Bolt", "Booster Energy", ""));
        assertTag("Raging Bolt", Tag.ATTACK_MIX_SET, buildSet("Raging Bolt", "Leftovers", "Calm Mind"));
        assertTag("Gouging Fire", Tag.ATTACK_MIX_SET, buildSet("Gouging Fire", "Booster Energy", "Dragon Dance"));
        assertTag("Gouging Fire", Tag.ATTACK_BULK_SET, buildSet("Gouging Fire", "Booster Energy", "Dragon Dance", "Morning Sun"));
        assertTag("Roaring Moon", Tag.ATTACK_MIX_SET, buildSet("Roaring Moon", "Booster Energy", "Dragon Dance"));
        assertTag("Roaring Moon", Tag.ATTACK_BULK_SET, buildSet("Roaring Moon", "", "Dragon Dance", "Roost"));
        assertTag("Iron Treads", Tag.BALANCE_SET, buildSet("Iron Treads", "Booster Energy", ""));
        assertTag("Hatterene", Tag.ATTACK_SET, buildSet("Hatterene", "Leftovers", ""));
        assertTag("Hatterene", Tag.ATTACK_SET, buildSet("Hatterene", "Grassy Seed", "Calm Mind"));
        assertTag("Iron Crown", Tag.ATTACK_MIX_SET, buildSet("Iron Crown", "Booster Energy", "Calm Mind"));
        assertTag("Iron Crown", Tag.DEFENSE_MIX_SET, buildSet("Iron Crown", "Assault Vest", ""));
        assertTag("Dragapult", Tag.ATTACK_SET, buildSet("Dragapult", "Heavy-Duty Boots", ""));
        assertTag("Dragapult", Tag.ATTACK_SET, buildSet("Dragapult", "Heavy-Duty Boots", "Dragon Dance"));
        assertTag("Dragapult", Tag.ATTACK_SET, buildSet("Dragapult", "Choice Specs", ""));
        assertTag("Rotom-Wash", Tag.DEFENSE_SET, buildSet("Rotom-Wash", "Leftovers", "Will-O-Wisp"));
        assertTag("Moltres", Tag.DEFENSE_MIX_SET, buildSet("Moltres", "Heavy-Duty Boots", "Roost"));
        assertTag("Moltres", Tag.DEFENSE_MIX_SET, buildSet("Moltres", "Heavy-Duty Boots", "Roost", "Will-O-Wisp"));
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