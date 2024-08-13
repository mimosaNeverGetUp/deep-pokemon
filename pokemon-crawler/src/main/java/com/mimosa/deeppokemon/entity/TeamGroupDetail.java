/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import java.time.LocalDate;

public record TeamGroupDetail(LocalDate start, LocalDate end, String teamGroupCollectionName,
                              String teamSetCollectionName) {

}