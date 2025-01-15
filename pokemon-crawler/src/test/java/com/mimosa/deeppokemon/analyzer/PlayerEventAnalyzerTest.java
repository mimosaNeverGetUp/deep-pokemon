/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.PlayerIcon;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class PlayerEventAnalyzerTest {
    @Autowired
    PlayerEventAnalyzer playerEventAnalyzer;

    @Test
    void analyze() {
        BattleEvent battleEvent = new BattleEvent("player", List.of("p1", "RUBYBLOOD", "#splxvtyrants2"), null
                , null);
        BattleStat battleStat = new BattleStat(null, new ArrayList<>(),new ArrayList<>());
        BattleContext battleContext = new BattleContext(new ArrayList<>(), new Battle());
        playerEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        Assertions.assertEquals(1, battleStat.playerStatList().size());
        PlayerStat playerStat = battleStat.playerStatList().get(0);
        Assertions.assertTrue(playerEventAnalyzer.supportAnalyze(battleEvent));
        Assertions.assertNotNull(playerStat);
        Assertions.assertEquals(1, playerStat.getPlayerNumber());
        Assertions.assertEquals("RUBYBLOOD", playerStat.getPlayerName());
        Assertions.assertNotNull(playerStat.getPokemonBattleStats());
        Assertions.assertEquals(1, battleContext.getPlayerStatusList().size());
        List<PlayerIcon> playerIcons = battleContext.getBattle().getPlayerIcons();
        Assertions.assertEquals(1, playerIcons.size());
        PlayerIcon playerIcon = playerIcons.get(0);
        Assertions.assertEquals("RUBYBLOOD", playerIcon.name());
        Assertions.assertEquals("#splxvtyrants2", playerIcon.icon());
        playerEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        Assertions.assertEquals(1, battleStat.playerStatList().size());
    }
}