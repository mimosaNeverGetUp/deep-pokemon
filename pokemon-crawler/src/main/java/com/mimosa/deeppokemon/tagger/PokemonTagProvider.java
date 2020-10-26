package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.entity.PokemonInfo;

/**
 * @program: deep-pokemon
 * @description: 宝可梦分类功能
 * @author: mimosa
 * @create: 2020//10//23
 */

public interface PokemonTagProvider {

    public void tag(PokemonInfo pokemonInfo);
}
