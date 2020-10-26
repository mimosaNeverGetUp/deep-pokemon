package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.entity.BaseStats;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Tag;
import org.springframework.stereotype.Component;

/**
 * @program: deep-pokemon
 * @description: 宝可梦种族分类标签提供类
 * @author: mimosa
 * @create: 2020//10//23
 */


@Component
public class PokemonStatsTagProvider implements PokemonTagProvider {
    //种族分类判断阈值（除hp外）
    private static final int THRESOLD_BAD = 80;
    private static final int THRESOLD_NROMAL = 100;
    private static final int THRESOLD_GOOD = 120;
    private static final int THRESOLD_EXCELLENT = 140;

    //双防种族分类判断阈值,对应的值为hp*def/spd的极限能力值相乘(暂定等级档 80*80 / 90*90/ 100*100 /105*105 /120*120)
    private static final float DEFENSETHRESOLD_BAD = (80 * 2 + 204) * 1.1F * (80 * 2 + 99);
    private static final float DEFENSETHRESOLD_NROMAL = (90 * 2 + 204) * 1.1F * (90 * 2 + 99);
    private static final float DEFENSETHRESOLD_GOOD = (105 * 2 + 204) * 1.1F * (105 * 2 + 99);
    private static final float DEFENSETHRESOLD_EXCELLENT = (120 * 2 + 204) * 1.1F * (120 * 2 + 99);

    @Override
    public void tag(PokemonInfo pokemonInfo) {
        tagAtk(pokemonInfo);
        tagSatk(pokemonInfo);
        tagDef(pokemonInfo);
        tagSpd(pokemonInfo);
        tagSpe(pokemonInfo);
    }

    private float getEndurance(int hp,int def){
        return (hp * 2 + 204) * 1.1F * (def * 2 + 99);
    }

    private void tagDef(PokemonInfo pokemonInfo){
        BaseStats baseStats = pokemonInfo.getBaseStats();
        int hp = baseStats.getHp();
        int def = baseStats.getDef();
        float endurance_def = getEndurance(hp, def);
        if (endurance_def < DEFENSETHRESOLD_BAD) {
            pokemonInfo.addTag(Tag.BAD_DEFENCESTATS);
        } else if (endurance_def < DEFENSETHRESOLD_NROMAL) {
            pokemonInfo.addTag(Tag.NORMAL_DEFENCESTATS);
        } else if (endurance_def < DEFENSETHRESOLD_GOOD) {
            pokemonInfo.addTag(Tag.GOOD_DEFENCESTATS);
        } else if (endurance_def < DEFENSETHRESOLD_EXCELLENT) {
            pokemonInfo.addTag(Tag.EXCELLENT_DEFENCESTATS);
        } else {
            pokemonInfo.addTag(Tag.PRETTY_DEFENCESTATS);
        }
    }

    private void tagSpd(PokemonInfo pokemonInfo){
        BaseStats baseStats = pokemonInfo.getBaseStats();
        int hp = baseStats.getHp();
        int spd = baseStats.getSpd();
        float endurance_def = getEndurance(hp, spd);
        if (endurance_def < DEFENSETHRESOLD_BAD) {
            pokemonInfo.addTag(Tag.BAD_SPDSTATS);
        } else if (endurance_def < DEFENSETHRESOLD_NROMAL) {
            pokemonInfo.addTag(Tag.NORMAL_SPDSTATS);
        } else if (endurance_def < DEFENSETHRESOLD_GOOD) {
            pokemonInfo.addTag(Tag.GOOD_SPDSTATS);
        } else if (endurance_def < DEFENSETHRESOLD_EXCELLENT) {
            pokemonInfo.addTag(Tag.EXCELLENT_SPDSTATS);
        } else {
            pokemonInfo.addTag(Tag.PRETTY_SPDSTATS);
        }
    }

    private void tagAtk(PokemonInfo pokemonInfo){
        BaseStats baseStats = pokemonInfo.getBaseStats();
        int atk = baseStats.getAtk();
        if (atk < THRESOLD_BAD) {
            pokemonInfo.addTag(Tag.BAD_ATTACKSTATSS);
        } else if (atk < THRESOLD_NROMAL) {
            pokemonInfo.addTag(Tag.NORMAL_ATTACKSTATS);
        } else if (atk < THRESOLD_GOOD) {
            pokemonInfo.addTag(Tag.GOOD_ATTACKSTATS);
        } else if (atk < THRESOLD_EXCELLENT) {
            pokemonInfo.addTag(Tag.EXCELLENT_ATTACKSTATS);
        } else {
            pokemonInfo.addTag(Tag.PRETTY_ATTACKSTATS);
        }
    }

    private void tagSatk(PokemonInfo pokemonInfo){
        BaseStats baseStats = pokemonInfo.getBaseStats();
        int satk = baseStats.getSpa();
        if (satk < THRESOLD_BAD) {
            pokemonInfo.addTag(Tag.BAD_SPASTATS);
        } else if (satk < THRESOLD_NROMAL) {
            pokemonInfo.addTag(Tag.NORMAL_SPASTATS);
        } else if (satk < THRESOLD_GOOD) {
            pokemonInfo.addTag(Tag.GOOD_SPASTATS);
        } else if (satk < THRESOLD_EXCELLENT) {
            pokemonInfo.addTag(Tag.EXCELLENT_SPASTATS);
        } else {
            pokemonInfo.addTag(Tag.PRETTY_SPASTATS);
        }
    }

    private void tagSpe(PokemonInfo pokemonInfo){
        BaseStats baseStats = pokemonInfo.getBaseStats();
        int spe = baseStats.getSpe();
        if (spe < THRESOLD_BAD) {
            pokemonInfo.addTag(Tag.BAD_SPESTATS);
        } else if (spe < THRESOLD_NROMAL) {
            pokemonInfo.addTag(Tag.NORMAL_SPESTATS);
        } else if (spe < THRESOLD_GOOD) {
            pokemonInfo.addTag(Tag.GOOD_SPESTATS);
        } else if (spe < THRESOLD_EXCELLENT) {
            pokemonInfo.addTag(Tag.EXCELLENT_SPESTATS);
        } else {
            pokemonInfo.addTag(Tag.PRETTY_SPESTATS);
        }
    }
}
