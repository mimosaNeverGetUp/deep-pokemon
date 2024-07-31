/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.provider;

import com.mimosa.deeppokemon.entity.Replay;
import com.mimosa.deeppokemon.entity.ReplaySource;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class FixedReplayProvider implements ReplayProvider {
    private final Queue<ReplaySource> replays = new LinkedList<>();

    public FixedReplayProvider(Collection<String> battleIds) {
        for (String battleId : battleIds) {
            replays.add(new ReplaySource(Collections.singletonList("fixed"), Collections.singletonList(new Replay(battleId))));
        }
    }

    @Override
    public ReplaySource next() {
        return replays.poll();
    }

    @Override
    public boolean hasNext() {
        return !replays.isEmpty();
    }
}