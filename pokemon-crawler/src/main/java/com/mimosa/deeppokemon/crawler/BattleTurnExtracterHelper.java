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

import com.mimosa.deeppokemon.entity.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: deep-pokemon
 * @description: 爬取帮助类, 主流程与get等set辅助流程、辅助变量分离
 * @author: mimosa
 * @create: 2021//06//16
 */
public class BattleTurnExtracterHelper {
    private static final String format_damgeFromkey = "%d_%s_%s";
    private static final String SPLIT_CHAR = "_";
    int turnIndex;
    Battle battle;

    /**
     * 双方对局每回合实时状态记录
     */
    BattleTeamTurnStautsRecoder[] battleTeamTurnStautsRecoders = new BattleTeamTurnStautsRecoder[2];


    public BattleTurnExtracterHelper(Battle battle) {
        turnIndex = 0;
        this.battle = battle;
        int i = 0;
        for (Team team : battle.getTeams()) {
            BattleTeamTurnStautsRecoder battleTeamTurnStautsRecoder = new BattleTeamTurnStautsRecoder();
            for (Pokemon pokemon : team.getPokemons()) {
                battleTeamTurnStautsRecoder.setPresentHealthLine(pokemon.getName(), (short) 100);
            }
            battleTeamTurnStautsRecoders[i] = battleTeamTurnStautsRecoder;
            ++i;
        }
    }

    public int setHealthTrendAndReturnDiff(int turnIndex, int playerIndex, String moveName, short health) {
        int arrayIndex = turnIndex - 1;
        String pokemonName = battleTeamTurnStautsRecoders[playerIndex - 1].getPokemonName(moveName);
        battle.setHealthLineTrend(arrayIndex, playerIndex - 1, pokemonName, health);
        battleTeamTurnStautsRecoders[playerIndex - 1].setPresentHealthLine(pokemonName, health);
        return battleTeamTurnStautsRecoders[playerIndex - 1].getPresemtHealthLine(pokemonName) - health;
    }

    public void setSpaceTrend(int turnIndex, int playerIndex, String move, boolean exist) {
        int arrayPlayerIndex = playerIndex - 1;
        int arrayTurnIndex = turnIndex - 1;
        switch (move) {
            case "Stealth Rock":
                battle.getBattleTrend().getStealthRockTrend()[arrayPlayerIndex][arrayTurnIndex] = exist;
                break;
            case "Toxic Spikes":
                battle.getBattleTrend().getToxicSpikeTrend()[arrayPlayerIndex][arrayTurnIndex] = exist;
                break;
            case "Spikes":
                battle.getBattleTrend().getSpikeTrend()[arrayPlayerIndex][arrayTurnIndex] = exist;
        }
    }

    public void addSwitchCount(int playerIndex, String pokemonName) {
        PokemonBattleAnalysis pokemonAnalysis =
                battle.getTeamBattleAnalysis(playerIndex - 1).getPokemonBattleAnalysis(pokemonName);
        pokemonAnalysis.setSwitchCount(pokemonAnalysis.getSwitchCount() + 1);
    }


    public static int getOppentIndex(int playerIndex) {
        return 3 - playerIndex;
    }

    public void countPokemonDamage(int playerIndex, int healthDiff, String damageFrom, String damageOf,
                                   int ofPlayerIndex) {
        /*
         * 追踪伤害来源，并统计数据
         * 伤害来源pm伤害统计增加，当回合对面行动的pm正负值增加(个人意见，换入属于pm的一个招式之一)
         * 伤害目标方当回合行动的pm正负值减少
         */
        int oppentPlayerIndex = getOppentIndex(playerIndex);
        if (damageOf != null) {
            //根据damgeof直接生成
            String ofPokemonName = battleTeamTurnStautsRecoders[ofPlayerIndex - 1].getPokemonName(damageOf);
        } else if (damageFrom != null) {
            // 根据from计算
            damageOf = getDamgeOfPokemonKey(damageFrom);
            ofPlayerIndex = Integer.parseInt(damageOf.split(SPLIT_CHAR)[0]);
        } else {
            // 默认属于上下文技能造成，即对面当回合行动pm
            damageOf = battleTeamTurnStautsRecoders[oppentPlayerIndex - 1].getPresentPokemonName();
            ofPlayerIndex = oppentPlayerIndex;
        }

        if (ofPlayerIndex != playerIndex) {
            // 非队友造成伤害，进行统计
            PokemonBattleAnalysis damgeResourcePokemonAnalysis =
                    battle.getTeamBattleAnalysis(playerIndex - 1).getPokemonBattleAnalysis(damageOf);
            damgeResourcePokemonAnalysis.setEffectiveDamage(damgeResourcePokemonAnalysis.getEffectiveDamage() + healthDiff);
        }

        String oppentPresentpokemonName = battleTeamTurnStautsRecoders[oppentPlayerIndex - 1].getPresentPokemonName();
        PokemonBattleAnalysis oppentPresentMovePokemon =
                battle.getTeamBattleAnalysis(oppentPlayerIndex - 1).getPokemonBattleAnalysis(oppentPresentpokemonName);
        oppentPresentMovePokemon.setHealLineValue(oppentPresentMovePokemon.getHealLineValue() + healthDiff);

        String presentpokemonName = battleTeamTurnStautsRecoders[playerIndex - 1].getPresentPokemonName();
        PokemonBattleAnalysis presentMovePokemon =
                battle.getTeamBattleAnalysis(playerIndex - 1).getPokemonBattleAnalysis(presentpokemonName);
        presentMovePokemon.setHealLineValue(presentMovePokemon.getHealLineValue() - healthDiff);
    }

    public void countPokemonHeal(int playerIndex, int health) {
        int oppentPlayerIndex = getOppentIndex(playerIndex);

        String oppentPresentpokemonName = battleTeamTurnStautsRecoders[oppentPlayerIndex - 1].getPresentPokemonName();
        PokemonBattleAnalysis oppentPresentMovePokemon =
                battle.getTeamBattleAnalysis(oppentPlayerIndex - 1).getPokemonBattleAnalysis(oppentPresentpokemonName);
        oppentPresentMovePokemon.setHealLineValue(oppentPresentMovePokemon.getHealLineValue() - health);
        oppentPresentMovePokemon.setEffectiveDamage(oppentPresentMovePokemon.getHealLineValue() - health);


        String presentpokemonName = battleTeamTurnStautsRecoders[playerIndex - 1].getPresentPokemonName();
        PokemonBattleAnalysis presentMovePokemon =
                battle.getTeamBattleAnalysis(playerIndex - 1).getPokemonBattleAnalysis(presentpokemonName);
        presentMovePokemon.setHealLineValue(presentMovePokemon.getHealLineValue() + health);
    }

    /**
     * 根据回放伤害事件里的damage from计算对应的来源宝可梦
     *
     * @param damageFrom 回放文件脚本里的damage事件里的from对象
     *                   例子:“|-damage|p1a: Garchomp|82\/100 tox|[from] psn”
     *                   damageFrom为psn
     * @return String 伤害的来源宝可梦
     * @author huangxiaocong(779032284 @ qq.com)
     */
    public String getDamgeOfPokemonKey(String damageFrom) {
        // 计算剧毒来源
        // |-damage|p1a: Garchomp|82\/100 tox|[from] psn

        // 计算岩钉伤害来源
        // |-damage|p2a: Corviknight|88\/100|[from] Stealth Rock

        return null;
    }

    public void setMovePokemonName(int playerIndex, String moveName, String pokemonName) {
        battleTeamTurnStautsRecoders[playerIndex - 1].setMovePokemonName(moveName, pokemonName);
    }

    public void setPokemonItem(int playerIndex, String moveName, String item) {
        String pokemonName = battleTeamTurnStautsRecoders[playerIndex - 1].getPokemonName(moveName);
        battle.setPokemonItem(playerIndex - 1, pokemonName, item);
    }

    public void setPresentPokemon(int playerIndex, String pokemonName) {
        battleTeamTurnStautsRecoders[playerIndex - 1].setPresentPokemon(pokemonName);
        if (battleTeamTurnStautsRecoders[playerIndex - 1].isPresentPokemonTransofm()) {
            battleTeamTurnStautsRecoders[playerIndex - 1].setPresentPokemonTransofm(false);
        }
    }

    public void addMove(int playerIndex, String pokemonMoveName, String move) {
        if (battleTeamTurnStautsRecoders[playerIndex - 1].isPresentPokemonTransofm()) {
            String pokemonName = battleTeamTurnStautsRecoders[playerIndex - 1].getPresentPokemonName();
            battle.setPokemonMove(playerIndex - 1, pokemonName, move);
            if ("Transform".equals(move)) {
                battleTeamTurnStautsRecoders[playerIndex - 1].setPresentPokemonTransofm(true);
            }
        }
    }

    public void addMoveCount(int playerIndex, String pokemonMoveName) {
        String pokemonName = battleTeamTurnStautsRecoders[playerIndex - 1].getPokemonName(pokemonMoveName);
        PokemonBattleAnalysis pokemonAnalysis =
                battle.getTeamBattleAnalysis(playerIndex - 1).getPokemonBattleAnalysis(pokemonName);
        pokemonAnalysis.setMoveCount(pokemonAnalysis.getMoveCount() + 1);
    }

    public void setStatusTrend(int turnIndex, int playerIndex, String pokemonMoveName, String status) {
        int arrayIndex = turnIndex - 1;
        String pokemonName = battleTeamTurnStautsRecoders[playerIndex - 1].getPokemonName(pokemonMoveName);
        Short statusCode = Status.getcode(status);
        if (statusCode != null) {
            battle.setStatusTrend(arrayIndex, playerIndex - 1, pokemonName, Status.getcode(status));
        }
        // 记录来源
        if (!Status.HEALTH.getName().equals(status)) {

        }
    }
}