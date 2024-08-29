/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.config.MongodbTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = MongodbTestConfig.class)
class TourServiceTest {
    @Autowired
    private TourService tourService;

    @Test
    void crawWcop2024() {
//        List<Battle> battles = tourService.crawWcop2024();
//        assertNotNull(battles);
//        assertFalse(battles.isEmpty());
    }
}