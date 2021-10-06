package com.mimosa.deeppokemon.util;

import com.mimosa.deeppokemon.entity.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: deep-pokemon
 * @description: 爬取帮助类,主流程与get等set辅助流程、辅助变量分离
 * @author: mimosa
 * @create: 2021//06//16
 */
public class BattleTurnExtracterHelper {
    final static String format_damgeFromkey = "%d_%s_%s";
    final private String SPLIT_CHAR = "_";
    boolean[] transofm;
    int index;
    // 当前回合行动pm,即可点击招式的pm(换人也被广义地视为一种招式)
    PokemonBattleAnalysis[] presentMovePokemons = new PokemonBattleAnalysis[2];

    // 当前时刻在场pm
    Pokemon[] presentPokemons = new Pokemon[2];

    // key:宝可梦名 value：当回合血线
    Map<String, Short> presentHealthLineMap = new HashMap<>();

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
                String key = getMapKey(i, pokemon.getName());
                pokemonMap.put(key, pokemon);
                presentHealthLineMap.put(key, (short) 100);
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

    public int setHealthTrendAndReturnDiff(int turnIndex, int playerIndex, String moveName, short health) {
        int arrayIndex = turnIndex - 1;
        String pokemonKey = movePokemonNameMap.get(getMapKey(playerIndex, moveName));
        int order = battle.getBattleTrend().getPokemonOrder().get(pokemonKey);
        battle.getBattleTrend().getHealthLineTrend()[order][arrayIndex] = health;
        int diff =presentHealthLineMap.get(pokemonKey) - health;
        presentHealthLineMap.put(pokemonKey, health);
        return diff;
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
            String ofPokemonName = movePokemonNameMap.get(getMapKey(ofPlayerIndex, damageOf));
            damageOf = getMapKey(ofPlayerIndex, ofPokemonName);
        } else if (damageFrom != null) {
            // 根据from计算
            damageOf = getDamgeOfPokemonKey(damageFrom);
            ofPlayerIndex = Integer.parseInt(damageOf.split(SPLIT_CHAR)[0]);
        } else {
            // 默认属于上下文技能造成，即对面当回合行动pm
            damageOf = getMapKey(oppentPlayerIndex, presentMovePokemons[oppentPlayerIndex - 1].getPokemonName());
            ofPlayerIndex = oppentPlayerIndex;
        }

        if (ofPlayerIndex != playerIndex) {
            // 非队友造成伤害，进行统计
            PokemonBattleAnalysis damgeResourcePokemonAnalysis = pokemonBattleAnalysisMap.get(damageOf);
            damgeResourcePokemonAnalysis.setEffectiveDamage(damgeResourcePokemonAnalysis.getEffectiveDamage() + healthDiff);
        }

        PokemonBattleAnalysis oppentPresentMovePokemon = presentMovePokemons[oppentPlayerIndex - 1];
        oppentPresentMovePokemon.setHealLineValue(oppentPresentMovePokemon.getHealLineValue() + healthDiff);

        PokemonBattleAnalysis presentMovePokemon = presentMovePokemons[playerIndex - 1];
        presentMovePokemon.setHealLineValue(presentMovePokemon.getHealLineValue() - healthDiff);
    }

    public void countPokemonHeal(int playerIndex, int health) {
        int oppentPlayerIndex = getOppentIndex(playerIndex);

        PokemonBattleAnalysis oppentPresentMovePokemon = presentMovePokemons[oppentPlayerIndex - 1];
        oppentPresentMovePokemon.setHealLineValue(oppentPresentMovePokemon.getHealLineValue() - health);
        oppentPresentMovePokemon.setEffectiveDamage(oppentPresentMovePokemon.getHealLineValue() - health);


        PokemonBattleAnalysis presentMovePokemon = presentMovePokemons[playerIndex - 1];
        presentMovePokemon.setHealLineValue(presentMovePokemon.getHealLineValue() + health);
    }



    public String getDamgeOfPokemonKey(String damge) {
        return null;
    }

    public String getMapKey(int playerIndex, String pokemonName) {
        return playerIndex + SPLIT_CHAR + pokemonName;
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
        if (transofm[arrayIndex]) {
            transofm[arrayIndex] = false;
        }
    }

    public void addMove(int playerIndex, String pokemonMoveName, String move) {
        int arrayIndex = playerIndex - 1;
        if (transofm[arrayIndex]) {
            presentPokemons[arrayIndex].getMoves().add(move);
            if ("Transform".equals(move)) {
                transofm[arrayIndex] = true;
            }
        }
    }

    public void addMoveCount(int playerIndex, String pokemonMoveName) {
        String key = movePokemonNameMap.get(getMapKey(playerIndex, pokemonMoveName));
        PokemonBattleAnalysis pokemonAnalysis = pokemonBattleAnalysisMap.get(key);
        pokemonAnalysis.setMoveCount(pokemonAnalysis.getMoveCount() + 1);
    }

    public void setStatusTrend(int turnIndex, int playerIndex, String pokemonMoveName, String status) {
        int arrayIndex = turnIndex - 1;
        String pokemonKey = movePokemonNameMap.get(getMapKey(playerIndex, pokemonMoveName));
        int order = battle.getBattleTrend().getPokemonOrder().get(pokemonKey);
        battle.getBattleTrend().getStatusLineTrend()[order][arrayIndex] = Status.getcode(status);
        // 记录来源
        if (!Status.HEALTH.getName().equals(status)) {

        }
    }

    public void afterTurn() {
        for (int i = 0; i < presentPokemons.length; ++i) {
            String pokemonName = presentPokemons[i].getName();
            int playerIndex = i + 1;
            String key = getMapKey(playerIndex, pokemonName);
            presentMovePokemons[i] = pokemonBattleAnalysisMap.get(key);
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
