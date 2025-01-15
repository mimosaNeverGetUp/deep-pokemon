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
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.utils.MatcherUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class PlayerEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(PlayerEventAnalyzer.class);

    private static final String PLAYER = "player";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(PLAYER);
    public static final Pattern PLAYER_NUMBER_PATTERN = Pattern.compile("p(\\d)");
    protected static final int ICON_INDEX = 2;

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleContext battleContext) {
        if (battleEvent.getContents().size() < ICON_INDEX) {
            log.warn("can not match player content: {}", battleEvent);
            return;
        }

        String playerNumberStr = MatcherUtil.groupMatch(PLAYER_NUMBER_PATTERN, battleEvent.getContents().get(0), 1);
        String playerName = battleEvent.getContents().get(1);
        if (!StringUtils.hasText(playerNumberStr) || !StringUtils.hasText(playerName)) {
            log.error("can not match player name or number: {}", battleEvent);
            throw new IllegalArgumentException("can not match player name or number");
        }
        if (battleStat.playerStatList().stream().noneMatch(playerStat -> playerStat.getPlayerName().equals(playerName))) {
            battleStat.playerStatList().add(new PlayerStat(Integer.parseInt(playerNumberStr), playerName));
            battleContext.getPlayerStatusList().add(new PlayerStatus());
        }
        if (battleEvent.getContents().size() >= 3) {
            setPlayerIcon(playerName, battleEvent.getContents().get(ICON_INDEX), battleContext);
        }
    }

    private void setPlayerIcon(String name, String icon, BattleContext battleContext) {
        Battle battle = battleContext.getBattle();
        if (battle.getPlayerIcons() == null) {
            List<PlayerIcon> playerIcons = new ArrayList<>();
            playerIcons.add(new PlayerIcon(name, icon, null));
            battle.setPlayerIcons(playerIcons);
        } else {
            battle.getPlayerIcons().add(new PlayerIcon(name, icon, null));
        }
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}