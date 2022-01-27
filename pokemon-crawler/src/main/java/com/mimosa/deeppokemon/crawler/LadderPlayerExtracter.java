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

import com.mimosa.deeppokemon.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LadderPlayerExtracter {
    private static final Logger logger = LoggerFactory.getLogger(LadderPlayerExtracter.class);
    public static ArrayList<Player> extract(String html, int RankMoreThan, int minElo, float minGxe, String format) {
        LocalDate today = LocalDate.now();
        Pattern pattern = Pattern.compile("<td>([0-9]{1,3})</td>[^<]*<td>([^<]*)</td>[^<]*<td><strong>([0-9]{4})</strong></td>[^<]*<td>([^<]*)<small>");
        Matcher matcher = pattern.matcher(html);
        ArrayList<Player> playerNames = new ArrayList<>();
        while (matcher.find()) {
            int rank = Integer.parseInt(matcher.group(1));
            String playerName = matcher.group(2);
            int elo = Integer.parseInt(matcher.group(3));
            float gxe = Float.parseFloat(matcher.group(4));
            logger.debug(String.format("match ladder %s , rank :%d , elo:%d gex:%f", playerName, rank, elo, gxe));
            if (elo < minElo || rank > RankMoreThan || gxe < minGxe) {
                continue;
            } else {
                playerNames.add(new Player(today, playerName, elo, rank, gxe, format));
            }
        }
        return playerNames;
    }
}
