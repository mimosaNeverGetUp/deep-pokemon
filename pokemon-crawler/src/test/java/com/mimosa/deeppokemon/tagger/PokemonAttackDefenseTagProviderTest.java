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
import java.util.Set;

@SpringBootTest
class PokemonAttackDefenseTagProviderTest {
    @Autowired
    private PokemonInfoCrawler pokemonInfoCrawler;

    @Autowired
    private PokemonAttackDefenseTagProvider pokemonAttackDefenseTagProvider;

    @Test
    void tag() throws Exception {
        Set<Tag> tagSet = Set.of(Tag.ATTACK_SET, Tag.DEFENSE_SET, Tag.DEFENSE_MIX_SET, Tag.DEFENSE_BULK_SET,
                Tag.ATTACK_MIX_SET, Tag.ATTACK_BULK_SET, Tag.BALANCE_SET, Tag.BALANCE_BULK_SET);
        List<PokemonInfo> pokemonInfoList = pokemonInfoCrawler.craw();
        for (PokemonInfo pokemonInfo : pokemonInfoList) {
            pokemonAttackDefenseTagProvider.tag(pokemonInfo, null);
            Assertions.assertEquals(1, pokemonInfo.getTags().size());
            Assertions.assertTrue(tagSet.contains(pokemonInfo.getTags().stream().findFirst().orElseThrow()));
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
        assertTag("Kyurem", Tag.ATTACK_MIX_SET, null);
        assertTag("Gliscor", Tag.DEFENSE_SET, null);
        assertTag("Gouging Fire", Tag.DEFENSE_MIX_SET, null);
        assertTag("Dragonite", Tag.ATTACK_MIX_SET, null);
        assertTag("Darkrai", Tag.ATTACK_SET, null);
        assertTag("Glimmora", Tag.ATTACK_SET, null);
        assertTag("Primarina", Tag.ATTACK_SET, null);
        assertTag("Hatterene", Tag.ATTACK_SET, null);
        assertTag("Clefable", Tag.DEFENSE_SET, null);
        assertTag("Garchomp", Tag.ATTACK_MIX_SET, null);
        assertTag("Serperior", Tag.ATTACK_SET, null);
        assertTag("Azumarill", Tag.ATTACK_MIX_SET, null);
        assertTag("Rotom-Wash", Tag.DEFENSE_MIX_SET, null);
        assertTag("Manaphy", Tag.ATTACK_SET, null);

        assertTag("Kingambit", Tag.ATTACK_BULK_SET, buildSet("Kingambit", "Black Glasses", "Swords Dance"));
        assertTag("Landorus-Therian", Tag.DEFENSE_MIX_SET, buildSet("Landorus-Therian", "Rocky Helmet", ""));
        assertTag("Landorus-Therian", Tag.DEFENSE_MIX_SET, buildSet("Landorus-Therian", "Leftovers", ""));
        assertTag("Landorus-Therian", Tag.DEFENSE_MIX_SET, buildSet("Landorus-Therian", "", "Earth Power"));
        assertTag("Great Tusk", Tag.ATTACK_BULK_SET, buildSet("Great Tusk", "Booster Energy", "Bulk Up"));
        assertTag("Great Tusk", Tag.ATTACK_BULK_SET, buildSet("Great Tusk", "Booster Energy", ""));
        assertTag("Great Tusk", Tag.DEFENSE_MIX_SET, buildSet("Great Tusk", "Heavy-Duty Boots", ""));
        assertTag("Iron Valiant", Tag.ATTACK_SET, buildSet("Iron Valiant", "Booster Energy", ""));
        assertTag("Iron Valiant", Tag.ATTACK_SET, buildSet("Iron Valiant", "Choice Specs", ""));
        assertTag("Iron Moth", Tag.ATTACK_SET, buildSet("Iron Moth", "Booster Energy", ""));
        assertTag("Gholdengo", Tag.BALANCE_BULK_SET, buildSet("Gold Glasses", "Heavy-Duty Boots", "Recover"));
        assertTag("Gholdengo", Tag.ATTACK_BULK_SET, buildSet("Gold Glasses", "Heavy-Duty Boots", "Recover", "Nasty Plot"));
        assertTag("Gholdengo", Tag.BALANCE_BULK_SET, buildSet("Gold Glasses", "Heavy-Duty Boots", "Recover", "Thunder Wave"));
        assertTag("Gholdengo", Tag.ATTACK_MIX_SET, buildSet("Gold Glasses", "Choice Scarf", ""));
        assertTag("Raging Bolt", Tag.ATTACK_MIX_SET, buildSet("Raging Bolt", "Booster Energy", ""));
        assertTag("Raging Bolt", Tag.ATTACK_MIX_SET, buildSet("Raging Bolt", "Leftovers", "Calm Mind"));
        assertTag("Zamazenta", Tag.BALANCE_BULK_SET, buildSet("Zamazenta", "Leftovers", "Iron Defense", "Body Press"));
        assertTag("Zamazenta", Tag.ATTACK_BULK_SET, buildSet("Zamazenta", "Heavy-Duty Boots", "Close Combat"));
        assertTag("Gouging Fire", Tag.ATTACK_MIX_SET, buildSet("Gouging Fire", "Booster Energy", "Dragon Dance"));
        assertTag("Gouging Fire", Tag.ATTACK_BULK_SET, buildSet("Gouging Fire", "Booster Energy", "Dragon Dance", "Morning Sun"));
        assertTag("Roaring Moon", Tag.ATTACK_MIX_SET, buildSet("Roaring Moon", "Booster Energy", "Dragon Dance"));
        assertTag("Roaring Moon", Tag.ATTACK_BULK_SET, buildSet("Roaring Moon", "", "Dragon Dance", "Roost"));
        assertTag("Samurott-Hisui", Tag.ATTACK_MIX_SET, buildSet("Samurott-Hisui", "Assault Vest", ""));
        assertTag("Dragonite", Tag.DEFENSE_MIX_SET, buildSet("Dragonite", "Heavy-Duty Boots", "Roost"));
        assertTag("Dragonite", Tag.ATTACK_BULK_SET, buildSet("Dragonite", "Heavy-Duty Boots", "Roost", "Dragon Dance"));
        assertTag("Primarina", Tag.ATTACK_MIX_SET, buildSet("Primarina", "Assault Vest", ""));
        assertTag("Iron Treads", Tag.BALANCE_SET, buildSet("Iron Treads", "Booster Energy", ""));
        assertTag("Hatterene", Tag.ATTACK_MIX_SET, buildSet("Hatterene", "Leftovers", ""));
        assertTag("Hatterene", Tag.ATTACK_SET, buildSet("Hatterene", "Grassy Seed", "Calm Mind"));
        assertTag("Iron Crown", Tag.ATTACK_MIX_SET, buildSet("Iron Crown", "Booster Energy", "Calm Mind"));
        assertTag("Iron Crown", Tag.DEFENSE_MIX_SET, buildSet("Iron Crown", "Assault Vest", ""));
        assertTag("Dragapult", Tag.ATTACK_SET, buildSet("Dragapult", "Heavy-Duty Boots", ""));
        assertTag("Dragapult", Tag.ATTACK_SET, buildSet("Dragapult", "Heavy-Duty Boots", "Dragon Dance"));
        assertTag("Dragapult", Tag.ATTACK_SET, buildSet("Dragapult", "Choice Specs", ""));
        assertTag("Rotom-Wash", Tag.DEFENSE_MIX_SET, buildSet("Rotom-Wash", "Leftovers", "Will-O-Wisp"));
        assertTag("Rotom-Wash", Tag.DEFENSE_MIX_SET, buildSet("Rotom-Wash", "Rocky Helmet", ""));
        assertTag("Rotom-Wash", Tag.ATTACK_MIX_SET, buildSet("Rotom-Wash", "Choice Scarf", ""));
        assertTag("Moltres", Tag.DEFENSE_MIX_SET, buildSet("Moltres", "Heavy-Duty Boots", "Roost"));
        assertTag("Moltres", Tag.DEFENSE_MIX_SET, buildSet("Moltres", "Heavy-Duty Boots", "Roost", "Will-O-Wisp"));
        assertTag("Hatterene", Tag.ATTACK_MIX_SET, buildSet("Hatterene", "Leftovers", ""));
        assertTag("Hatterene", Tag.ATTACK_MIX_SET, buildSet("Hatterene", "Assault Vest", ""));
        assertTag("Hatterene", Tag.ATTACK_MIX_SET, buildSet("Hatterene", "Rocky Helmet", ""));
        assertTag("Hatterene", Tag.ATTACK_MIX_SET, buildSet("Hatterene", "Eject Button", ""));
        assertTag("Clefable", Tag.DEFENSE_MIX_SET, buildSet("Clefable", "Sticky Barb", ""));
        assertTag("Clefable", Tag.DEFENSE_MIX_SET, buildSet("Clefable", "Leftovers", "Calm Mind"));
        assertTag("Clefable", Tag.DEFENSE_SET, buildSet("Clefable", "Leftovers", "Moonlight"));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Rocky Helmet", ""));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Leftovers", ""));
        assertTag("Garchomp", Tag.ATTACK_MIX_SET, buildSet("Garchomp", "Leftovers", "Swords Dance"));
        assertTag("Garchomp", Tag.DEFENSE_MIX_SET, buildSet("Garchomp", "Leftovers", "Project"));
        assertTag("Serperior", Tag.ATTACK_SET, buildSet("Serperior", "Leftovers", ""));
        assertTag("Serperior", Tag.ATTACK_MIX_SET, buildSet("Serperior", "", "Synthesis"));
        assertTag("Manaphy", Tag.ATTACK_SET, buildSet("Manaphy", "Leftovers", "Tail Glow"));
        assertTag("Manaphy", Tag.ATTACK_MIX_SET, buildSet("Manaphy", "Leftovers", "Take Heart"));
        assertTag("Azumarill", Tag.ATTACK_MIX_SET, buildSet("Azumarill", "Leftovers", ""));
        assertTag("Azumarill", Tag.ATTACK_MIX_SET, buildSet("Azumarill", "Assault Vest", ""));
        assertTag("Azumarill", Tag.ATTACK_SET, buildSet("Azumarill", "Sitrus Berry", ""));
        assertTag("Azumarill", Tag.ATTACK_SET, buildSet("Azumarill", "Choice Band", ""));
        assertTag("Azumarill", Tag.ATTACK_SET, buildSet("Azumarill", "", "Belly Drum"));
        assertTag("Azumarill", Tag.DEFENSE_SET, buildSet("Azumarill", "", "Whirlpool"));
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