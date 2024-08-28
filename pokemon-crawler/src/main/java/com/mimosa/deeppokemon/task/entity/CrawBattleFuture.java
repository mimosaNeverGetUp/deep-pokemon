/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.task.entity;

import com.mimosa.deeppokemon.entity.Battle;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record CrawBattleFuture(CompletableFuture<List<Battle>> crawFuture) {
}