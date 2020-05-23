package com.mimosa.deeppokemon.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Arrays;

@Document(collection = "battle")
public class Battle {
    @Id
    private String battleID;
    private String info;
    private LocalDate date;
    private String winner;
    private float avageRating;
    private Team[] teams;
    public Battle(Team[] teams) {
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

    @Override
    public String toString() {
        return ("Battle:\n" +
                "   info:" + String.format("%s  vs %s", teams[0].getPlayerName(), teams[1].getPlayerName()) +"\n"+
                "   battle id:" + battleID + "\n" +
                "   date:" + date + "\n" +
                "   winner:" + winner + "\n" +
                "   avageRating:" + avageRating + "\n\n" +
                 Arrays.toString(teams) +
                "\n\n").replaceAll(","," ");
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
}
