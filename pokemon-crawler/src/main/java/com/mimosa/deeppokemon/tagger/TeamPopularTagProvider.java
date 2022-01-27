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

package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.crawler.PokemonInfoCrawlerImp;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Tag;
import com.mimosa.deeppokemon.entity.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * @program: deep-pokemon
 * @description: 队伍冷无缺程度标签
 * @author: mimosa
 * @create: 2020//10//28
 */
@Component
public class TeamPopularTagProvider implements TeamTagProvider {

    @Autowired
    private PokemonInfoCrawlerImp pokemonInfoCrawlerImp;

    private static Logger logger = LoggerFactory.getLogger(TeamPopularTagProvider.class);
    @Override
    public void tag(Team team) {
        HashSet<Tag> tags = team.getTagSet();
        float unpopularPokemonUse = 0;//使用冷门精灵的程度
        try {
            //手动神经元，求和大于阈值判断分类
            for (Pokemon pokemon : team.getPokemons()) {
                PokemonInfo pokemonInfo = pokemonInfoCrawlerImp.getPokemonInfo(pokemon);
                if (pokemonInfo == null) {
                    throw new NullPointerException("pokemon info "+pokemon.getName()+" get fail");
                }
                if (!pokemonInfo.getTier().equals("Illegal") && !pokemonInfo.getTier().equals("OU")) {
                    if (pokemonInfo.getTier().contains("UU")) {
                        unpopularPokemonUse += 0.5f;
                    } else {
                        unpopularPokemonUse += 1;
                    }
                }
            }
            if (unpopularPokemonUse > 1.0) {
                tags.add(Tag.UNPOPULAR);
            }

        } catch (Exception e) {
            logger.error("tag team fail",e);
        }
    }
}
