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

import com.mimosa.deeppokemon.entity.BaseStats;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Type;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * @program: deep-pokemon
 * @description: 宝可梦数据爬取, 创建时储存在内存里减少io但增加内存负担
 * @author: mimosa
 * @create: 2020//10//18
 */


@Component
public class PokemonInfoCrawlerImp implements PokemonInfoCrawler {

    private static final Logger logger = LoggerFactory.getLogger(PokemonInfoCrawlerImp.class);
    public static final String FORMES_PATTERN = "-";
    private final String dataPath = "META-INF/pokemoninfo.txt";
    private HashMap<String, PokemonInfo> infoHashMap = new HashMap<>(900);

    public PokemonInfoCrawlerImp() {
        try {
            List<PokemonInfo> pokemonInfos = craw();
            for (PokemonInfo pokemonInfo : pokemonInfos) {
                infoHashMap.put(pokemonInfo.getName(), pokemonInfo);
            }
        } catch (Exception e) {
            logger.info("宝可梦信息爬取创建失败", e);
        }
    }


    public PokemonInfo getPokemonInfo(Pokemon pokemon) {
        if (infoHashMap == null) {
            return null;
        }
        String name = pokemon.getName();
        //由于多形态而带后缀的名字消去后缀
        if (name.contains("-*")) {
            name = name.replace("-*", "");
        }
        PokemonInfo info = infoHashMap.get(name);
        if (info == null && name.contains(FORMES_PATTERN)) {
            info = infoHashMap.get(name.substring(0, name.indexOf(FORMES_PATTERN)));
        }
        if (info != null) {
            info.getTags().clear();//清理之前贴上的标签
        }
        return info;
    }

    @Override
    public List<PokemonInfo> craw() throws IOException {
        logger.info("start read pokemonInfo resource,path:[{}]", dataPath);
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(dataPath);
        byte[] dataByte = toByteArray(inputStream);
        String dataString = new String(dataByte);
        JSONObject jsonObject = new JSONObject(dataString);
        Iterator<String> iterator = jsonObject.keys();
        List<PokemonInfo> pokemonInfos = new ArrayList<>();
        while (iterator.hasNext()) {
            String id = iterator.next();
            PokemonInfo pokemonInfo = extractPokeInfo(id, jsonObject);
            pokemonInfos.add(pokemonInfo);
        }
        logger.info("pokemoninfo create and craw successfully, total {} pokemonInfo", pokemonInfos.size());
        return pokemonInfos;
    }

    private PokemonInfo extractPokeInfo(String id, JSONObject jsonObject) {
        JSONObject pokemonJson = jsonObject.getJSONObject(id);
        //提取名字和分级
        String name = pokemonJson.getString("name");
        Integer num = pokemonJson.getInt("num");
        String tier = "";
        if (pokemonJson.has("tier")) {
            tier = pokemonJson.getString("tier");

        }
        JSONArray tpyeNames = pokemonJson.getJSONArray("types");
        List<Type> types = new ArrayList<>();
        for (int i = 0; i < tpyeNames.length(); ++i) {
            //提取属性
            String type = tpyeNames.getString(i).toUpperCase();
            try {
                types.add(Type.valueOf(type));
            } catch (IllegalArgumentException e) {
                logger.error("{} is not in type list", type);
            }
        }
        //提取种族
        JSONObject baseStatsJson = pokemonJson.getJSONObject("baseStats");
        BaseStats baseStats = new BaseStats(baseStatsJson.getInt("hp"), baseStatsJson.getInt("atk"),
                baseStatsJson.getInt("def"), baseStatsJson.getInt("spd"), baseStatsJson.getInt("spa"),
                baseStatsJson.getInt("spe"));
        //提取特性
        List<String> abilities = new ArrayList<>();
        JSONObject abilitesJson = pokemonJson.getJSONObject("abilities");
        Iterator<String> iterator = abilitesJson.keys();
        while (iterator.hasNext()) {
            String abilitiesKey = iterator.next();
            abilities.add(abilitesJson.getString(abilitiesKey));
        }
        PokemonInfo pokemonInfo = new PokemonInfo(id, num, name, baseStats, types, tier, abilities);
        logger.debug("craw [{}] end,info is:[{}]", id, pokemonInfo);
        return pokemonInfo;
    }

    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}