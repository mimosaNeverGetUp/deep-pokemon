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
