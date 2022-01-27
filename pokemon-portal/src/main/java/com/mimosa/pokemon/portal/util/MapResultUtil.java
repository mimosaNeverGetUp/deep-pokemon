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
            } else {
                mapResult.getValue().setUseDiff(getUseRate(mapResult));
                mapResult.getValue().setWinDiff(getWinRate(mapResult));
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
