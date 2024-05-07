/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.Side;
import com.mimosa.deeppokemon.analyzer.entity.Status;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.utils.BattleEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class StatusEventAnalyzer implements BattleEventAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(StatusEventAnalyzer.class);
    private static final String STATUS = "status";
    private static final Set<String> SUPPORT_EVENT_TYPE = Set.of(STATUS);

    private static final int TARGET_INDEX = 0;
    private static final int STATUS_INDEX = 1;
    private static final int FROM_INDEX = 2;
    private static final String TOXIC_SPIKES = "Toxic Spikes";

    @Override
    public void analyze(BattleEvent battleEvent, BattleStat battleStat, BattleStatus battleStatus) {
        if (battleEvent.getContents().size() < 2) {
            log.warn("can not analyze battle event {}", battleEvent);
            return;
        }

        EventTarget eventTarget = BattleEventUtil.getEventTarget(battleEvent.getContents().get(TARGET_INDEX),
                battleStatus);
        String status = battleEvent.getContents().get(STATUS_INDEX);
        if (eventTarget != null) {
            EventTarget ofTarget = null;
            if (battleEvent.getParentEvent() != null && battleEvent.getParentEvent().getBattleEventStat()
                    instanceof MoveEventStat moveEventStat) {
                ofTarget = moveEventStat.eventTarget();
            } else if (battleEvent.getContents().size() - 1 > FROM_INDEX) {
                ofTarget = eventTarget;
            } else if (!getToxicSide(battleStatus, eventTarget.playerNumber()).isEmpty()) {
                List<Side> toxicSide = getToxicSide(battleStatus, eventTarget.playerNumber());
                ofTarget = toxicSide.get(toxicSide.size() - 1).ofTarget();
            }
            battleStatus.getPlayerStatusList().get(eventTarget.playerNumber() - 1).getPokemonStatus(eventTarget.targetName())
                    .setStatus(new Status(status, ofTarget));
        }
    }

    private List<Side> getToxicSide(BattleStatus battleStatus, int playerNumber) {
        return battleStatus.getPlayerStatusList().get(playerNumber - 1).getSideListByName(TOXIC_SPIKES);
    }

    @Override
    public boolean supportAnalyze(BattleEvent battleEvent) {
        return SUPPORT_EVENT_TYPE.contains(battleEvent.getType());
    }
}