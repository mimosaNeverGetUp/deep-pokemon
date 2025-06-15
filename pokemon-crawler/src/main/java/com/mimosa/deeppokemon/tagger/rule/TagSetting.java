/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.tagger.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TagSetting(String format,@JsonProperty("bulk_def_threshold") Double bulkDefThreshold,
                         @JsonProperty("good_def_threshold") Double goodDefThreshold,
                         @JsonProperty("bulk_atk_threshold") Double bulkAtkThreshold,
                         @JsonProperty("good_atk_threshold") Double goodAtkThreshold,
                         @JsonProperty("ability_value") List<AbilityValue> abilityValue,
                         @JsonProperty("item_value") List<ItemValue> itemValue,
                         @JsonProperty("move_value") List<MoveValue> moveValue,
                         @JsonProperty("specify_pokemon_rule") List<SpecifyPokemonRule> specifyPokemonRule) {
}