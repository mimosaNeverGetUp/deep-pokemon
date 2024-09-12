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

import com.mimosa.deeppokemon.entity.stat.BattleStat;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "battle")
public class Battle implements Serializable {
    @Id
    protected String battleID;
    protected String format;
    // 简介
    protected String info;
    // 对局日期
    protected LocalDateTime date;
    // 对战玩家
    protected List<String> players;
    // 胜方
    protected String winner;
    // 排名
    protected float avageRating;
    // 对局记录
    protected String log;
    //对局回合
    protected int turnCount;
    //比赛类型
    protected List<String> type;

    // 队伍
    @Transient
    protected transient List<BattleTeam> battleTeams;

    @Transient
    protected transient BattleStat battleStat;

    public Battle(List<BattleTeam> battleTeams) {
        this.battleTeams = battleTeams;
    }

    public Battle(String battleID, String info, LocalDateTime date, String winner, float avageRating,
                  List<BattleTeam> battleTeams) {
        this.battleID = battleID;
        this.info = info;
        this.date = date;
        this.winner = winner;
        this.avageRating = avageRating;
        this.battleTeams = battleTeams;
    }

    public Battle(List<BattleTeam> battleTeams, LocalDateTime date, String winner, float avageRating) {
        this.battleTeams = battleTeams;
        this.date = date;
        this.winner = winner;
        this.avageRating = avageRating;
    }

    public Battle() {
    }

    public String getBattleID() {
        return battleID;
    }

    public void setBattleID(String battleID) {
        this.battleID = battleID;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<BattleTeam> getBattleTeams() {
        return battleTeams;
    }

    public void setBattleTeams(List<BattleTeam> battleTeams) {
        this.battleTeams = battleTeams;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public float getAvageRating() {
        return avageRating;
    }

    public void setAvageRating(float avageRating) {
        this.avageRating = avageRating;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public BattleStat getBattleStat() {
        return battleStat;
    }

    public void setBattleStat(BattleStat battleStat) {
        this.battleStat = battleStat;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Battle battle = (Battle) o;

        return battleID != null ? battleID.equals(battle.battleID) : battle.battleID == null;
    }

    @Override
    public int hashCode() {
        return battleID != null ? battleID.hashCode() : 0;
    }

    @Override
    public String toString() {
        return ("Battle:\n" +
                "   info:" + String.format("%s  vs %s", battleTeams.get(0).getPlayerName(),
                battleTeams.get(1).getPlayerName()) + "\n" +
                "   battle id:" + battleID + "\n" +
                "   date:" + date + "\n" +
                "   winner:" + winner + "\n" +
                "   turn:" + turnCount + "\n" +
                "   avageRating:" + avageRating + "\n\n").replace(",", " ");
    }
}