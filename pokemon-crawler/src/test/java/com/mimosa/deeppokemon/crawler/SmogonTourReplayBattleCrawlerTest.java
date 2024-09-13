/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.*;
import com.mimosa.deeppokemon.entity.tour.TourBattle;
import com.mimosa.deeppokemon.entity.tour.TourPlayer;
import com.mimosa.deeppokemon.entity.tour.TourTeam;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmogonTourReplayBattleCrawlerTest {

    protected static final String TEST_123 = "test123";

    @Test
    void craw() {
        ReplayBattleCrawler replayBattleCrawler = Mockito.mock(ReplayBattleCrawler.class);

        SmogonTourReplay replay = new SmogonTourReplay(TEST_123);
        replay.setTourName("WCOP 2024");
        replay.setStage("Finals");
        TourPlayer a = new TourPlayer("a", null, null);
        TourPlayer b = new TourPlayer("b", null, null);
        replay.setTourPlayers(List.of(a, b));
        replay.setWinPlayer(new TourPlayer("a", null, null));

        Battle battle = buildBattle();

        Mockito.doReturn(Collections.singletonList(battle)).when(replayBattleCrawler).craw(Mockito.any());
        TourBattle tourBattle;
        try (var mockHttpUtil = Mockito.mockStatic(HttpUtil.class)) {
            mockHttpUtil.when(() -> HttpUtil.request(Mockito.any())).thenReturn(null);
            SmogonTourReplayBattleCrawler crawler = new SmogonTourReplayBattleCrawler(replayBattleCrawler);
            tourBattle = (TourBattle) (crawler.craw(new ReplaySource(null, Collections.singletonList(replay))).get(0));
        }

        assertNotNull(tourBattle);
        assertNotNull(tourBattle.getBattleID());
        assertNotNull(tourBattle.getFormat());
        assertNotNull(tourBattle.getDate());
        assertEquals("a1", tourBattle.getWinner());
        assertEquals("log", tourBattle.getLog());
        assertNotNull(tourBattle.getType());
        assertNotNull(tourBattle.getBattleTeams());
        assertEquals(2, tourBattle.getBattleTeams().size());
        BattleTeam team1 = tourBattle.getBattleTeams().get(0);
        assertInstanceOf(TourTeam.class, team1);
        TourTeam tourTeamA = (TourTeam) team1;
        assertNotNull(tourTeamA.getBattleId());
        assertEquals("WCOP 2024", tourTeamA.getTourId());
        assertEquals("Finals", tourTeamA.getStage());
        assertEquals("a", tourTeamA.getPlayer().getName());

        assertEquals("WCOP 2024", tourBattle.getTourId());
        assertEquals("Finals", tourBattle.getStage());
        assertEquals("a", tourBattle.getWinSmogonPlayerName());
        List<TourPlayer> smogonPlayers = tourBattle.getSmogonPlayer();
        assertNotNull(smogonPlayers);
        assertEquals(2, smogonPlayers.size());
    }

    @Test
    void crawBo3Battle() {
        ReplayBattleCrawler replayBattleCrawler = Mockito.mock(ReplayBattleCrawler.class);

        SmogonTourReplay replay = new SmogonTourReplay(TEST_123);
        replay.setTourName("WCOP 2024");
        replay.setStage("Finals");
        TourPlayer a = new TourPlayer("a", null, null);
        TourPlayer b = new TourPlayer("b", null, null);
        replay.setTourPlayers(List.of(a, b));
        replay.setWinPlayer(new TourPlayer("a", null, null));

        Battle battleA = buildBattle();
        battleA.setWinner("b1");
        Battle battleB = buildBattle();
        battleB.setWinner("a1");
        Battle battleC = buildBattle();
        battleC.setWinner("a1");

        Mockito.doReturn(Collections.singletonList(battleA), Collections.singletonList(battleB),
                Collections.singletonList(battleC)).when(replayBattleCrawler).craw(Mockito.any());
        ReplaySource replaySource = new ReplaySource(null, Collections.nCopies(3, replay));
        List<Battle> tourBattles;
        try (var mockHttpUtil = Mockito.mockStatic(HttpUtil.class)) {
            mockHttpUtil.when(() -> HttpUtil.request(Mockito.any())).thenReturn(null);
            SmogonTourReplayBattleCrawler crawler = new SmogonTourReplayBattleCrawler(replayBattleCrawler);
            tourBattles = crawler.craw(replaySource);
        }

        for(Battle battle : tourBattles) {
            TourBattle tourBattle = (TourBattle) battle;
            if (tourBattle.getWinner().equals("a1")) {
                assertEquals("a", tourBattle.getWinSmogonPlayerName());
            } else {
                assertEquals("b", tourBattle.getWinSmogonPlayerName());
            }
            for (BattleTeam battleTeam : tourBattle.getBattleTeams()) {
                TourTeam tourTeam = (TourTeam) battleTeam;
                if (tourTeam.getPlayerName().equals("a1")) {
                    assertEquals("a", tourTeam.getPlayer().getName());
                } else {
                    assertEquals("b", tourTeam.getPlayer().getName());
                }
            }
        }
    }

    private static @NotNull Battle buildBattle() {
        Battle battle = new Battle();
        battle.setBattleID(TEST_123);
        battle.setFormat("gen9ou");
        battle.setDate(LocalDateTime.now());
        battle.setPlayers(List.of("a1", "b1"));
        battle.setWinner("a1");
        battle.setLog("log");
        battle.setType(List.of("WCOP 2024"));

        BattleTeam teamA = new BattleTeam();
        teamA.setId("team_1");
        teamA.setBattleId(battle.getBattleID());
        teamA.setBattleDate(LocalDateTime.now());
        teamA.setTeamId("team_1".getBytes());
        teamA.setBattleType(battle.getType());
        teamA.setPlayerName("a1");
        teamA.setTier("gen9ou");
        teamA.setTagSet(Collections.singleton(Tag.STAFF));
        teamA.setPokemons(Collections.singletonList(new Pokemon("1")));
        BattleTeam teamB = new BattleTeam();
        teamB.setId("team_2");
        teamB.setBattleId(battle.getBattleID());
        teamB.setBattleDate(LocalDateTime.now());
        teamB.setTeamId("team_2".getBytes());
        teamB.setBattleType(battle.getType());
        teamB.setPlayerName("b1");
        teamB.setTier("gen9ou");
        teamB.setTagSet(Collections.singleton(Tag.STAFF));
        teamB.setPokemons(Collections.singletonList(new Pokemon("2")));
        battle.setBattleTeams(List.of(teamA, teamB));
        return battle;
    }
}