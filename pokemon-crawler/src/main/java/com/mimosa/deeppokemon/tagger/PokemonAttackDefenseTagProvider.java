package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.entity.PokemonInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * @program: deep-pokemon
 * @description: 根据宝可梦种族、特性、属性进行攻受向分类标签
 * @author: mimosa
 * @create: 2020//10//23
 */


public class PokemonAttackDefenseTagProvider implements PokemonTagProvider {


    @Override
    public void tag(PokemonInfo pokemonInfo) {

    }
}
