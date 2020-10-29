package com.mimosa.deeppokemon.config;

import com.mimosa.deeppokemon.tagger.TeamAttackDefenceTagProvider;
import com.mimosa.deeppokemon.tagger.TeamPopularTagProvider;
import com.mimosa.deeppokemon.tagger.TeamTagProvider;
import com.mimosa.deeppokemon.tagger.TeamTagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.xml.ws.soap.Addressing;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: deep-pokemon
 * @description: 配置TeamTagger类
 * @author: mimosa
 * @create: 2020//10//28
 */
@Configuration
public class TeamTaggerConfig {

    @Autowired
    private TeamAttackDefenceTagProvider teamAttackDefenceTagProvider;

    @Autowired
    private TeamPopularTagProvider teamPopularTagProvider;

    @Bean
    TeamTagger teamTagger() {
        List<TeamTagProvider> teamTagProviderList = new ArrayList<>();
        teamTagProviderList.add(teamAttackDefenceTagProvider);
        teamTagProviderList.add(teamPopularTagProvider);
        return new TeamTagger(teamTagProviderList);
    }
}
