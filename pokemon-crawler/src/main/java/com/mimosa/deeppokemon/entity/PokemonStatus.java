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

package com.mimosa.deeppokemon.entity;

import java.util.List;
import java.util.Map;

/**
 * @program: deep-pokemon
 * @description: 对局中当前宝可梦状态
 * @author: mimosa
 * @create: 2021//06//01
 */
public class PokemonStatus {
    //名字
    protected String name;
    //操作玩家
    protected String playerName;
    // 血量
    protected Integer health;
    // 是否在场
    protected Boolean isPresent;
    // 异常状态
    protected String status;
    // 额外状态（混乱，鼓掌，束缚等）
    protected List<String> extraStatus;
    // 道具，未知：？，无道具：null
    protected String item;
    // 能力状态
    protected Map<String, Integer> levelStatusMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getExtraStatus() {
        return extraStatus;
    }

    public void setExtraStatus(List<String> extraStatus) {
        this.extraStatus = extraStatus;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Map<String, Integer> getLevelStatusMap() {
        return levelStatusMap;
    }

    public void setLevelStatusMap(Map<String, Integer> levelStatusMap) {
        this.levelStatusMap = levelStatusMap;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Boolean getPresent() {
        return isPresent;
    }

    public void setPresent(Boolean present) {
        isPresent = present;
    }
}
