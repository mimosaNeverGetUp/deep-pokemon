package com.mimosa.deeppokemon.crawler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerUrlExtracter {
    private static String replayUrlRoot = "https://replay.pokemonshowdown.com";
    public static ArrayList<String> extract(String html) {
        Pattern pattern = Pattern.compile("<li><a href=\"(.*)\" data-target");
        Matcher matcher = pattern.matcher(html);
        ArrayList<String> replayUrls = new ArrayList<>();
        while (matcher.find()) {
            System.out.println("match url:" + matcher.group(1));
            replayUrls.add(replayUrlRoot + matcher.group(1).trim());
        }
        return replayUrls;
    }
}
