/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.tagger.rule;

import com.mimosa.deeppokemon.entity.PokemonBuildSet;
import com.mimosa.deeppokemon.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TagSettingProviderTest {

    @Autowired
    private TagSettingProvider tagSettingProvider;

    @Test
    void getTagSetting() {
        TagSetting tagSetting = tagSettingProvider.getTagSetting("gen9ou");
        assertNotNull(tagSetting);
        assertNotNull(tagSetting.bulkAtkThreshold());
        assertNotNull(tagSetting.goodAtkThreshold());
        assertNotNull(tagSetting.bulkDefThreshold());
        assertNotNull(tagSetting.goodDefThreshold());
        assertFalse(tagSetting.itemValue().isEmpty());
        for (ItemValue itemValue : tagSetting.itemValue()) {
            assertNotNull(itemValue.atkValue());
            assertNotNull(itemValue.defValue());
            assertFalse(itemValue.items().isEmpty());
        }
        assertFalse(tagSetting.moveValue().isEmpty());
        for (MoveValue moveValue : tagSetting.moveValue()) {
            assertNotNull(moveValue.atkValue());
            assertNotNull(moveValue.defValue());
            assertNotNull(moveValue.isExtraValue());
            assertFalse(moveValue.moves().isEmpty());
        }

        assertFalse(tagSetting.abilityValue().isEmpty());
        for (AbilityValue abilityValue : tagSetting.abilityValue()) {
            assertNotNull(abilityValue.atkValue());
            assertNotNull(abilityValue.defValue());
            assertFalse(abilityValue.abilities().isEmpty());
        }

        assertFalse(tagSetting.specifyPokemonRule().isEmpty());
        for (SpecifyPokemonRule specifyPokemonRule : tagSetting.specifyPokemonRule()) {
            assertFalse(specifyPokemonRule.names().isEmpty());
            assertFalse(specifyPokemonRule.rules().isEmpty());
            for (TagRule rule : specifyPokemonRule.rules()) {
                assertNotNull(rule.tag());
                assertNotNull(rule.condition());
            }
        }
    }

    @Test
    void getItemValue() {
        TagSetting tagSetting = tagSettingProvider.getTagSetting("gen9ou");
        ItemValue choiceBand = tagSettingProvider.getItemValue(tagSetting, "Choice Band");
        assertNotNull(choiceBand);
        assertEquals(1.0D, choiceBand.atkValue());
        assertEquals(0.0D, choiceBand.defValue());
    }

    @Test
    void getAbilityValue() {
        TagSetting tagSetting = tagSettingProvider.getTagSetting("gen9ou");
        AbilityValue regenerator = tagSettingProvider.getAbilityValue(tagSetting, "Good as Gold");
        assertNotNull(regenerator);
        assertEquals(0.25D, regenerator.defValue());
        assertEquals(0.5D, regenerator.atkValue());
    }

    @Test
    void getMoveAtkValue() {
        TagSetting tagSetting = tagSettingProvider.getTagSetting("gen9ou");
        double moveAtkValue = tagSettingProvider.getMoveAtkValue(tagSetting, Set.of("Taunt", "Bulk Up", "Slack Off", "Thunder Wave", "Blast Burn"));
        assertEquals(1.0D, moveAtkValue);
    }

    @Test
    void getMoveDefValue() {
        TagSetting tagSetting = tagSettingProvider.getTagSetting("gen9ou");
        double moveDefValue = tagSettingProvider.getMoveDefValue(tagSetting, Set.of("Taunt", "Bulk Up", "Slack Off",
                "Thunder Wave", "Blast Burn"));
        assertEquals(0.5D, moveDefValue);
    }

    @Test
    void getPokemonTags() {
        TagSetting tagSetting = tagSettingProvider.getTagSetting("gen9ou");
        assertSpecifyTagEquals(null, tagSetting, buildSet("Kingambit", "Black Glasses", "Swords Dance"));
        assertSpecifyTagEquals(Tag.DEFENSE_MIX_SET, tagSetting, buildSet("Landorus-Therian", "Rocky Helmet", ""));
        assertSpecifyTagEquals(Tag.DEFENSE_MIX_SET, tagSetting, buildSet("Landorus-Therian", "", "Earth Power"));
        assertSpecifyTagEquals(null, tagSetting, buildSet("Landorus-Therian", "", "Earthquake"));
        assertSpecifyTagEquals(Tag.DEFENSE_MIX_SET, tagSetting, buildSet("Clefable", "Sticky Barb", ""));
        assertSpecifyTagEquals(Tag.DEFENSE_MIX_SET, tagSetting, buildSet("Clefable", "Leftovers", "Calm Mind"));
        assertSpecifyTagEquals(Tag.ATTACK_SET, tagSetting, buildSet("Ninetales", "Leftovers", "Calm Mind"));
        assertSpecifyTagEquals(Tag.ATTACK_MIX_SET, tagSetting, buildSet("Torkoal", "Leftovers", "Calm Mind"));
    }

    private void assertSpecifyTagEquals(Tag tag, TagSetting tagSetting, PokemonBuildSet pokemonBuildSet) {
        Set<Tag> pokemonTags = tagSettingProvider.getPokemonTags(tagSetting, pokemonBuildSet.name(), pokemonBuildSet);
        assertEquals(tag, pokemonTags.stream().findFirst().orElse(null));
    }

    public PokemonBuildSet buildSet(String name, String item, String... moves) {
        return new PokemonBuildSet(name, List.of(moves), null, Collections.singletonList(item), null);
    }
}