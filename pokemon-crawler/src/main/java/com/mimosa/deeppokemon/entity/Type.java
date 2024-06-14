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

import java.util.ArrayList;
import java.util.List;

/**
 * @program: deep-pokemon
 * @description: 宝可梦属性，包含克制关系
 * @author: mimosa
 * @create: 2020//10//18
 */
public enum Type {
    NORMAL("NORMAL", "11111" + "1111U" + "11011" + "U00",
            "11111" + "11111" + "21111" + "111"),

    FIRE("FIRE", "1UU21" + "2211U" + "11111" + "2U1",
            "1U2U1" + "UU122" + "11111" + "U1U"),

    WATER("WATER", "12UU1" + "11122" + "11111" + "1U1",
            "1UU22" + "U1122" + "11111" + "U11"),

    GRASS("GRASS", "1U2U1" + "1UU22" + "111U1" + "UU1",
            "12UUU" + "222U2" + "11121" + "111"),

    ELECTRIC("ELECTRIC", "112UU" + "11U0U" + "11111" + "1U1",
            "1111U" + "11121" + "11111" + "U11"),

    ICE("ICE", "11U21" + "U1221" + "11111" + "U21",
            "12111" + "U1112" + "21111" + "211"),

    BUG("BUG", "1U121" + "1UUU1" + "U2UU2" + "U1U",
            "121U1" + "1U2U2" + "U1111" + "111"),

    FLYING("FLYING", "1112U" + "1211U" + "21111" + "U11",
            "111U2" + "2U102" + "U1111" + "111"),

    GROUND("GROUND", "111U2" + "1U012" + "11121" + "211",
            "11220" + "2111U" + "111U1" + "111"),

    ROCK("ROCK", "12111" + "222U1" + "U1111" + "U11",
            "UU221" + "11U21" + "211U1" + "211"),

    FIGHTING("FIGHTING", "21111" + "2UU12" + "1U0U2" + "121",
            "11111" + "1U21U" + "1211U" + "112"),

    PSYCHIC("PSYCHIC", "11111" + "11111" + "2U12U" + "U11",
            "11111" + "12111" + "UU2U2" + "111"),

    GHOST("GHOST", "01111" + "11111" + "1221U" + "111",
            "01111" + "1U111" + "012U2" + "111"),

    POISON("POISON", "11121" + "111UU" + "11UU1" + "012",
            "111U1" + "1U121" + "U21U1" + "11U"),

    DARK("DARK", "11111" + "11111" + "U221U" + "11U",
            "11111" + "12111" + "20U1U" + "112"),

    STEEL("STEEL", "1UU1U" + "21112" + "11111" + "U12",
            "U21U1" + "UUU2U" + "2U101" + "UUU"),

    DRAGON("DRAGON", "11111" + "11111" + "11111" + "U20",
            "1UUUU" + "21111" + "11111" + "122"),

    FAIRY("FAIRY", "1U111" + "11111" + "211U2" + "U21",
            "11111" + "1U111" + "U112U" + "201");


    //一般、火、水、草、电、   冰、虫、飞行、地面、岩石、   格斗、超能力、幽灵、毒、恶、   钢、龙、妖精
    public static final List<String> TYPEORDER;

    static {
        TYPEORDER = new ArrayList<>(18);
        TYPEORDER.add("NORMAL");
        TYPEORDER.add("FIRE");
        TYPEORDER.add("WATER");
        TYPEORDER.add("GRASS");
        TYPEORDER.add("ELECTRIC");
        TYPEORDER.add("ICE");
        TYPEORDER.add("BUG");
        TYPEORDER.add("FLYING");
        TYPEORDER.add("GROUND");
        TYPEORDER.add("ROCK");
        TYPEORDER.add("FIGHTING");
        TYPEORDER.add("PSYCHIC");
        TYPEORDER.add("GHOST");
        TYPEORDER.add("POISON");
        TYPEORDER.add("DARK");
        TYPEORDER.add("STEEL");
        TYPEORDER.add("DRAGON");
        TYPEORDER.add("FAIRY");
    }

    private String name;
    //属性进攻克制bit字符串，位数组对应应位置为克制修正*（1:1倍 2:2倍  0：无效果 U:0.5倍 ）
    private String effectiveType_attack_bitString;
    //属性防御抵抗bit字符串，位数组对应应位置为克制修正*（1:1倍 2:2倍 0：无效果 U:0.5倍）
    private String effectiveType_defense_bitString;


    private Type(String name, String effectiveType_attack_bitString, String effectiveType_defense_bitString) {
        this.name = name;
        this.effectiveType_attack_bitString = effectiveType_attack_bitString;
        this.effectiveType_defense_bitString = effectiveType_defense_bitString;
    }

    public static List<Float> getResistanceRate(PokemonInfo pokemonInfo) {
        List<Type> types = pokemonInfo.getTypes();
        List<Float> resistanceRates = new ArrayList<>(18);
        //单属性，直接转换
        if (types.size() == 1) {
            Type type = types.get(0);
            String s = type.effectiveType_defense_bitString;
            for (int j = 0; j < s.length(); ++j) {
                if (s.charAt(j) == 'U') {
                    resistanceRates.add(0.5F);
                } else {
                    resistanceRates.add(Float.parseFloat(String.valueOf(s.charAt(j))));
                }
            }
            return resistanceRates;
        }
        //双属性，属性修正相乘
        Type firstType = types.get(0);
        Type secondType = types.get(1);
        for (int i = 0; i < firstType.effectiveType_defense_bitString.length(); ++i) {
            char firstResistanceRateChar = firstType.effectiveType_defense_bitString.charAt(i);
            char secondResistanceRateChar = secondType.effectiveType_defense_bitString.charAt(i);
            //“U”对应0.5
            float firstResistanceRate;
            float secondResistanceRate;
            if (firstResistanceRateChar == 'U') {
                firstResistanceRate = 0.5f;
            } else {
                firstResistanceRate = Float.parseFloat(String.valueOf(firstResistanceRateChar));
            }
            if (secondResistanceRateChar == 'U') {
                secondResistanceRate = 0.5f;
            } else {
                secondResistanceRate = Float.parseFloat(String.valueOf(secondResistanceRateChar));
            }
            resistanceRates.add(firstResistanceRate * secondResistanceRate);
        }
        return resistanceRates;
    }
}