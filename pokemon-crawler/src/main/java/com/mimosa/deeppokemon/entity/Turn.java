package com.mimosa.deeppokemon.entity;

import java.util.List;

/**
 * @program: deep-pokemon
 * @description: 回合描述类
 * @author: mimosa
 * @create: 2021//05//31
 */
public class Turn {
    // 回合数
    protected Integer turnNumber;
    // 发生事件集
    protected List<TurnEvent> turnEvents;
    // 宝可梦状态集
    protected List<PokemonStatus> pokemonStatuses;

    public Integer getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(Integer turnNumber) {
        this.turnNumber = turnNumber;
    }

    public List<TurnEvent> getTurnEvents() {
        return turnEvents;
    }

    public void setTurnEvents(List<TurnEvent> turnEvents) {
        this.turnEvents = turnEvents;
    }

    public List<PokemonStatus> getPokemonStatuses() {
        return pokemonStatuses;
    }

    public void setPokemonStatuses(List<PokemonStatus> pokemonStatuses) {
        this.pokemonStatuses = pokemonStatuses;
    }
}
