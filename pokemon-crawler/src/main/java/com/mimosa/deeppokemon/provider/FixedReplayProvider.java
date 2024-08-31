/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;

import java.util.*;

public class FixedReplayProvider implements ReplayProvider {
    private final Queue<ReplaySource> replaySources = new LinkedList<>();

    public FixedReplayProvider(List<Replay> replays,List<String> replayType) {
        replaySources.add(new ReplaySource(replayType, replays));
    }

    public FixedReplayProvider(Collection<String> battleIds) {
        for (String battleId : battleIds) {
            replaySources.add(new ReplaySource(Collections.singletonList("fixed"), Collections.singletonList(new Replay(battleId))));
        }
    }

    @Override
    public ReplaySource next() {
        return replaySources.poll();
    }

    @Override
    public boolean hasNext() {
        return !replaySources.isEmpty();
    }
}