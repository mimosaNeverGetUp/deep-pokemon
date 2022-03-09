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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 玩家登场事件处理器
 *
 * @author huangxiaocong(2070132549@qq.com)
 */
public class PlayerEventStringHandler implements BattleEventStringHandler {

    /**
     * 玩家登场事件正则匹配器
     * example:
     *      "|player|p1|Separation|sabrina|"
     */
    private static final Pattern playerPattern = Pattern.compile("\\|player\\|p([12])\\|([^|]*)\\|.*");

    @Override
    public void handle(String eventString, BattleMetaData battleMetaData) throws EventHandlerNotSupportException {
        Matcher matcher = playerPattern.matcher(eventString);
        if (!matcher.matches()) {
            throw new EventHandlerNotSupportException(String.format("eventString %s dont match pattern",eventString));
        }

        int number = Integer.parseInt(matcher.group(1));
        String name = matcher.group(2);
        setPlayerInfo(number, name, battleMetaData);
    }

    private void setPlayerInfo(int number, String name, BattleMetaData battleMetaData) {
        if (isPlayerExist(battleMetaData.getPlayerList(),number)) {
            Player player = battleMetaData.getPlayerList().get(number - 1);
            player.setName(name);
            player.setNumber(number);
        } else {
            Player player = new Player(number, name, null);
            battleMetaData.getPlayerList().add(number-1,player);
        }
    }

    private boolean isPlayerExist(List<Player> playerList,int number) {
        return playerList.size() >=number && playerList.get(number - 1) != null;
    }
}
