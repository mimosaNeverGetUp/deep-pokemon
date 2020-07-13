package com.mimosa.pokemon.portal.service;

import com.mimosa.deeppokemon.entity.Player;
import com.mimosa.deeppokemon.entity.Team;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class BattleServiceTest {

    @Autowired
    BattleService battleService;

    @Autowired
    PlayerService playerService;

    @Test
    void listTeamByPlayerList() {
        List<Player> playerList = playerService.listPlayerRank();
        List<Team> teamList = battleService.listTeamByPlayerList(playerList);
        for (Player player : playerList) {
            System.out.println(player.getName());
        }
        for (Team team : teamList) {
            System.out.println(team);
        }
    }

    @Test
    void team() {
        List<Pair<Team, String>> list = battleService.Team(1);
        for (Pair<Team, String> pair : list) {
            System.out.println(pair.getKey()+pair.getValue());
        }
    }

    @Test
    void statistic() {
        LocalDate dayB = LocalDate.now();
        LocalDate dayA = dayB.minusMonths(1);
        Pair<Pair<Float, Float>, List<Team>> pair = battleService.statistic("Cinderace", dayA, dayB);
        System.out.println(pair.getKey().getKey());
        System.out.println(pair.getKey().getValue());

    }
}