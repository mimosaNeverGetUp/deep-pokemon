package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Tag;
import com.mimosa.deeppokemon.entity.Team;

import java.util.List;

/**
 * @program: deep-pokemon
 * @description: 队伍标签提供接口
 * @author: mimosa
 * @create: 2020//10//27
 */


public interface TeamTagProvider {
    void tag(Team team) ;
}
