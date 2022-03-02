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

package com.mimosa.deeppokemon.refactor.entity.metadata.battle;


import com.mimosa.deeppokemon.refactor.entity.metadata.MetaData;
import com.mimosa.deeppokemon.refactor.entity.metadata.battle.event.BattleEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 比赛元数据
 *
 * @author huangxiaocong(2070132549 @ qq.com)
 */
public class BattleMetaData extends MetaData {
    /**
     * 对局比赛id
     */
    private String battleID;

    /**
     * 对局日期
     */
    private LocalDate date;

    /**
     * 对局玩家平均分数
     */
    private float averageRating;

    /**
     * 胜利玩家名
     */
    private String winnerName;

    /**
     * 对局玩家信息
     */
    private List<Player> playerList = new ArrayList<>(2);

    private List<BattleEvent> battleEventList = new ArrayList<>();

    public BattleMetaData() {

    }

    public BattleMetaData(String battleID, LocalDate date, float averageRating, String winnerName) {
        this.battleID = battleID;
        this.date = date;
        this.averageRating = averageRating;
        this.winnerName = winnerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattleMetaData that = (BattleMetaData) o;
        return Float.compare(that.averageRating, averageRating) == 0 && Objects.equals(battleID, that.battleID)
                && Objects.equals(date, that.date) && Objects.equals(winnerName, that.winnerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(battleID, date, averageRating, winnerName);
    }

    public String getBattleID() {
        return battleID;
    }

    public void setBattleID(String battleID) {
        this.battleID = battleID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public List<BattleEvent> getBattleEventList() {
        return battleEventList;
    }

    public void setBattleEventList(List<BattleEvent> battleEventList) {
        this.battleEventList = battleEventList;
    }
}
