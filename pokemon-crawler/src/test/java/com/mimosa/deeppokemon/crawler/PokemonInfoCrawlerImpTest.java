package com.mimosa.deeppokemon.crawler;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;



class PokemonInfoCrawlerImpTest {

    @Test
    void craw() throws IOException {
        PokemonInfoCrawler pokemonInfoCrawler = new PokemonInfoCrawlerImp();
        pokemonInfoCrawler.craw();
    }
}