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

package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.entity.BaseStats;
import com.mimosa.deeppokemon.entity.PokemonBuildSet;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class PokemonStatsTagProvider implements PokemonTagProvider {
    //种族分类判断阈值（除hp外）
    private static final int THRESOLD_BAD = 85;
    private static final int THRESOLD_NROMAL = 100;
    private static final int THRESOLD_GOOD = 120;
    private static final int THRESOLD_EXCELLENT = 140;

    //双防种族分类判断阈值,对应的值为hp*def/spd的极限能力值相乘(暂定等级档 80*80 / 90*90/ 100*100 /105*105 /120*120)
    private static final float DEFENSETHRESOLD_BAD = (80 * 2 + 204) * 1.1F * (80 * 2 + 99);
    private static final float DEFENSETHRESOLD_NROMAL = (90 * 2 + 204) * 1.1F * (90 * 2 + 99);
    private static final float DEFENSETHRESOLD_GOOD = (105 * 2 + 204) * 1.1F * (105 * 2 + 99);
    private static final float DEFENSETHRESOLD_EXCELLENT = (120 * 2 + 204) * 1.1F * (120 * 2 + 99);

    @Override
    public void tag(PokemonInfo pokemonInfo, PokemonBuildSet pokemonBuildSet) {
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
        float enduranceDef = getEndurance(hp, def);
        if (enduranceDef < DEFENSETHRESOLD_BAD) {
            pokemonInfo.addTag(Tag.BAD_DEFENCESTATS);
        } else if (enduranceDef < DEFENSETHRESOLD_NROMAL) {
            pokemonInfo.addTag(Tag.NORMAL_DEFENCESTATS);
        } else if (enduranceDef < DEFENSETHRESOLD_GOOD) {
            pokemonInfo.addTag(Tag.GOOD_DEFENCESTATS);
        } else if (enduranceDef < DEFENSETHRESOLD_EXCELLENT) {
            pokemonInfo.addTag(Tag.EXCELLENT_DEFENCESTATS);
        } else {
            pokemonInfo.addTag(Tag.PRETTY_DEFENCESTATS);
        }
    }

    private void tagSpd(PokemonInfo pokemonInfo){
        BaseStats baseStats = pokemonInfo.getBaseStats();
        int hp = baseStats.getHp();
        int spd = baseStats.getSpd();
        float enduranceDef = getEndurance(hp, spd);
        if (enduranceDef < DEFENSETHRESOLD_BAD) {
            pokemonInfo.addTag(Tag.BAD_SPDSTATS);
        } else if (enduranceDef < DEFENSETHRESOLD_NROMAL) {
            pokemonInfo.addTag(Tag.NORMAL_SPDSTATS);
        } else if (enduranceDef < DEFENSETHRESOLD_GOOD) {
            pokemonInfo.addTag(Tag.GOOD_SPDSTATS);
        } else if (enduranceDef < DEFENSETHRESOLD_EXCELLENT) {
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