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
