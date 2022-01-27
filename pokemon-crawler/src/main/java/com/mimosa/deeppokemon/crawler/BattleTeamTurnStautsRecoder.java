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

package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.PokemonBattleAnalysis;

import java.util.HashMap;
import java.util.Map;

/**
 * 对局实时回合状态记录
 *
 * @author huangxiaocong(779032284@qq.com)
 */
public class BattleTeamTurnStautsRecoder {
    /**
     * 当前回合行动pm,即可点击招式的pm(换人也被广义地视为一种招式)
     */
    private PokemonBattleAnalysis[] presentMovePokemons = new PokemonBattleAnalysis[2];

    /**
     * 当前回合在场pm
     */
    private String presentPokemonName = null;

    /**
     * 当前回合在场pm是否处于变身状态
     */
    private boolean isPresentPokemonTransofm = false;

    /**
     * key:宝可梦名 value：当回合血线
     */
    private Map<String, Short> presentHealthLineMap = new HashMap<>();

    /**
     * key:宝可梦昵称 value:宝可梦名
     */
    private Map<String, String> movePokemonNameMap = new HashMap<>();;

    /**
     * 记录伤害来源，包括异常状态、道具、特性、天气、钉子等伤害，用于统计伤害数据
     * key:da
     */
    private Map<String, String> damageFromPokemonMap = new HashMap<>();

    public BattleTeamTurnStautsRecoder() {
    }

    public void setMovePokemonName(String moveName, String pokemonName) {
        movePokemonNameMap.put(moveName, pokemonName);
    }

    public String getPokemonName(String moveName) {
        return movePokemonNameMap.get(moveName);
    }

    public void setPresentHealthLine(String pokemonName, short healthLine) {
        presentHealthLineMap.put(pokemonName, healthLine);
    }

    public short getPresemtHealthLine(String pokemonName) {
        return presentHealthLineMap.get(pokemonName);
    }

    public void setPresentPokemon(String pokemonName) {
        presentPokemonName = pokemonName;
    }

    public String getPresentPokemonName() {
        return presentPokemonName;
    }

    public boolean isPresentPokemonTransofm() {
        return isPresentPokemonTransofm;
    }

    public void setPresentPokemonTransofm(boolean presentPokemonTransofm) {
        isPresentPokemonTransofm = presentPokemonTransofm;
    }
}
