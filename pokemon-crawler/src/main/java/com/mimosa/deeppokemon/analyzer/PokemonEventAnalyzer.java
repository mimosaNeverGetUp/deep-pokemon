/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.utils.MatcherUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.regex.Pattern;

import static com.mimosa.deeppokemon.analyzer.PlayerEventAnalyzer.PLAYER_NUMBER_PATTERN;

@Component
public class PokemonEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(PokemonEventAnalyzer.class);

    private static final String POKE = "poke";

    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(POKE);

    private static final Pattern POKE_PATTERN = Pattern.compile("[^,]+");

    @Override
    public void analyze(BattleEvent event, BattleStat battleStat, BattleStatus battleStatus) {
        if (event.contents().size() < 2) {
            log.warn("can not analyze pokemon event content: {}", event);
            return;
        }

        String playerNumberStr = MatcherUtil.groupMatch(PLAYER_NUMBER_PATTERN, event.contents().get(0), 1);
        String pokemonName = MatcherUtil.groupMatch(POKE_PATTERN, event.contents().get(1), 0);
        if (!StringUtils.hasText(playerNumberStr) || !StringUtils.hasText(pokemonName)) {
            log.warn("can not match player number or pokemon name content: {}", event);
            return;
        }

        int playerNumber = Integer.parseInt(playerNumberStr);
        battleStat.playerStatList().get(playerNumber - 1).addPokemonBattleStat(new PokemonBattleStat(pokemonName));
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.type());
    }
}