package com.mimosa.deeppokemon.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;

@Document(collection = "player")
public class Player {
    private String name;
    private String format;
    private LocalDate infoDate;
    private int elo;
    private int rank;
    private float gxe;


    public Player() {
    }

    public Player(LocalDate infoDate, String name, int elo, int rank, float gxe,String format) {
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

    @Override
    public String toString() {
        return "Player{" +
                "infoDate=" + infoDate +
                ", name='" + name + '\'' +
                ", elo=" + elo +
                ", rank=" + rank +
                ", gxe=" + gxe +
                '}'+"\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (name != null ? !name.equals(player.name) : player.name != null) return false;
        if (format != null ? !format.equals(player.format) : player.format != null) return false;
        return infoDate != null ? infoDate.equals(player.infoDate) : player.infoDate == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (infoDate != null ? infoDate.hashCode() : 0);
        return result;
    }
}
