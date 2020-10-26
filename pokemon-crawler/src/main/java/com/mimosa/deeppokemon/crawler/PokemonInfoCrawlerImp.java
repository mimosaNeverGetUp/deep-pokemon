package com.mimosa.deeppokemon.crawler;
import com.mimosa.deeppokemon.entity.BaseStats;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Type;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @program: deep-pokemon
 * @description: 宝可梦数据爬取
 * @author: mimosa
 * @create: 2020//10//18
 */


@Component
public class PokemonInfoCrawlerImp implements PokemonInfoCrawler {
    private final String dataPath = "src/main/resources/META-INF/pokemonInfo.txt";
    @Override
    public List<PokemonInfo> craw() throws IOException {
        byte[] dataByte = Files.readAllBytes(new File(dataPath).toPath());
        String dataString = new String(dataByte);
        JSONObject jsonObject = new JSONObject(dataString);
        Iterator<String> iterator = jsonObject.keys();
        List<PokemonInfo> pokemonInfos = new ArrayList<>();
        while (iterator.hasNext()) {
            String pokemonName = iterator.next();
            PokemonInfo pokemonInfo = extractPokeInfo(pokemonName, jsonObject);
            pokemonInfos.add(pokemonInfo);
        }
        return pokemonInfos;
    }

    private PokemonInfo extractPokeInfo(String pokemonName, JSONObject jsonObject) {

        JSONObject pokemonJson =jsonObject.getJSONObject(pokemonName);
        //提取名字和分级
        String name = pokemonJson.getString("name");
        String tier = "";
        if(pokemonJson.has("tier")){
            tier = pokemonJson.getString("tier");

        }
        JSONArray tpyeNames = pokemonJson.getJSONArray("types");
        List<Type> types = new ArrayList<>();
        for (int i = 0; i < tpyeNames.length(); ++i) {
            //提取属性
            String type = tpyeNames.getString(i).toUpperCase();
            types.add(Type.valueOf(type));
        }
        //提取种族
        JSONObject baseStatsJson = pokemonJson.getJSONObject("baseStats");
        BaseStats baseStats =new BaseStats(baseStatsJson.getInt("hp"),baseStatsJson.getInt("atk"),
                baseStatsJson.getInt("def"),baseStatsJson.getInt("spd"),baseStatsJson.getInt("spa"),
                baseStatsJson.getInt("spe"));
        //提取特性
        List<String> abilities = new ArrayList<>();
        JSONObject abilitesJson = pokemonJson.getJSONObject("abilities");
        Iterator<String> iterator = abilitesJson.keys();
        while (iterator.hasNext()) {
            String abilitiesKey = iterator.next();
            abilities.add(abilitesJson.getString(abilitiesKey));
        }
        PokemonInfo pokemonInfo = new PokemonInfo(baseStats, types, tier, name, abilities);
        return pokemonInfo;
    }
}
