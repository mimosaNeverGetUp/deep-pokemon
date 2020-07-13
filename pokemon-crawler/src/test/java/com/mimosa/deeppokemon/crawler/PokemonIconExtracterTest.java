package com.mimosa.deeppokemon.crawler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PokemonIconExtracterTest {

    @Test
    void extract() {
        try {
            PokemonIconExtracter.extract("E:\\java\\study\\deep-pokemon\\pokemon-crawler\\src\\main\\resources\\META-INF\\pokemonicon\\pokemonicons-sheet.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}