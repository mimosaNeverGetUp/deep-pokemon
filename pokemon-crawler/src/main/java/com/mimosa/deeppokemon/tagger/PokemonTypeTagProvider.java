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

import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Tag;
import com.mimosa.deeppokemon.entity.Type;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @program: deep-pokemon
 * @description: 宝可梦属性分类标签类
 * @author: mimosa
 * @create: 2020//10//23
 */

@Component
public class PokemonTypeTagProvider implements PokemonTagProvider {
    private static final float superReistanceValueRate = 1.5f; //4倍抵抗或免疫的价值

    //属性抵抗价值标签判断阈值
    private static final float THRESOLD_BAD = 4.5f;
    private static final float THRESOLD_NROMAL = 6.5f;
    private static final float THRESOLD_GOOD = 8.5f;
    private static final float THRESOLD_EXCELLENT = 10.5f;

    private static final float THRESOLD_WEAK_LITTLE = -4.5f;
    private static final float THRESOLD_WEAK_NORMAL = -6.75f;

    @Override
    public void tag(PokemonInfo pokemonInfo) {
        List<Float> reistanceRates = Type.getResistanceRate(pokemonInfo);
        float totalValue = 0; //抵抗价值
        float totalWeakValue =0;//弱点负价值
        for (int i = 0; i < reistanceRates.size(); i++) {
            Float f =  reistanceRates.get(i);
            Type reistanceType = Type.valueOf(Type.TYPEORDER.get(i));//数组对应属性抵抗是按TYPEORDER的位置给的
            if (f < 1) {
                float reistanceValue = getValueOfReistance(reistanceType);
                if (f < 0.5) {
                    totalValue += reistanceValue * superReistanceValueRate;
                } else {
                    totalValue += reistanceValue;
                }
            } else if (f > 1) {
                float weakValue = getValueOfReistance(reistanceType);
                if (f > 2) {
                    totalWeakValue -= superReistanceValueRate * weakValue;
                } else {
                    totalWeakValue -= weakValue;
                }
            }
        }
        //贴抵抗标签
        if (totalValue < THRESOLD_BAD) {
            pokemonInfo.addTag(Tag.TYPE_BAD);

        } else if (totalValue <= THRESOLD_NROMAL) {
            pokemonInfo.addTag(Tag.TYPE_NORMAL);
        } else if (totalValue <= THRESOLD_GOOD) {
            pokemonInfo.addTag(Tag.TYPE_GOOD);
        } else if (totalValue <= THRESOLD_EXCELLENT) {
            pokemonInfo.addTag(Tag.TYPE_EXCELLENT);
        } else {
            pokemonInfo.addTag(Tag.TYPE_PRETTY);//抵抗价值大于10
        }

        //贴弱点标签
        if (totalWeakValue > THRESOLD_WEAK_LITTLE) {
            pokemonInfo.addTag(Tag.TYPE_LITTLEWEAK);
        } else if (totalWeakValue > THRESOLD_WEAK_NORMAL) {
            pokemonInfo.addTag(Tag.TYPE_NORMALWEAK);
        } else {
            pokemonInfo.addTag(Tag.TYPE_MANYWEAK);
        }

    }

    //属性抵抗价值
    private float getValueOfReistance(Type type) {
        switch (type) {
            case BUG:
            case STEEL:
            case POISON:
            case NORMAL:
                return 0.75f; //虫、钢、毒、一般 抵抗价值小
            case ROCK:
            case GRASS:
            case FLYING:
            case FIGHTING:
            case PSYCHIC:
            case FAIRY:
                return 1.0f; //草  岩石 超能 格斗 飞行 仙 抵抗价值一般
            case FIRE:
            case ICE:
            case GHOST:
            case DARK:
            case WATER:
            case DRAGON:
                return 1.25f;//鬼 恶 水 龙 冰 火 抵抗价值好
            default:
                return 1.5f;//剩余的电 地 抵抗价值重要
        }
    }
}