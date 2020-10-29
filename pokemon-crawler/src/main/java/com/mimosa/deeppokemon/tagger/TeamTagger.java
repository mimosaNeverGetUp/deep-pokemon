package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.crawler.PokemonInfoCrawler;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Tag;
import com.mimosa.deeppokemon.entity.Team;

import java.io.IOException;
import java.util.*;

/**
 * @program: deep-pokemon
 * @description: 队伍标签整合类
 * @author: mimosa
 * @create: 2020//10//27
 */
public class TeamTagger {
    private List<TeamTagProvider> teamTagProviders;

    //设置队伍标签列表，需要扩充标签功能往teamTagProciders里增加即可
    public TeamTagger(List<TeamTagProvider> teamTagProviders) {
        this.teamTagProviders = teamTagProviders;
    }


    public void tagTeam(Team team) {
        for (TeamTagProvider teamTagProvider : teamTagProviders) {
            teamTagProvider.tag(team);
        }
    }

}
