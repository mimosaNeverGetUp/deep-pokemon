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

package com.mimosa.deeppokemon.refactor.crawler.eventhandler;

import com.mimosa.deeppokemon.refactor.entity.metadata.battle.BattleMetaData;
import com.mimosa.deeppokemon.refactor.entity.metadata.battle.Player;
import com.mimosa.deeppokemon.refactor.exception.EventHandlerNotSupportException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 玩家登场事件处理器测试
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class PlayerEventHandlerTest {

    private PlayerEventStringHandler playerEventStringHandler;
    private BattleMetaData battleMetaData;

    @Before
    public void setup() {
        playerEventStringHandler = new PlayerEventStringHandler();
        battleMetaData = new BattleMetaData();
    }

    @Test
    public void handlePlayerEventString() throws EventHandlerNotSupportException {
        String playerEventA = "|player|p1|Separation|sabrina|";
        String playerEventB = "|player|p2|Serene Grace|266|";
        playerEventStringHandler.handle(playerEventA, battleMetaData);
        playerEventStringHandler.handle(playerEventB, battleMetaData);

        assertListSizeEquals(battleMetaData.getPlayerList(),2);
        assertPlayerEqual(battleMetaData.getPlayerList().get(0),"Separation",1);
        assertPlayerEqual(battleMetaData.getPlayerList().get(1),"Serene Grace",2);
    }

    @Test
    public void handleNotSupportEventString() {
        String switchEvent = "|switch|p2a: Garchomp|Garchomp, F|100\\/100";
        try {
            playerEventStringHandler.handle(switchEvent, battleMetaData);
            Assert.fail();
        } catch (EventHandlerNotSupportException e) {
            e.printStackTrace();
            Assert.assertEquals(e.getMessage(),String.format("eventString %s dont match pattern",switchEvent));
        }
        assertListSizeEquals(battleMetaData.getPlayerList(), 0);
    }

    private void assertListSizeEquals(List<?> list, int size) {
        Assertions.assertNotNull(list);
        Assertions.assertEquals(list.size(),size);
    }

    private void assertPlayerEqual(Player player,String name,int number) {
        Assertions.assertNotNull(player);
        Assertions.assertEquals(number,player.getNumber());
        Assertions.assertEquals(name,player.getName());
    }


}
