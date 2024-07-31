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

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "battle")
public class Battle implements Serializable {
    @Id
    private String battleID;
    // 简介
    private String info;
    // 对局日期
    private LocalDate date;
    // 对战玩家
    private List<String> players;
    // 胜方
    private String winner;
    // 排名
    private float avageRating;
    // 队伍
    private Team[] teams;
    // 总血线变化表，json字符串
    private String healthLinePairJsonString;
    // 比赛highlight表，json字符串
    private String highLightJsonString;
    // 对局记录
    private String log;
    //对局回合
    private int turnCount;
    //比赛类型
    private List<String> type;

    public Battle(Team[] teams) {
        this.teams = teams;
    }

    public Battle(String battleID, String info, LocalDate date, String winner, float avageRating, Team[] teams) {
        this.battleID = battleID;
        this.info = info;
        this.date = date;
        this.winner = winner;
        this.avageRating = avageRating;
        this.teams = teams;
    }

    public Battle(Team[] teams, LocalDate date, String winner, float avageRating) {
        this.teams = teams;
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

    public Team[] getTeams() {
        return teams;
    }

    public void setTeams(Team[] teams) {
        this.teams = teams;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

    public String getHealthLinePairJsonString() {
        return healthLinePairJsonString;
    }

    public void setHealthLinePairJsonString(String healthLinePairJsonString) {
        this.healthLinePairJsonString = healthLinePairJsonString;
    }

    public String getHighLightJsonString() {
        return highLightJsonString;
    }

    public void setHighLightJsonString(String highLightJsonString) {
        this.highLightJsonString = highLightJsonString;
    }

    public void setPokemonItem(int playIndex, String pokemonName, String item) {
        teams[playIndex].getPokemon(pokemonName).setItem(item);
    }

    public void setPokemonMove(int playerIndex, String pokemonName, String move) {
        teams[playerIndex].getPokemon(pokemonName).getMoves().add(move);
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
                "   info:" + String.format("%s  vs %s", teams[0].getPlayerName(), teams[1].getPlayerName()) + "\n" +
                "   battle id:" + battleID + "\n" +
                "   date:" + date + "\n" +
                "   winner:" + winner + "\n" +
                "   turn:" + turnCount + "\n" +
                "   avageRating:" + avageRating + "\n\n").replace(",", " ");
    }
}