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
import com.mimosa.deeppokemon.refactor.entity.metadata.battle.Player;
import com.mimosa.deeppokemon.refactor.entity.metadata.battle.Pokemon;
import com.mimosa.deeppokemon.refactor.entity.metadata.battle.Team;
import org.assertj.core.util.Strings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 比赛元数据爬取测试类
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class BattleMetaDataCralwerTest {
    private String battleDataString;
    private BattleMetaData exceptBattleMetaData;

    /**
     * 预设爬取数据以及预测爬取结果
     *
     * @author huangxiaocong(2070132549@qq.com)
     */
    @Before
    public void setup() throws IOException {
        File battleResource =
                new ClassPathResource("battlereplay/smogtours-gen8ou-560155(toxic,helmet,stealth)").getFile();
        battleDataString = new String(Files.readAllBytes(battleResource.toPath()));
        Assert.assertFalse(Strings.isNullOrEmpty(battleDataString));

        exceptBattleMetaData = new BattleMetaData("smogtours-gen8ou-560155",LocalDate.of(2021, 5, 9),
                0.0F,"Separation");

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
        playerList.add(new Player(2, "Serene Grace",new Team(pokemonListB)));

        exceptBattleMetaData.setPlayerList(playerList);
    }

    @Test
    public void crawBattleMetaData(){
        BattleMetaDataCrawler battleMetaDataCrawler = new BattleMetaDataCrawler();
        BattleMetaData battleMetaData = battleMetaDataCrawler.craw(null);
        Assert.assertEquals(exceptBattleMetaData,battleMetaData);
    }
}
