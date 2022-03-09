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

package com.mimosa.deeppokemon.refactor.crawler;

import com.mimosa.deeppokemon.refactor.entity.metadata.battle.BattleMetaData;
import com.mimosa.deeppokemon.refactor.entity.CrawResource;
import com.mimosa.deeppokemon.refactor.entity.metadata.battle.Player;
import com.mimosa.deeppokemon.refactor.entity.metadata.battle.Pokemon;
import com.mimosa.deeppokemon.refactor.entity.metadata.battle.Team;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BattleMetaDataCrawler implements MetaDataCralwer{

    /**
     * 提取比赛元数据
     *
     * @param resource 比赛爬取源数据
     * @return Meata 从爬取源中解析得到的比赛元数据
     * @author huangxiaocong(2070132549@qq.com)
     */
    public BattleMetaData craw(CrawResource resource){
        BattleMetaData battleMetaData = new BattleMetaData("smogtours-gen8ou-560155", LocalDate.of(2021, 5, 9),
                0.0F, "Separation");
        List<Player> playerList = new ArrayList<>();
        List<Pokemon> pokemonListA = Arrays.asList(
                new Pokemon("Zeraora", null),
                new Pokemon("Tapu Fini", null),
                new Pokemon("Garchomp", null),
                new Pokemon("Moltres-Galar", null),
                new Pokemon("Aegislash", null),
                new Pokemon("Zapdos-Galar", null));
        List<Pokemon> pokemonListB = Arrays.asList(
                new Pokemon("Bisharp", null),
                new Pokemon("Clefable", null),
                new Pokemon("Dragapult", null),
                new Pokemon("Slowking", null),
                new Pokemon("Garchomp", null),
                new Pokemon("Corviknight", null));

        playerList.add(new Player(1,"Separation", new Team(pokemonListA)));
        playerList.add(new Player(2,"Serene Grace", new Team(pokemonListB)));
        battleMetaData.setPlayerList(playerList);
        return battleMetaData;
    }

}
