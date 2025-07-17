/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.tagger.rule;

import java.util.List;
import java.util.Set;

public record SpecifyPokemonRule(Set<String> names, List<TagRule> rules) {
}

record TagRule(List<Condition> condition, String tag) {

}

record Condition(String operator, List<SetCondition> set) {

}

record SetCondition(Set<String> move, Set<String>item, String type) {

}