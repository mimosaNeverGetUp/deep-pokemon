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
import java.util.List;

/**
 * @program: deep-pokemon
 * @description: 队伍攻受标签提供类
 * @author: mimosa
 * @create: 2020//10//27
 */
@Component
public class TeamAttackDefenceTagProvider implements TeamTagProvider {
    @Autowired
    private PokemonAttackDefenseTagProvider pokemonAttackDefenseTagProvider;

    @Autowired
    private PokemonInfoCrawlerImp pokemonInfoCrawlerImp;

    private static Logger logger = LoggerFactory.getLogger(TeamAttackDefenceTagProvider.class);
    @Override
    public void tag(Team team) {
        HashSet<Tag> tags = team.getTagSet();
        float attackDefenseDif = 0;//攻受差异，大于0表示攻向
        try {
            for (Pokemon pokemon : team.getPokemons()) {
                PokemonInfo pokemonInfo = pokemonInfoCrawlerImp.getPokemonInfo(pokemon);
                if (pokemonInfo == null) {
                    System.out.println(pokemon.getName());
                    logger.error("pokemoninfo not found and team tag fail");
                    return;
                }
                pokemonAttackDefenseTagProvider.tag(pokemonInfo);
                //手动神经元，加权求和大于阈值进行分类...
                for (Tag tag : pokemonInfo.getTags()) {
                    switch (tag) {
                        case STAFF:
                            attackDefenseDif -= 1;
                            break;
                        case BALANCE_STAFF:
                            attackDefenseDif -= 0.5;
                            break;
                        case BALANCE:
                            break;
                        case BALANCE_ATTACK:
                            attackDefenseDif += 0.5;
                            break;
                        case ATTACK:
                            attackDefenseDif += 1;
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
        } catch (Exception e) {
            logger.error("tag team fail!",e);
        }

    }
}
