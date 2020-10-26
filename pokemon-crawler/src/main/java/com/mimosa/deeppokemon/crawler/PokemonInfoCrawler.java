package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.PokemonInfo;

import java.io.IOException;
import java.util.List;

/**
 * @program: deep-pokemon
 * @description: 宝可梦数据爬取
 * @author: mimosa
 * @create: 2020//10//18
 */
public interface PokemonInfoCrawler {
    List<PokemonInfo> craw() throws IOException;
}
