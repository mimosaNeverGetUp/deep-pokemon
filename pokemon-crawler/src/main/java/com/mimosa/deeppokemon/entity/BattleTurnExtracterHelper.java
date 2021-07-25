package com.mimosa.deeppokemon.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: deep-pokemon
 * @description: 爬取帮助类,主流程与get等set辅助流程、辅助变量分离
 * @author: mimosa
 * @create: 2021//06//16
 */
public class BattleTurnExtracterHelper {
    boolean[] transofm;
    int index;
    Pokemon[] presentPokemons = new Pokemon[2];
    // key:宝可梦昵称 value:宝可梦名
    Map<String, String> movePokemonNameMap = new HashMap<>();;
    // key:宝可梦名 value：宝可梦类
    Map<String, Pokemon> pokemonMap = new HashMap<>();
    // 寻找伤害来源，包括异常状态、道具、特性、天气、钉子等伤害，用于统计伤害数据
    Map<String, String> damageFromPokemonMap = new HashMap<>();
    // key:宝可梦名 value：宝可梦统计
    Map<String, PokemonBattleAnalysis> pokemonBattleAnalysisMap = new HashMap<>();
    Battle battle;


    public BattleTurnExtracterHelper(Battle battle) {
        index = 0;
        this.battle = battle;
        for (Team team : battle.getTeams()) {
            int i = 1;
            for (Pokemon pokemon : team.getPokemons()) {
                pokemonMap.put(getMapKey(i, pokemon.getName()), pokemon);
            }
            ++i;
        }
        for (TeamBattleAnalysis teamBattleAnalysis : battle.getTeamBattleAnalysis()) {
            int i = 1;
            for (PokemonBattleAnalysis pokemonAnalysis : teamBattleAnalysis.getPokemonBattleAnalysisList()) {
                pokemonBattleAnalysisMap.put(getMapKey(i, pokemonAnalysis.getPokemonName()), pokemonAnalysis);
            }
            ++i;
        }
    }

    public void setHealthTrend(int turnIndex, int playerIndex, String moveName, short health) {
        int arrayIndex = turnIndex - 1;
        String pokemonKey = movePokemonNameMap.get(getMapKey(playerIndex, moveName));
        int order = battle.getBattleTrend().getPokemonOrder().get(pokemonKey);
        battle.getBattleTrend().getHealthLineTrend()[order][arrayIndex] = health;
    }

    public void setSpaceTrend(int turnIndex, int playerIndex, String move, boolean exist) {
        int arrayPlayerIndex = playerIndex - 1;
        int arrayTurnIndex = turnIndex - 1;
        switch (move) {
            case "Stealth Rock":
                battle.getBattleTrend().getStealthRockTrend()[arrayPlayerIndex][arrayTurnIndex]=exist;
                break;
            case "Toxic Spikes":
                battle.getBattleTrend().getToxicSpikeTrend()[arrayPlayerIndex][arrayTurnIndex]=exist;
                break;
            case "Spikes":
                battle.getBattleTrend().getSpikeTrend()[arrayPlayerIndex][arrayTurnIndex]=exist;
        }
    }

    public void addSwitchCount(int playerIndex, String pokemonName) {
        String key = getMapKey(playerIndex, pokemonName);
        PokemonBattleAnalysis pokemonAnalysis = pokemonBattleAnalysisMap.get(key);
        pokemonAnalysis.setMoveCount(pokemonAnalysis.getSwitchCount() + 1);
    }

    public String getMapKey(int playerIndex, String pokemonName) {
        return playerIndex + "_" + pokemonName;
    }

    public void setMovePokemonName(int playerIndex, String moveName, String pokemonName) {
        String key = getMapKey(playerIndex, moveName);
        movePokemonNameMap.put(key, getMapKey(playerIndex,pokemonName));
    }

    public void setPokemonItem(int playIndex, String moveName, String item) {
        String pokemonKey = movePokemonNameMap.get(getMapKey(playIndex, moveName));
        pokemonMap.get(pokemonKey).setItem(item);
    }

    public void setPresentPokemon(int playerIndex, String pokemonName) {
        String key = getMapKey(playerIndex, pokemonName);
        int arrayIndex = playerIndex - 1;
        presentPokemons[arrayIndex] = pokemonMap.get(key);
    }

    public void addMove(int playerIndex, String move) {
        int arrayIndex = playerIndex - 1;
        if (transofm[arrayIndex]) {
            presentPokemons[arrayIndex].getMoves().add(move);
            if ("Transform".equals(move)) {
                transofm[arrayIndex] = true;
            }
        }
    }
    public boolean[] getTransofm() {
        return transofm;
    }

    public void setTransofm(boolean[] transofm) {
        this.transofm = transofm;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Pokemon[] getPresentPokemons() {
        return presentPokemons;
    }

    public void setPresentPokemons(Pokemon[] presentPokemons) {
        this.presentPokemons = presentPokemons;
    }

    public Map<String, String> getMovePokemonNameMap() {
        return movePokemonNameMap;
    }

    public void setMovePokemonNameMap(Map<String, String> movePokemonNameMap) {
        this.movePokemonNameMap = movePokemonNameMap;
    }
}
