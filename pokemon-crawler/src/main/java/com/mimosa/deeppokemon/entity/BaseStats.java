package com.mimosa.deeppokemon.entity;

/**
 * @program: deep-pokemon
 * @description: 宝可梦种族值
 * @author: mimosa
 * @create: 2020//10//18
 */
public class BaseStats {
    int hp;
    int atk;
    int def;
    int spd;
    int spa;
    int spe;

    public BaseStats() {

    }

    @Override
    public String toString() {
        return "BaseStats{" +
                "hp=" + hp +
                ", atk=" + atk +
                ", def=" + def +
                ", spd=" + spd +
                ", spa=" + spa +
                ", spe=" + spe +
                '}';
    }

    public BaseStats(int hp, int atk, int def, int sdef, int satk, int spe) {
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.spd = sdef;
        this.spa = satk;
        this.spe = spe;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }

    public int getSpa() {
        return spa;
    }

    public void setSpa(int spa) {
        this.spa = spa;
    }

    public int getSpe() {
        return spe;
    }

    public void setSpe(int spe) {
        this.spe = spe;
    }
}
