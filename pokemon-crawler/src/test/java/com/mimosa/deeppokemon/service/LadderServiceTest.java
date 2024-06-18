/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import com.mimosa.deeppokemon.entity.Ladder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class LadderServiceTest {
    @Autowired
    LadderService ladderService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void save() {
        LocalDate today = LocalDate.now();
        String id = DateTimeFormatter.BASIC_ISO_DATE.format(today);
        Ladder ladder = new Ladder();
        ladder.setId(id);
        try {
            ladderService.save(ladder, false);
            assertThrows(Exception.class, () -> ladderService.save(ladder, false));
            assertDoesNotThrow(()-> ladderService.save(ladder, true));
        } finally {
            mongoTemplate.remove(ladder);
        }
    }
}