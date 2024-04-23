/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUtil {

    public static String groupMatch(Pattern pattern, String target, int group) {
        Matcher matcher = pattern.matcher(target);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return null;
    }
}