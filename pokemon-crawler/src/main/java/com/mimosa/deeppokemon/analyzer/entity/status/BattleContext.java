/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer.entity.status;

import com.mimosa.deeppokemon.analyzer.entity.Field;
import com.mimosa.deeppokemon.analyzer.entity.Weather;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.stat.TurnEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BattleContext {
    private static final Logger log = LoggerFactory.getLogger(BattleContext.class);

    private List<PlayerStatus> playerStatusList;
    private int turn;
    private Weather weather;
    private Field field;
    private Battle battle;
    private List<TurnEvent> currentTurnImportanceEvents;

    public BattleContext(List<PlayerStatus> playerStatusList, Battle battle) {
        this.playerStatusList = playerStatusList;
        this.turn = 0;
        this.weather = null;
        this.field = null;
        this.battle = battle;
        this.currentTurnImportanceEvents = new ArrayList<>();
    }

    public List<PlayerStatus> getPlayerStatusList() {
        return playerStatusList;
    }

    public void setPlayerStatusList(List<PlayerStatus> playerStatusList) {
        this.playerStatusList = playerStatusList;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public List<TurnEvent> getCurrentTurnImportanceEvents() {
        return currentTurnImportanceEvents;
    }

    public void setCurrentTurnImportanceEvents(List<TurnEvent> currentTurnImportanceEvents) {
        this.currentTurnImportanceEvents = currentTurnImportanceEvents;
    }

    /**
     * 记录一条当前回合的重要对战事件，入该回合的累积队列。
     */
    public void addTurnEvent(TurnEvent turnEvent) {
        if (currentTurnImportanceEvents == null) {
            currentTurnImportanceEvents = new ArrayList<>();
        }
        currentTurnImportanceEvents.add(turnEvent);
    }

    public void changePokemonName(int playerNumber, String nameBefore, String changeName) {
        playerStatusList.get(playerNumber - 1).changePokemonName(nameBefore, changeName);
    }

    public void setPokemonItem(int playerNumber, String pokemonName, String item) {
        Pokemon pokemon = findPokemon(playerNumber, pokemonName);
        if (pokemon == null) return;

        if (pokemon.getItem() == null) {
            pokemon.setItem(item);
        }
    }

    public void setPokemonDetailChange(int playerNumber, String pokemonName, String detailChange) {
        Pokemon pokemon = findPokemon(playerNumber, pokemonName);
        if (pokemon == null) return;

        if (pokemon.getDetailChange() == null) {
            pokemon.setDetailChange(detailChange);
        }
    }

    public void setPokemonAbility(int playerNumber, String pokemonName, String ability) {
        Pokemon pokemon = findPokemon(playerNumber, pokemonName);
        if (pokemon == null) return;

        if (pokemon.getAbility() == null) {
            pokemon.setAbility(ability);
        }
    }

    public void setPokemonTeraType(int playerNumber, String pokemonName, String teraType) {
        Pokemon pokemon = findPokemon(playerNumber, pokemonName);
        if (pokemon == null) return;

        if (pokemon.getTeraType() == null) {
            pokemon.setTeraType(teraType);
        }
    }

    private Pokemon findPokemon(int playerNumber, String pokemonName) {
        if (battle == null || battle.getBattleTeams() == null || battle.getBattleTeams().size() < playerNumber
                || battle.getBattleTeams().get(playerNumber - 1) == null) {
            log.warn("battle {} is invalid", battle);
            return null;
        }

        Pokemon pokemon = battle.getBattleTeams().get(playerNumber - 1).findPokemon(pokemonName);
        if (pokemon == null) {
            log.warn("pokemon {} is not found in battle,try use blur pokemon name", pokemonName);
            String blurPokemonName = pokemonName + "-*";
            if (battle.getBattleTeams().get(playerNumber - 1).findPokemon(blurPokemonName) != null) {
                pokemon = battle.getBattleTeams().get(playerNumber - 1).findPokemon(blurPokemonName);
            } else {
                log.warn("blur pokemon {} is not found in battle", blurPokemonName);
                int formCharIndex = pokemonName.indexOf("-");
                if (formCharIndex != -1) {
                    blurPokemonName = pokemonName.substring(0, formCharIndex) + "-*";
                    log.info("try use another blur pokemon name {}", blurPokemonName);
                    pokemon = battle.getBattleTeams().get(playerNumber - 1).findPokemon(blurPokemonName);
                }
            }
        }
        return pokemon;
    }
}