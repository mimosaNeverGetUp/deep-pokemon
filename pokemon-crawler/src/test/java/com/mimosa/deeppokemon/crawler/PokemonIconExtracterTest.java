/*
 * The MIT License
 *
 * Copyright (c) [2022] [Xiaocong Huang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.PokemonInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class PokemonIconExtracterTest {
    public static final String iconPath = "C:\\Users\\Miyu\\IdeaProjects\\deep-pokemon\\pokemon-crawler\\src\\main" +
            "\\resources\\META-INF\\pokemonicon\\pokemonicons-sheet.png";

    public static final String pokemonIconIndexPath = "C:\\Users\\Miyu\\IdeaProjects\\deep-pokemon\\pokemon-crawler" +
            "\\src\\main\\resources\\META-INF\\pokemonicon\\pokemonInconIndex.json";

    @Test
    void extract() throws IOException {
        try {
            PokemonInfoCrawler pokemonInfoCrawler = new PokemonInfoCrawlerImp();
            List<PokemonInfo> pokemonInfos = pokemonInfoCrawler.craw();
            PokemonIconExtracter pokemonIconExtracter = new PokemonIconExtracter(iconPath, pokemonIconIndexPath,
                    pokemonInfos);
            pokemonIconExtracter.extract();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}