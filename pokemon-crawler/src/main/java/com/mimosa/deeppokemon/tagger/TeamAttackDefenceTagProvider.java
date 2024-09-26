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
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class TeamAttackDefenceTagProvider implements TeamTagProvider {
    private static final Logger logger = LoggerFactory.getLogger(TeamAttackDefenceTagProvider.class);

    private final List<PokemonAttackDefenseTagProvider> pokemonAttackDefenseTagProviders;

    private final PokemonInfoCrawlerImp pokemonInfoCrawlerImp;

    public TeamAttackDefenceTagProvider(List<PokemonAttackDefenseTagProvider> pokemonAttackDefenseTagProviders, PokemonInfoCrawlerImp pokemonInfoCrawlerImp) {
        this.pokemonAttackDefenseTagProviders = pokemonAttackDefenseTagProviders;
        this.pokemonInfoCrawlerImp = pokemonInfoCrawlerImp;
    }

    @Override
    public void tag(BattleTeam team, TeamSet teamSet) {
        Set<Tag> tags = team.getTagSet();
        Map<String, PokemonBuildSet> pokemonBuildSetMap = new HashMap<>();
        if (teamSet != null && teamSet.pokemons() != null) {
            for (PokemonBuildSet pokemonBuildSet : teamSet.pokemons()) {
                pokemonBuildSetMap.put(pokemonBuildSet.name(), pokemonBuildSet);
            }
        }

        try {
            float atk = 0;
            float def = 0;
            for (Pokemon pokemon : team.getPokemons()) {
                PokemonInfo pokemonInfo = pokemonInfoCrawlerImp.getPokemonInfo(pokemon.getName());
                if (pokemonInfo == null) {
                    logger.error("pokemoninfo {} not found and team tag fail", pokemon.getName());
                    return;
                }
                for (PokemonAttackDefenseTagProvider pokemonAttackDefenseTagProvider : pokemonAttackDefenseTagProviders) {
                    if (pokemonAttackDefenseTagProvider.supportTag(team.getTier())) {
                        pokemonAttackDefenseTagProvider.tag(pokemonInfo, pokemonBuildSetMap.get(pokemon.getName()));
                    }
                }
                logger.debug("pokemon {} tag {}", pokemonInfo.getName(), pokemonInfo.getTags());

                //手动神经元，加权求和大于阈值进行分类...
                for (Tag tag : pokemonInfo.getTags()) {
                    switch (tag) {
                        case ATTACK_SET:
                            atk += 1;
                            break;
                        case ATTACK_BULK_SET:
                            atk += 1;
                            def += 0.5F;
                            break;
                        case ATTACK_MIX_SET:
                            atk += 1;
                            def += 0.25F;
                            break;
                        case DEFENSE_SET:
                            def += 1F;
                            break;
                        case DEFENSE_BULK_SET:
                            def += 1;
                            atk += 0.5F;
                            break;
                        case DEFENSE_MIX_SET:
                            def += 1;
                            atk += 0.25F;
                            break;
                        case BALANCE_SET:
                            atk += 0.5F;
                            def += 0.5F;
                            break;
                        case BALANCE_BULK_SET:
                            atk += 0.75F;
                            def += 0.75F;
                            break;
                        default:
                            logger.error("tag {} not supported", tag);
                    }
                }
            }

            if (atk == 0 && def == 0) {
                logger.error("can not find valiad tag for team {}", team.getId());
                return;
            }

            float dif = atk - def;
            if (Math.abs(dif) <= 1.25) {
                tags.add(Tag.BALANCE);
            } else if (dif > 0 && def >= 2) {
                tags.add(Tag.BALANCE_ATTACK);
            } else if (dif > 0 && def < 2) {
                tags.add(Tag.ATTACK);
            } else if (dif < 0 && atk >= 2) {
                tags.add(Tag.BALANCE);
            } else if (dif < 0 && atk < 2) {
                tags.add(Tag.STAFF);
            } else {
                logger.error("unknown tag situation atk {} def {}", atk, def);
            }

            logger.info("{} atk {} def {} tag {}", team.getPokemons().stream().map(Pokemon::getName).toList(),
                    atk, def, tags);
        } catch (Exception e) {
            logger.error("tag team fail", e);
        }
    }
}