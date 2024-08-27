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

import com.mimosa.deeppokemon.entity.BattleTeam;
import com.mimosa.deeppokemon.entity.TeamSet;

import java.util.List;

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


    public void tagTeam(BattleTeam team, TeamSet teamSet) {
        for (TeamTagProvider teamTagProvider : teamTagProviders) {
            teamTagProvider.tag(team, teamSet);
        }
    }

}