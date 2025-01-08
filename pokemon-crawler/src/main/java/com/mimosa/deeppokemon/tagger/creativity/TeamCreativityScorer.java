/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.tagger.creativity;

import com.mimosa.deeppokemon.entity.PokemonBuildSet;
import com.mimosa.deeppokemon.entity.TeamSet;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyPokemonMoveSet;
import com.mimosa.deeppokemon.entity.stat.monthly.MonthlyPokemonUsage;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class TeamCreativityScorer {
    protected static final String STAT_ID = "statId";
    protected static final String YYYY_MM = "yyyyMM";
    protected static final String NAME = "name";

    private final Map<String, MonthlyPokemonUsage> monthlyPokemonUsageMap = new HashMap<>();
    private final Map<String, MonthlyPokemonMoveSet> monthlyPokemonMoveSetMap = new HashMap<>();
    private final MongoTemplate mongoTemplate;

    public TeamCreativityScorer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public float getCreativeScore(TeamSet teamSet) {
        float creativityScore = 0F;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM);
        String latestStatId = formatter.format(LocalDate.now().minusMonths(1)) + teamSet.tier();

        for (PokemonBuildSet pokemon : teamSet.pokemons()) {
            creativityScore += getPokemonCreativeScore(pokemon, latestStatId);
        }

        return creativityScore;
    }

    private float getPokemonCreativeScore(PokemonBuildSet pokemon, String latestStatId) {
        float usageScore = getPokemonUsageScore(pokemon.name(), latestStatId);
        float itemScore = getPokemonItemScore(pokemon.name(), pokemon.items().stream().findFirst().orElse(null),
                latestStatId);
        float moveScore = getPokemonMoveScore(pokemon, latestStatId);
        return usageScore + itemScore + moveScore;
    }

    private float getPokemonMoveScore(PokemonBuildSet pokemon, String statId) {
        Set<String> topMoves = new HashSet<>(pokemon.moves().subList(0,
                Math.min(pokemon.moves().size(), 4)));
        if (topMoves.isEmpty()) {
            return 0;
        }

        MonthlyPokemonMoveSet pokemonMoveSet = getMonthlyPokemonMoveSet(statId, pokemon.name());
        if (pokemonMoveSet == null) {
            return 0;
        }
        float moveScore = 0;
        for (String move : topMoves) {
            Double usage = pokemonMoveSet.moves().get(move);
            if (usage == null || usage < 0.01D) {
                moveScore = Math.min(1F, moveScore + 0.5F);
            } else if (usage < 0.02D) {
                moveScore = Math.min(1F, moveScore + 0.25F);
            } else if (usage < 0.05D) {
                moveScore = Math.min(1F, moveScore + 0.1F);
            }
        }


        return moveScore;
    }

    private @Nullable MonthlyPokemonMoveSet getMonthlyPokemonMoveSet(String statId, String pokemon) {
        if (monthlyPokemonMoveSetMap.containsKey(statId + pokemon)) {
            return monthlyPokemonMoveSetMap.get(statId + pokemon);
        }

        Query query = new Query().addCriteria(Criteria.where(STAT_ID).is(statId).and(NAME).is(pokemon));
        MonthlyPokemonMoveSet pokemonMoveSet = mongoTemplate.findOne(query, MonthlyPokemonMoveSet.class);
        monthlyPokemonMoveSetMap.put(statId + pokemon, pokemonMoveSet);
        return pokemonMoveSet;
    }

    private float getPokemonItemScore(String pokemon, String item, String statId) {
        if (item == null) {
            return 0;
        }

        MonthlyPokemonMoveSet pokemonMoveSet = getMonthlyPokemonMoveSet(statId, pokemon);
        if (pokemonMoveSet == null) {
            return 0;
        }

        Double usage = pokemonMoveSet.items().get(item);
        if (usage == null || usage < 0.03D) {
            return 0.5F;
        } else if (usage < 0.1D) {
            return 0.25F;
        }

        return 0;
    }

    private float getPokemonUsageScore(String pokemon, String statId) {
        MonthlyPokemonUsage pokemonUsage = getMonthlyPokemonUsage(pokemon, statId);
        if (pokemonUsage == null || pokemonUsage.usage().weighted() < 0.01D) {
            return 1F;
        } else if (pokemonUsage.usage().weighted() < 0.02D) {
            return 0.25F;
        }

        return 0;
    }

    private @Nullable MonthlyPokemonUsage getMonthlyPokemonUsage(String pokemon, String statId) {
        if (monthlyPokemonUsageMap.containsKey(statId + pokemon)) {
            return monthlyPokemonUsageMap.get(statId + pokemon);
        }

        Query query = new Query().addCriteria(Criteria.where(STAT_ID).is(statId).and(NAME).is(pokemon));
        MonthlyPokemonUsage pokemonUsage = mongoTemplate.findOne(query, MonthlyPokemonUsage.class);
        monthlyPokemonUsageMap.put(statId + pokemon, pokemonUsage);
        return pokemonUsage;
    }
}