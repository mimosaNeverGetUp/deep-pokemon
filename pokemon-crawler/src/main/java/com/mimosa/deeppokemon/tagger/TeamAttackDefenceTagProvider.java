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
import com.mimosa.deeppokemon.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class TeamAttackDefenceTagProvider implements TeamTagProvider {
    private static final Logger logger = LoggerFactory.getLogger(TeamAttackDefenceTagProvider.class);

    private final PokemonAttackDefenseTagProvider pokemonAttackDefenseTagProvider;

    private final PokemonInfoCrawlerImp pokemonInfoCrawlerImp;

    public TeamAttackDefenceTagProvider(PokemonAttackDefenseTagProvider pokemonAttackDefenseTagProvider, PokemonInfoCrawlerImp pokemonInfoCrawlerImp) {
        this.pokemonAttackDefenseTagProvider = pokemonAttackDefenseTagProvider;
        this.pokemonInfoCrawlerImp = pokemonInfoCrawlerImp;
    }

    @Override
    public void tag(Team team, TeamSet teamSet) {
        Set<Tag> tags = team.getTagSet();
        float attackDefenseDif = 0;//攻受差异，大于0表示攻向
        Map<String, PokemonBuildSet> pokemonBuildSetMap = new HashMap<>();
        if (teamSet != null && teamSet.pokemons() != null) {
            for(PokemonBuildSet pokemonBuildSet : teamSet.pokemons()) {
                pokemonBuildSetMap.put(pokemonBuildSet.name(), pokemonBuildSet);
            }
        }

        try {
            for (Pokemon pokemon : team.getPokemons()) {
                PokemonInfo pokemonInfo = pokemonInfoCrawlerImp.getPokemonInfo(pokemon.getName());
                if (pokemonInfo == null) {
                    logger.error("pokemoninfo {} not found and team tag fail", pokemon.getName());
                    return;
                }
                pokemonAttackDefenseTagProvider.tag(pokemonInfo, pokemonBuildSetMap.get(pokemon.getName()));
                logger.debug("pokemon {} tag {}", pokemonInfo.getName(), pokemonInfo.getTags());
                //手动神经元，加权求和大于阈值进行分类...
                for (Tag tag : pokemonInfo.getTags()) {
                    switch (tag) {
                        case STAFF:
                            attackDefenseDif -= 1F;
                            break;
                        case BALANCE_STAFF:
                            attackDefenseDif -= 0.5F;
                            break;
                        case BALANCE:
                            break;
                        case BALANCE_ATTACK:
                            attackDefenseDif += 0.5F;
                            break;
                        case ATTACK:
                            attackDefenseDif += 1F;
                            break;
                        default:
                            logger.error("tag {} not supported", tag);
                    }
                }
            }
            if (Math.abs(attackDefenseDif) >= 2) {
                if (attackDefenseDif > 0) {
                    tags.add(Tag.ATTACK);
                } else {
                    tags.add(Tag.STAFF);
                }
            } else if (Math.abs(attackDefenseDif) >= 1) {
                if (attackDefenseDif > 0) {
                    tags.add(Tag.BALANCE_ATTACK);
                } else {
                    tags.add(Tag.BALANCE_STAFF);
                }
            } else {
                tags.add(Tag.BALANCE);
            }
            logger.debug("{} attackDefence diff is {}", team.getPokemons(), attackDefenseDif);
        } catch (Exception e) {
            logger.error("tag team fail", e);
        }

    }
}