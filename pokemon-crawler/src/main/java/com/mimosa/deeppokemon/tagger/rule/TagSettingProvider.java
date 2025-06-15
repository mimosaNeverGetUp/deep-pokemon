/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.tagger.rule;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.entity.PokemonBuildSet;
import com.mimosa.deeppokemon.entity.Tag;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class TagSettingProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagSettingProvider.class);
    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    protected static final String CLASSPATH_TAG_SETTING = "classpath:tagSetting/**";
    protected static final String OPERATOR_OR = "or";
    protected static final String AND = "and";

    private volatile Map<String, TagSetting> tagSettingMap = new HashMap<>();

    public TagSetting getTagSetting(String format) {
        if (tagSettingMap.isEmpty()) {
            synchronized (this) {
                if (tagSettingMap.isEmpty()) {
                    loadTagSetting();
                }
            }
        }
        return tagSettingMap.get(format);
    }

    public void loadTagSetting() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(CLASSPATH_TAG_SETTING);

            for (Resource resource : resources) {
                TagSetting tagSetting =
                        OBJECT_MAPPER.readValue(resource.getContentAsString(StandardCharsets.UTF_8), TagSetting.class);
                tagSettingMap.put(tagSetting.format(), tagSetting);
            }
        } catch (IOException e) {
            LOGGER.error("load tag setting error", e);
        }
    }

    public ItemValue getItemValue(TagSetting tagSetting, String item) {
        for (ItemValue itemValue : tagSetting.itemValue()) {
            if (itemValue.items().contains(item)) {
                return itemValue;
            }
        }
        LOGGER.debug("no item {}", item);
        return null;
    }

    public AbilityValue getAbilityValue(TagSetting tagSetting, String ability) {
        for (AbilityValue abilityValue : tagSetting.abilityValue()) {
            if (abilityValue.abilities().contains(ability)) {
                return abilityValue;
            }
        }
        LOGGER.debug("no ability {}", ability);
        return null;
    }

    public double getMoveAtkValue(TagSetting tagSetting, Set<String> moves) {
        double atkValue = 0D;
        double atkExtraValue = 0D;
        for (MoveValue moveValue : tagSetting.moveValue()) {
            if (moves.stream().anyMatch(moveValue.moves()::contains)) {
                if (Boolean.FALSE.equals(moveValue.isExtraValue())) {
                    atkValue = Math.max(atkValue, moveValue.atkValue());
                } else {
                    atkExtraValue += moveValue.atkValue();
                }
            }
        }
        return atkValue + atkExtraValue;
    }

    public double getMoveDefValue(TagSetting tagSetting, Set<String> moves) {
        double defValue = 0D;
        double defExtraValue = 0D;
        for (MoveValue moveValue : tagSetting.moveValue()) {
            if (moves.stream().anyMatch(moveValue.moves()::contains)) {
                if (Boolean.FALSE.equals(moveValue.isExtraValue())) {
                    defValue = Math.max(defValue, moveValue.defValue());
                } else {
                    defExtraValue += moveValue.defValue();
                }
            }
        }
        return defValue + defExtraValue;
    }

    public Set<Tag> getPokemonTags(TagSetting tagSetting, String pokemon, PokemonBuildSet pokemonBuildSet) {
        for (SpecifyPokemonRule pokemonRule : tagSetting.specifyPokemonRule()) {
            if (pokemonRule.names().contains(pokemon)) {
                Tag tag = getSpecifyPokemonTag(pokemonRule, pokemonBuildSet);
                if (tag != null) {
                    return Collections.singleton(tag);
                }
            }
        }
        return Collections.emptySet();
    }

    private Tag getSpecifyPokemonTag(SpecifyPokemonRule pokemonRule, PokemonBuildSet pokemonBuildSet) {
        for (TagRule tagRule : pokemonRule.rules()) {
            if (isTagConditionMatch(tagRule.condition(), pokemonBuildSet)) {
                return Tag.valueOf(tagRule.tag().toUpperCase(Locale.ROOT));
            }
        }
        return null;
    }

    private boolean isTagConditionMatch(List<Condition> conditions, PokemonBuildSet pokemonBuildSet) {
        if (conditions.isEmpty()) {
            return true;
        }

        String item = null;
        Set<String> topMoves = Collections.emptySet();
        if (pokemonBuildSet != null) {
            List<String> items = pokemonBuildSet.items();
            item = items == null || items.isEmpty() ? null : items.get(0);
            topMoves = pokemonBuildSet.moves() == null ? Collections.emptySet() :
                    new HashSet<>(pokemonBuildSet.moves().subList(0, Math.min(pokemonBuildSet.moves().size(), 4)));
        }

        boolean isTagConditionMatch = true;
        for (Condition condition : conditions) {
            isTagConditionMatch = isTagConditionMatch && isSetConditionMatch(condition, item, topMoves);

        }
        return isTagConditionMatch;
    }

    private boolean isSetConditionMatch(Condition condition, String item, Set<String> topMoves) {
        boolean isSetConditionMatch = true;
        for (SetCondition setCondition : condition.set()) {
            if (isSetItemConditionMatch(setCondition.item(), item, setCondition.type())
                    && isSetMoveConditionMatch(setCondition.move(), topMoves, setCondition.type())) {
                isSetConditionMatch = true;
                if (StringUtils.equals(condition.operator(), OPERATOR_OR)) {
                    break;
                }
            } else {
                isSetConditionMatch = false;
                if (StringUtils.equals(condition.operator(), AND)) {
                    break;
                }
            }
        }
        return isSetConditionMatch;
    }

    private boolean isSetMoveConditionMatch(Set<String> moves, Set<String> topMoves, String type) {
        if (moves.isEmpty()) {
            return true;
        }
        return switch (type) {
            case "in" -> topMoves.stream().anyMatch(moves::contains);
            case "not in" -> topMoves.stream().noneMatch(moves::contains);
            default -> false;
        };
    }

    private boolean isSetItemConditionMatch(Set<String> items, String item, String type) {
        if (items.isEmpty()) {
            return true;
        }
        return switch (type) {
            case "in" -> items.contains(item);
            case "not in" -> !items.contains(item);
            default -> false;
        };
    }
}