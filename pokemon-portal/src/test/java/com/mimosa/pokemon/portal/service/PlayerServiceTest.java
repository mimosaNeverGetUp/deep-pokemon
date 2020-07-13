package com.mimosa.pokemon.portal.service;

import com.mimosa.deeppokemon.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class PlayerServiceTest {

    @Autowired
    PlayerService playerService;

    @Test
    void listPlayerRank() {
        List<Player> playerList = playerService.listPlayerRank();
        for (Player player :playerList ) {
            System.out.println(player);
        }

    }
}