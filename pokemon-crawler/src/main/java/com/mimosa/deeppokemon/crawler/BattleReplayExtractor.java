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

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.BattleReplayData;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.deeppokemon.tagger.TeamTagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BattleReplayExtractor {
    private static final Logger logger = LoggerFactory.getLogger(BattleReplayExtractor.class);
    @Autowired
    private TeamTagger teamTagger;

    public Battle extract(BattleReplayData battleReplayData) {
        logger.debug("extract Team start");

        String tier = battleReplayData.formatId();
        Team[] teams = extractTeam(battleReplayData.log());
        //贴标签
        for (Team team : teams) {
            teamTagger.tagTeam(team);
        }

        teams[0].setPlayerName(battleReplayData.players().get(0));
        teams[1].setPlayerName(battleReplayData.players().get(1));
        teams[0].setTier(tier);
        teams[1].setTier(tier);
        logger.debug("extract end");
        LocalDate date = LocalDate.ofInstant(Instant.ofEpochSecond(battleReplayData.uploadTime()), ZoneId.systemDefault()) ;
        String winner = extractWinner(battleReplayData.log());
        Integer avageRating = battleReplayData.rating();
        Battle battle = new Battle(teams, date, winner, avageRating);
        battle.setBattleID(battleReplayData.id());
        battle.setLog(battleReplayData.log());
        extractBattleTurn(battleReplayData.log(), battle);
        logger.debug("extract battle: {}", battle);
        return battle;
    }

    private static Team[] extractTeam(String html) {
        Pattern pattern = Pattern.compile("\\|poke\\|p([12])\\|([^//|,]*)[\\|,]");
        Matcher matcher = pattern.matcher(html);
        ArrayList<Pokemon> pokemons1 = new ArrayList<Pokemon>(6);
        ArrayList<Pokemon> pokemons2 = new ArrayList<Pokemon>(6);
        while (matcher.find()) {
            if (matcher.group(1).equals("1")) {
                String pokemonName = matcher.group(2).trim();
                logger.debug("match p1 Pokemon:" + pokemonName);
                Pokemon pokemon = extractPokemon(html, pokemonName, 1);
                pokemons1.add(pokemon);
            } else {
                String pokemonName = matcher.group(2).trim();
                logger.debug("match p2 Pokemon:" + pokemonName);
                Pokemon pokemon = extractPokemon(html, pokemonName, 2);
                pokemons2.add(pokemon);
            }
        }
        if (pokemons1.size() == 0 && pokemons2.size() == 0) {
            throw new RuntimeException("A Team match failed");
        }
        Team team1 = new Team(pokemons1);
        Team team2 = new Team(pokemons2);
        Team[] teams = new Team[2];
        teams[0] = team1;
        teams[1] = team2;
        return teams;
    }

    private static Pokemon extractPokemon(String html, String pokemonName, int playerNumber) {
        Pokemon pokemon = new Pokemon(pokemonName);
        String pokemonMoveName = extractMoveName(html, pokemonName, playerNumber);
        if ("Ditto".equals(pokemonName)) {
            pokemon.setMoves(new HashSet<>(Collections.singletonList("Transform")));
        } else {
            String regex = String.format("move\\|p%da: %s\\|([^\\|]*)\\|", playerNumber, Pattern.quote(pokemonMoveName));
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(html);
            HashSet<String> moves = new HashSet<>(4);
            while (matcher.find()) {
                if (moves.add(matcher.group(1))) {
                    logger.debug(String.format("match %s move:%s", pokemonName, matcher.group(1)));
                }
            }
            pokemon.setMoves(moves);
        }
        String item = extractPokemonItem(html, pokemonMoveName, playerNumber);
        logger.debug("match item:" + item);
        pokemon.setItem(item);
        return pokemon;
    }

    private static String extractWinner(String html) {
        Pattern pattern = Pattern.compile("win\\|(.*)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String winPlayerName = matcher.group(1).trim();
            logger.debug("match winner:" + winPlayerName);
            return winPlayerName;
        }
        throw new RuntimeException("match battle win relations failed");
    }

    private static String extractMoveName(String html, String pokemonName, int playerNumber) {
        String regex = String.format("switch\\|p%da: ([^\\|]*)\\|%s", playerNumber, pokemonName);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String pokemonMoveName = matcher.group(1).trim();
            logger.debug("match Move Name:" + pokemonMoveName);
            return pokemonMoveName;
        }
        return pokemonName;
    }

    private static String extractPokemonItem(String html, String pokemonMoveName, int playerNumber) {
        //trick will disturb other check ,so should check first
        String item = extractTrickItem(html, pokemonMoveName, playerNumber);
        if (item != null) {
            return item;
        }

        String regex = String.format("p%da: %s\\|.*item: (.*)", playerNumber, Pattern.quote(pokemonMoveName));
        Pattern itemPattern = Pattern.compile(regex);
        Matcher itemMatcher = itemPattern.matcher(html);
        while (itemMatcher.find()) {
            //check it have RockyHelmet because RockyHelmet belong oppent
            item = itemMatcher.group(1).trim();
            if (!containRockyHelmet(item)) {
                return item;
            }
        }
        //check another pattern item dont match pre
        item = extractEndItem(html, pokemonMoveName, playerNumber);
        if (item != null) {
            return item;
        }

        item = extractRockyHelmetItem(html, pokemonMoveName, playerNumber);
        if (item != null) {
            return item;
        }

        item = extractMegaItem(html, pokemonMoveName, playerNumber);
        if (item != null) {
            return item;
        }

        item = extractZMovetItem(html, pokemonMoveName, playerNumber);
        if (item != null) {
            return item;
        }

        return null;
    }

    private static boolean containRockyHelmet(String item) {
        return item.contains("Rocky Helmet");
    }

    private static String extractRockyHelmetItem(String html, String pokemonMoveName, int playerNumber) {
        String regex = String.format("item: Rocky Helmet\\|\\[of\\] p%da: %s", playerNumber, Pattern.quote(pokemonMoveName));
        Pattern itemPattern = Pattern.compile(regex);
        Matcher itemMatcher = itemPattern.matcher(html);
        if (itemMatcher.find()) {
            return "RockyHelmet";
        }
        return null;
    }

    private static String extractZMovetItem(String html, String pokemonMoveName, int playerNumber) {
        String regex = String.format("\\-zpower\\|p%da: %s", playerNumber, Pattern.quote(pokemonMoveName));
        Pattern itemPattern = Pattern.compile(regex);
        Matcher itemMatcher = itemPattern.matcher(html);
        if (itemMatcher.find()) {
            return "Z-Crystal";
        }
        return null;
    }

    private static String extractMegaItem(String html, String pokemonMoveName, int playerNumber) {
        String regex = String.format(new String("\\-mega\\|p%da: %s\\|[^\\|]*\\|([^\\|]*)"), playerNumber, Pattern.quote(pokemonMoveName));
        Pattern itemPattern = Pattern.compile(regex);
        Matcher itemMatcher = itemPattern.matcher(html);
        if (itemMatcher.find()) {
            return itemMatcher.group(1).trim();
        }
        return null;
    }

    private static String extractEndItem(String html, String pokemonMoveName, int playerNumber) {
        String regex = String.format(new String("\\-enditem\\|p%da: %s\\|([^\\|]*)\\|"), playerNumber, Pattern.quote(pokemonMoveName));
        Pattern itemPattern = Pattern.compile(regex);
        Matcher itemMatcher = itemPattern.matcher(html);
        if (itemMatcher.find()) {
            return itemMatcher.group(1).trim();
        }
        return null;
    }

    private static String extractTrickItem(String html, String pokemonMoveName, int playerNumber) {
        String target = String.format("p%da: %s", playerNumber, Pattern.quote(pokemonMoveName));
        String regexA = String.format("\\-activate\\|([^\\|]*)\\|move: Trick\\|\\[of\\] %s", target);
        String regexB = String.format("\\-activate\\|%s\\|move: Trick\\|\\[of\\] (.*)", target);

        Pattern itemPatternA = Pattern.compile(regexA);
        Pattern itemPatternB = Pattern.compile(regexB);

        Matcher itemMatcherA = itemPatternA.matcher(html);
        Matcher itemMatcherB = itemPatternB.matcher(html);
        // 正则可能会匹配到不包含目标的字符串，所以要循环检查直到匹配为止
        if (itemMatcherA.find()) {
            target = itemMatcherA.group(1).trim();
            // 匹配到的戏法string为目标
        } else if (itemMatcherB.find()) {
            target = itemMatcherB.group(1).trim();
        } else {
            return null;
        }
        logger.debug("match trick target:" + target);
        regexA = String.format(new String("%s\\|([^\\|]*)\\|\\[from\\] move: Trick"), target);
        itemPatternA = Pattern.compile(regexA);
        itemMatcherA = itemPatternA.matcher(html);
        if (itemMatcherA.find()) {
            return itemMatcherA.group(1).trim();
        }
        return null;
    }

    private void extractBattleTurn(String html, Battle battle)  {
        String lastTurn = html.substring(html.lastIndexOf("|turn|"));
        String turnCount = null;
        try {
            turnCount = new BufferedReader(new StringReader(lastTurn)).readLine().split("\\|")[2];
            battle.setTurnCount(Integer.parseInt(turnCount));
        } catch (IOException e) {
            logger.warn("extract battle turn fail");
        }

    }
}