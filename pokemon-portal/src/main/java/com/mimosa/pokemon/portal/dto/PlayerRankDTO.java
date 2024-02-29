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

package com.mimosa.pokemon.portal.dto;

import com.mimosa.deeppokemon.entity.Team;

import java.time.LocalDate;
import java.util.List;


public class PlayerRankDTO {
    private String name;
    private String format;
    private LocalDate infoDate;
    private int elo;
    private int rank;
    private float gxe;
    private List<Team> recentTeam;


    public PlayerRankDTO() {
    }

    public PlayerRankDTO(LocalDate infoDate, String name, int elo, int rank, float gxe, String format) {
        this.infoDate = infoDate;
        this.name = name;
        this.elo = elo;
        this.rank = rank;
        this.gxe = gxe;
        this.format = format;
    }

    public LocalDate getInfoDate() {
        return infoDate;
    }

    public void setInfoDate(LocalDate infoDate) {
        this.infoDate = infoDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public float getGxe() {
        return gxe;
    }

    public void setGxe(float gxe) {
        this.gxe = gxe;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<Team> getRecentTeam() {
        return recentTeam;
    }

    public void setRecentTeam(List<Team> recentTeam) {
        this.recentTeam = recentTeam;
    }
}
