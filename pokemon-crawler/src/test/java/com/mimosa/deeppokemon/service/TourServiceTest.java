/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.mimosa.deeppokemon.entity.tour.TourBattle;
import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import com.mimosa.deeppokemon.entity.tour.TourPlayerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class TourServiceTest {

    @Test
    void updatePlayerRecord() {
        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        List<TourBattle> tourBattles = new ArrayList<>();
        tourBattles.add(buildBattle("a", "b", "a"));
        tourBattles.add(buildBattle("a", "c", "a"));
        tourBattles.add(buildBattle("b", "c", "b"));
        tourBattles.add(buildBattle("a", "d", "d"));
        Mockito.doReturn(tourBattles).when(mongoTemplate).find(Mockito.any(), Mockito.any());
        TourService tourService = new TourService(null, mongoTemplate, null);
        List<TourPlayerRecord> tourPlayerRecords = tourService.updatePlayerRecord("WCOP2024", "gen9ou");
        Assertions.assertEquals(4, tourPlayerRecords.size());
        for (TourPlayerRecord tourPlayerRecord : tourPlayerRecords) {
            switch (tourPlayerRecord.getName()) {
                case "a" -> assertPlayerRecord(tourPlayerRecord, 2, 1);
                case "b" -> assertPlayerRecord(tourPlayerRecord, 1, 1);
                case "c" -> assertPlayerRecord(tourPlayerRecord, 0, 2);
                case "d" -> assertPlayerRecord(tourPlayerRecord, 1, 0);
            }
        }
    }

    TourBattle buildBattle(String firstPlayer, String secondPlayer, String winner) {
        TourBattle tourBattle = new TourBattle();
        tourBattle.setWinSmogonPlayerName(winner);
        tourBattle.setSmogonPlayer(List.of(new TourPlayer(firstPlayer, null, null),
                new TourPlayer(secondPlayer, null, null)));
        return tourBattle;
    }

    void assertPlayerRecord(TourPlayerRecord tourPlayerRecord, int exceptWin, int exceptLose) {
        int total = exceptWin + exceptLose;
        float exceptWinRate = (float) exceptWin / (float) total;
        int exceptDif = exceptWin - exceptLose;
        Assertions.assertNotNull(tourPlayerRecord.getId());
        Assertions.assertNotNull(tourPlayerRecord.getFormat());
        Assertions.assertNotNull(tourPlayerRecord.getTourId());
        Assertions.assertEquals(exceptWin, tourPlayerRecord.getWin());
        Assertions.assertEquals(exceptLose, tourPlayerRecord.getLoss());
        Assertions.assertEquals(exceptDif, tourPlayerRecord.getWinDif());
        Assertions.assertEquals(exceptWinRate, tourPlayerRecord.getWinRate());
    }
}