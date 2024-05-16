/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.task.entity;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.stat.BattleStat;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record CrawAnalyzeBattleFuture(CompletableFuture<List<Battle>> crawFuture,
                                      CompletableFuture<List<BattleStat>> analyzeFuture) {
}