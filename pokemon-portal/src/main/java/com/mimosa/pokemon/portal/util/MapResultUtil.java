package com.mimosa.pokemon.portal.util;

import com.mimosa.pokemon.portal.entity.MapResult;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

/**
 * @program: deep-pokemon
 * @description: mapruesult相关工具类
 * @author: mimosa
 * @create: 2020//10//25
 */
public class MapResultUtil
{
    public static void compareStatistic(List<MapResult> origin, List<MapResult> other) {
        HashMap<String, MapResult> mapResultHashMap = new HashMap<>(other.size());
        //放入hashmap减少查询时间复杂度
        for (MapResult result : other) {
            mapResultHashMap.put(result.get_id(), result);
        }

        for (MapResult mapResult : origin) {
            MapResult compare=mapResultHashMap.get(mapResult.get_id());
            if (compare != null) {
                float useDiff = getUseRate(mapResult) - getUseRate(compare);
                mapResult.getValue().setUseDiff(useDiff);
                float winDiff = getWinRate(mapResult) - getWinRate(compare);
                mapResult.getValue().setWinDiff(winDiff);
            }
        }
    }


    public static float getUseRate(MapResult mapResult) {
        return mapResult.getValue().getUse() / mapResult.getValue().getTotal();
    }

    public static float getWinRate(MapResult mapResult) {
        return  mapResult.getValue().getWin() / mapResult.getValue().getUse();
    }
}
