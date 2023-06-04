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

package com.mimosa.deeppokemon.config;

import com.mimosa.deeppokemon.tagger.TeamAttackDefenceTagProvider;
import com.mimosa.deeppokemon.tagger.TeamPopularTagProvider;
import com.mimosa.deeppokemon.tagger.TeamTagProvider;
import com.mimosa.deeppokemon.tagger.TeamTagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
