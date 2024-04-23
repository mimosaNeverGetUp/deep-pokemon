/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.PlayerStat;
import com.mimosa.deeppokemon.analyzer.entity.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SwitchEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(SwitchEventAnalyzer.class);
    private static final String SWITCH = "switch";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(SWITCH);
    private static final Pattern SWITHCH_PATTERN = Pattern.compile("p(\\d)[a-z]: (.+)");


    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.contents().size() < 2) {
            log.warn("can not match battle event contents: {}", battleEvent);
            return;
        }

        String pokemonName = battleEvent.contents().get(1).split(",")[0];
        Matcher matcher = SWITHCH_PATTERN.matcher(battleEvent.contents().get(0));
        if (matcher.find()) {
            int playerNumber = Integer.parseInt(matcher.group(1));
            String pokemonNickName = matcher.group(2);
            battleStatus.getPlayerStatusList().get(playerNumber - 1).setPokemonNickNameMap(pokemonNickName,
                    pokemonName);
            PlayerStat playerStat = battleStat.playerStatList().get(playerNumber - 1);
            playerStat.setSwitchCount(playerStat.getSwitchCount() + 1);
            PokemonBattleStat pokemonBattleStat =
                    playerStat.getPokemonBattleStat(pokemonName);
            pokemonBattleStat.setSwitchCount(pokemonBattleStat.getSwitchCount() + 1);
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.type());
    }
}