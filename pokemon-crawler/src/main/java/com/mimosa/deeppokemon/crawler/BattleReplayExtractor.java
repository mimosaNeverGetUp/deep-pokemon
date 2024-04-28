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

import com.mimosa.deeppokemon.entity.*;
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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BattleReplayExtractor {
    private static final Logger logger = LoggerFactory.getLogger(BattleReplayExtractor.class);
    @Autowired
    private TeamTagger teamTagger;
    private static final Pattern playerPattern = Pattern.compile(new String("\\|player\\|p([12])\\|([^//|]*)\\|"));
    private static final Pattern tierPattern = Pattern.compile(new String("\\|tier\\|(.*)"));
    private static final Pattern winPattern = Pattern.compile(new String("win\\|(.*)"));
    private static final Pattern datePattern = Pattern.compile(new String("Uploaded:</em>([^\\|<]*)"));
    private static final Pattern rankPattern = Pattern.compile(new String("Rating:</em> ([0-9]+)"));
    private static final Pattern pokePattern = Pattern.compile(new String("\\|poke\\|p([12])\\|([^//|,]*)[\\|,]"));
    private static final Pattern movePattern = Pattern.compile(new String("\\|move\\|p([12]+)a: ([^\\|]+)\\|([^\\|]+)\\|"));
    private static final Pattern switchPattern = Pattern.compile(new String("\\|switch\\|p([12]+)a: ([^\\|]+)\\|"));
    private static final Pattern endItemPattern = Pattern.compile(new String("\\-enditem\\|p([12]+)a: ([^\\|]+)\\|([^\\|]*)"));
    private static final Pattern rockyHelmetPattern = Pattern.compile(new String("item: Rocky Helmet\\|\\[of\\] p([12]+)a: (.*)"));
    private static final Pattern trickPattern = Pattern.compile(new String("\\-item\\|p([12]+)a: ([^\\|]+)\\|([^\\|]+)\\|\\[from\\] move: Trick"));
    private static final Pattern spacePattern = Pattern.compile(new String("\\-side(end|start)\\|p([12]+): ([^\\|]+)\\|move: (.*)"));
    private static final Pattern damagePattern = Pattern.compile(new String("(\\|\\-damage|\\|\\-heal)\\|p([12]+)a: (.*?)\\|([0-9]+)(.*)"));
    private static final Pattern fromPattern = Pattern.compile(new String("\\[from\\] ([^\\|]+)"));
    private static final Pattern ofPattern = Pattern.compile(new String("\\[of\\] p([12]+)a: (.*)"));
    private static final Pattern turnPattern = Pattern.compile(new String("([\\d\\D]*?)(\\|turn\\|([0-9]+)|\\|win)"));
    private static final Pattern statusPattern = Pattern.compile(new String("\\|-(status|curestatus)\\|p([12]+)a: ([^\\|]+)\\|([^\\|]+)(.*)"));

    public Battle extract(BattleReplayData battleReplayData) {
        logger.debug("extract Team start");

        String tier = battleReplayData.formatId();
        Team[] teams = extractTeam(battleReplayData.log());
        //贴标签
        for (Team team : teams) {
            teamTagger.tagTeam(team);
        }

        teams[0].setPlayerName(battleReplayData.players().get(0));
        teams[1].setPlayerName(battleReplayData.players().get(0));
        teams[0].setTier(tier);
        teams[1].setTier(tier);
        logger.debug("extract end");
        LocalDate date = LocalDate.ofInstant(Instant.ofEpochSecond(battleReplayData.uploadTime()), ZoneId.systemDefault()) ;
        String winner = extractWinner(battleReplayData.log());
        Integer avageRating = battleReplayData.rating();
        Battle battle = new Battle(teams, date, winner, avageRating);
        battle.setBattleID(battleReplayData.id());
        battle.setLog(battleReplayData.log());
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

    private static String extractPokemonName(String html, String moveName, int playerNumber) {
        String regex = String.format("switch\\|p%da: %s\\|([^,\\|]*)", playerNumber, moveName);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String pokemonName = matcher.group(1).trim();
            return pokemonName;
        }
        return moveName;
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

    public static ArrayList<ArrayList<HashMap<String, Float>>> extractHealthLineData(String html) {
        String regex = new String("([\\d\\D]*?)(\\|turn\\|([0-9]+)|\\|win)");
        Pattern turnPattern = Pattern.compile(regex);
        regex = new String("(\\|\\-damage|\\|\\-heal)\\|p([12]+)a: (.*?)\\|([0-9]+)");
        Pattern damagePattern = Pattern.compile(regex);
        Matcher turnMatcher = turnPattern.matcher(html);
        HashMap<String, Float> healthMap1 = new HashMap<>();
        HashMap<String, Float> healthMap2 = new HashMap<>();
        ArrayList<HashMap<String, Float>> healthLineData1 = new ArrayList<>();
        ArrayList<HashMap<String, Float>> healthLineData2 = new ArrayList<>();
        while (turnMatcher.find()) {
            if ("1".equals(turnMatcher.group(3))) {
                continue;
            }
            String turnContest = turnMatcher.group(1);
            logger.debug("match turnContext: {}", turnContest);
            Matcher healthMatcher = damagePattern.matcher(turnContest);
            while (healthMatcher.find()) {
                String moveName = healthMatcher.group(3);
                String playerNumber = healthMatcher.group(2);
                Float health = Float.valueOf(healthMatcher.group(4));
                String pokemonName = extractPokemonName(html, moveName, Integer.valueOf(playerNumber));
                if ("1".equals(playerNumber)) {
                    healthMap1.put(pokemonName, health);
                } else {
                    healthMap2.put(pokemonName, health);
                }
            }
            healthLineData1.add(healthMap1);
            healthLineData2.add(healthMap2);
            healthMap1 = (HashMap<String, Float>) healthMap1.clone();
            healthMap2 = (HashMap<String, Float>) healthMap2.clone();
        }
        ArrayList<ArrayList<HashMap<String, Float>>> lists = new ArrayList<>();
        lists.add(healthLineData1);
        lists.add(healthLineData2);
        return lists;
    }

    public static ArrayList<ArrayList<String>> extractHighLight(String html) {
        String regex = new String("([\\d\\D]*?)(\\|turn\\|([0-9]+)|\\|win)");
        Pattern turnPattern = Pattern.compile(regex);
        Matcher turnMatcher = turnPattern.matcher(html);
        int i = 1;
        ArrayList<ArrayList<String>> highlightLists = new ArrayList<>();
        ArrayList<String> highlightList1 = new ArrayList<>();
        ArrayList<String> highlightList2 = new ArrayList<>();

        regex = new String("\\|move\\|p([12]+)a: ([^\\|]+)\\|([^\\|]+)\\|");
        Pattern movePattern = Pattern.compile(regex);
        regex = new String("\\|switch\\|p([12]+)a: (.*?)\\|");
        Pattern switchPattern = Pattern.compile(regex);
        regex = new String("\\|faint\\|p([12]+)a: (.*)");
        Pattern faintPattern = Pattern.compile(regex);
        regex = new String("\\|\\-enditem\\|p([12]+)a: ([^\\|]+)\\|([^\\|]*)");
        Pattern endItemPattern = Pattern.compile(regex);
        regex = new String("\\|\\-status\\|p([12]+)a: ([^\\|]+)\\|([^\\|]+)\\|");
        Pattern statPattern = Pattern.compile(regex);
        regex = new String("\\|\\-boost\\|p([12]+)a: (.*?)\\|");
        Pattern boostPattern = Pattern.compile(regex);
        int turn = 1;
        /*String json = battle.getHealthLinePairJsonString();
        ArrayList<HashMap<String,Float>> arrayList = (ArrayList<HashMap<String,Float>>) JSONArray.parseArray(json, HashMap.class);*/
        while (turnMatcher.find()) {
            if ("1".equals(turnMatcher.group(3))) {
                continue;
            }

            String turnContest = turnMatcher.group(1);
            Matcher moveMatcher = movePattern.matcher(turnContest);
            String base1 = new String(" ");
            String base2 = new String(" ");
            while (moveMatcher.find()) {
                String moveName = moveMatcher.group(2);
                String pokeName = extractPokemonName(html, moveName, Integer.valueOf(moveMatcher.group(1)));
                if ("1".equals(moveMatcher.group(1))) {
                    base1 += pokeName + " use " + moveMatcher.group(3) + " ";
                } else {
                    base2 += pokeName + " use " + moveMatcher.group(3) + " ";
                }
            }

            Matcher switchMatcher = switchPattern.matcher(turnContest);
            while (switchMatcher.find()) {
                String moveName = switchMatcher.group(2);
                String pokeName = extractPokemonName(html, moveName, Integer.parseInt(switchMatcher.group(1)));
                if ("1".equals(switchMatcher.group(1))) {
                    base1 += "switch " + pokeName + " ";
                } else {
                    base2 += "switch " + pokeName + "";
                }
            }

            Matcher faintMatcher = faintPattern.matcher(turnContest);
            while (faintMatcher.find()) {
                String moveName = faintMatcher.group(2).trim();
                String pokeName = extractPokemonName(html, moveName, Integer.valueOf(faintMatcher.group(1)));
                if ("1".equals(faintMatcher.group(1))) {
                    base2 += "(opp's " + pokeName + " faint) ";
                } else {
                    base1 += "(opp's " + pokeName + " faint) ";
                }
            }

            Matcher endMatcher = endItemPattern.matcher(turnContest);
            while (endMatcher.find()) {
                String moveName = endMatcher.group(2);
                String pokeName = extractPokemonName(html, moveName, Integer.valueOf(endMatcher.group(1)));
                if ("1".equals(endMatcher.group(1))) {
                    base2 += "(opp's " + pokeName + " " + endMatcher.group(3).trim() + " drop) ";
                } else {
                    base1 += "(opp's " + pokeName + " " + endMatcher.group(3).trim() + " drop) ";
                }
            }

            Matcher statMatcher = statPattern.matcher(turnContest);
            while (statMatcher.find()) {
                String moveName = statMatcher.group(2);
                String pokeName = extractPokemonName(html, moveName, Integer.valueOf(statMatcher.group(1)));
                if ("1".equals(statMatcher.group(1))) {
                    base2 += "(opp's " + pokeName + " " + statMatcher.group(3) + ") ";
                } else {
                    base1 += "(opp's " + pokeName + " " + statMatcher.group(3) + ") ";
                }
            }


            Matcher boostMatcher = boostPattern.matcher(turnContest);
            while (boostMatcher.find()) {
                String moveName = boostMatcher.group(2);
                String pokeName = extractPokemonName(html, moveName, Integer.valueOf(boostMatcher.group(1)));
                if ("1".equals(boostMatcher.group(1))) {
                    if (!base1.contains("boost")) {
                        base1 += "(boost)";

                    }
                } else {
                    if (!base2.contains("boost")) {
                        base2 += "(boost)";
                    }
                }
            }
            logger.debug("extract highlight: {}", base1);
            logger.debug("extract highlight: {}", base2);
            highlightList1.add(base1);
            highlightList2.add(base2);
        }
        highlightLists.add(highlightList1);
        highlightLists.add(highlightList2);
        return highlightLists;
    }

    private List<TeamBattleAnalysis> extractTeamBattleAnalysis(Team[] teams) {
//        //初始化
//        List<PokemonBattleAnalysis> firstPokemonBattleAnalysisList;
//        List<PokemonBattleAnalysis> secondBattleAnalysisList;
//        List<Map<String, PokemonBattleAnalysis>> mapList = new ArrayList<>(2);
//        for (Team team : teams) {
//            Map<String, PokemonBattleAnalysis> analysisMap = new HashMap<>(6);
//            for (Pokemon pokemon : team.getPokemons()) {
//                PokemonBattleAnalysis pokemonBattleAnalysis = new PokemonBattleAnalysis();
//                pokemonBattleAnalysis.setPokemonName(pokemon.getName());
//                analysisMap.put(pokemon.getName(), pokemonBattleAnalysis);
//            }
//            mapList.add(analysisMap);
//        }
        return null;
    }

    public static Battle extract1(String html) throws Exception {
        Battle battle = new Battle();
        extractTeam(html, battle);
        extractTier(html, battle);
        extractPlayerName(html, battle);
        extractWinner(html, battle);
        extractDate(html, battle);
        extractAvageRating(html, battle);
        initBattleTrendAndAnalysis(html, battle);
        // 读取每回合并解析
        extarctTurn(html, battle);

        return null;
    }

    public static void extractTeam(String html, Battle battle) {
        Matcher matcher = pokePattern.matcher(html);
        ArrayList<Pokemon> pokemons1 = new ArrayList<Pokemon>(6);
        ArrayList<Pokemon> pokemons2 = new ArrayList<Pokemon>(6);
        while (matcher.find()) {
            if (matcher.group(1).equals("1")) {
                String pokemonName = matcher.group(2).trim();
                logger.debug("match p1 Pokemon:" + pokemonName);
                pokemons1.add(new Pokemon(pokemonName));
            } else {
                String pokemonName = matcher.group(2).trim();
                logger.debug("match p2 Pokemon:" + pokemonName);
                pokemons2.add(new Pokemon(pokemonName));
            }
        }
        Team[] teams = {new Team(pokemons1), new Team(pokemons2)};
        battle.setTeams(teams);
    }

    public static void extractPlayerName(String html, Battle battle) throws Exception {
        Matcher matcher = playerPattern.matcher(html);
        Team[] team = battle.getTeams();
        while (matcher.find()) {
            if (matcher.group(1).equals("1")) {
                logger.debug("match playerName1:" + matcher.group(2));
                team[0].setPlayerName(matcher.group(2));
            } else {
                logger.debug("match playerName2:" + matcher.group(2));
                team[1].setPlayerName(matcher.group(2));
            }
        }
    }

    public static void extractTier(String html, Battle battle) {

        Matcher matcher = tierPattern.matcher(html);
        String tier = "unknown";
        if (matcher.find()) {
            tier = matcher.group(1).trim();
            logger.debug(String.format("match tier:%s", tier));
        }
        for (Team team : battle.getTeams()) {
            team.setTier(tier);
        }
    }

    public static void extractWinner(String html, Battle battle) throws Exception {

        Matcher matcher = winPattern.matcher(html);
        if (matcher.find()) {
            String winPlayerName = matcher.group(1).trim();
            logger.debug("match winner:" + winPlayerName);
            battle.setWinner(winPlayerName);
        }
        throw new Exception("match battle win relations failed");
    }

    public static void extractDate(String html, Battle battle) {

        Matcher matcher = datePattern.matcher(html);
        if (matcher.find()) {
            logger.debug("match Date" + matcher.group(1));
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.ENGLISH);
            LocalDate date = LocalDate.parse(matcher.group(1).trim(), formatter);
            logger.debug("after format:" + formatter.format(date));
            battle.setDate(date);
        }
    }

    public static void extractAvageRating(String html, Battle battle) {

        Matcher matcher = rankPattern.matcher(html);
        Float rating = 0.0F;
        if (matcher.find()) {
            logger.debug("match avgRating:" + matcher.group(1));
            rating = Float.parseFloat(matcher.group(1));
        }
        battle.setAvageRating(rating);
    }

    /**
     * 提取宝可梦配置，统计血线、贡献变化以及击杀、行动回合以及有效行动数
     */
    public static void extarctTurn(String html, Battle battle) {
        BattleTurnExtracterHelper turnExtracterHelper = new BattleTurnExtracterHelper(battle);
        Matcher turnMatcher = turnPattern.matcher(html);
        Matcher switchMatcher;
        while (turnMatcher.find()) {
            //正则匹配到回合数是下一回合的开始提示，需要减一
            int turnIndex = Integer.parseInt(turnMatcher.group(3)) - 1;
            String turnContext = turnMatcher.group(1);
            String[] events = turnContext.split("\\n");
            for (String event : events) {
                String[] subStr = event.split("\\|");
                if (subStr.length < 2) {
                    continue;
                }
                String eventType = event.split("\\|")[1];
                switch (eventType) {
                    case "switch":
                        extractSwitch(event, turnExtracterHelper);
                        break;

                    case "move":
                        extractPokemonMove(event, turnExtracterHelper);
                        break;

                    case "-heal":
                    case "-damage":
                        extractDamageOrHealth(turnIndex, event, turnExtracterHelper);
                        break;

                    case "-sidestart":
                    case "-sideend":
                        extractSpaceTrend(turnIndex, event, turnExtracterHelper);
                        break;

                    case "-status":
                        extractStatus(turnIndex, event, turnExtracterHelper);
                        break;

                    case "-weather":
                        //todo
                        break;
                }
            }
            extractPokemonItem(turnContext, turnExtracterHelper);
        }
        //特殊化处理
        for (Team team : battle.getTeams()) {
            for (Pokemon pokemon : team.getPokemons()) {
                if ("Ditto".equals(pokemon.getName())) {
                    pokemon.setMoves(new HashSet<>(Collections.singletonList("Transform")));
                }
            }
        }
    }

    public static void initBattleTrendAndAnalysis(String html, Battle battle) throws IOException {
        String lastTurn = html.substring(html.lastIndexOf("turn"));
        String turnCount = new BufferedReader(new StringReader(lastTurn)).readLine().split("\\|")[2];
        battle.setBattleTrend(new BattleTrend(Integer.parseInt(turnCount), battle.getTeams()));
        TeamBattleAnalysis[] teamBattleAnalyses = new TeamBattleAnalysis[2];
        int i = 0;
        for (Team team : battle.getTeams()) {
            teamBattleAnalyses[i] = new TeamBattleAnalysis(team);
            i++;
        }
        battle.setTeamBattleAnalysis(teamBattleAnalyses);
    }

    public static void extractPokemonMove(String turnContext, BattleTurnExtracterHelper turnExtracterHelper) {
        Matcher moveMatcher = movePattern.matcher(turnContext);
        if (moveMatcher.find()) {
            int playerIndex = Integer.parseInt(moveMatcher.group(1));
            String pokemonMoveName = moveMatcher.group(2);
            String move = moveMatcher.group(3);
            turnExtracterHelper.addMove(playerIndex, pokemonMoveName, move);
            turnExtracterHelper.addMoveCount(playerIndex, pokemonMoveName);
        }
    }

    public static void extractPokemonItem(String turnContext, BattleTurnExtracterHelper turnExtracterHelper) {
        extractTrickItem(turnContext, turnExtracterHelper);
        extractEndItem(turnContext, turnExtracterHelper);
        extractRockyItem(turnContext, turnExtracterHelper);
    }

    public static void extractEndItem(String turnContext, BattleTurnExtracterHelper turnExtracterHelper) {
        Matcher endItemMatcher = endItemPattern.matcher(turnContext);
        while (endItemMatcher.find()) {
            int playIndex = Integer.parseInt(endItemMatcher.group(1));
            String moveName = endItemMatcher.group(2);
            String item = endItemMatcher.group(3);
            turnExtracterHelper.setPokemonItem(playIndex, moveName, item);
        }
    }

    public static void extractTrickItem(String turnContext, BattleTurnExtracterHelper turnExtracterHelper) {
        Matcher trickMatcher = trickPattern.matcher(turnContext);
        while (trickMatcher.find()) {
            int playerIndex = Integer.parseInt(trickMatcher.group(1));
            String moveName = trickMatcher.group(2);
            String move = trickMatcher.group(3);
            if (trickMatcher.find()) {
                turnExtracterHelper.setPokemonItem(playerIndex, moveName, trickMatcher.group(3));
                turnExtracterHelper.setPokemonItem(Integer.parseInt(trickMatcher.group(1)), trickMatcher.group(2), move);
            }
        }
    }

    public static void extractRockyItem(String turnContext, BattleTurnExtracterHelper turnExtracterHelper) {
        Matcher rockyHelmetMatcher = rockyHelmetPattern.matcher(turnContext);
        while (rockyHelmetMatcher.find()) {
            int playerIndex = Integer.parseInt(rockyHelmetMatcher.group(1));
            String moveName = rockyHelmetMatcher.group(2);
            turnExtracterHelper.setPokemonItem(playerIndex, moveName, "Rocky Helmet");
        }
    }

    public static void extractSpaceTrend(int turnIndex, String turnContext, BattleTurnExtracterHelper turnExtracterHelper) {
        Matcher spaceMatcher = spacePattern.matcher(turnContext);
        if (spaceMatcher.find()) {
            boolean exist = "start".equals(spaceMatcher.group(1));
            int playerIndex = Integer.parseInt(spaceMatcher.group(2));
            String move = spaceMatcher.group(3);
            turnExtracterHelper.setSpaceTrend(turnIndex, playerIndex, move, exist);
        }
    }

    public static void extractDamageOrHealth(int turnIndex, String turnContext, BattleTurnExtracterHelper turnExtracterHelper) {
        Matcher damageMatcher = damagePattern.matcher(turnContext);
        if (damageMatcher.find()) {
            boolean isDamage = "damage".equals(damageMatcher.group(1));
            int playerIndex = Integer.parseInt(damageMatcher.group(2));
            String moveName = damageMatcher.group(3);
            short currentHealth = Short.parseShort(damageMatcher.group(4));
            int damage = turnExtracterHelper.setHealthTrendAndReturnDiff(turnIndex, playerIndex, moveName, currentHealth);
            String damageFrom = null, damageOf = null;
            int ofPlayerIndex = 0;
            if (damageMatcher.groupCount() == 5) {
                String extraDamageInfo = damageMatcher.group(5);
                // 伤害来源及归属宝可梦提取
                Matcher fromMatcher = fromPattern.matcher(extraDamageInfo);
                Matcher ofMatcher = ofPattern.matcher(extraDamageInfo);
                if (fromMatcher.find()) {
                    damageFrom = fromMatcher.group(1);
                }
                if (ofMatcher.find()) {
                    ofPlayerIndex = Integer.parseInt(ofMatcher.group(1));
                    damageOf = ofMatcher.group(2);
                }
            }
            if (isDamage) {
                turnExtracterHelper.countPokemonDamage(playerIndex, damage, damageFrom, damageOf, ofPlayerIndex);
            } else {
                // 统计回复
                turnExtracterHelper.countPokemonHeal(playerIndex, damage);
            }
        }
    }


    public static void extractSwitch(String turnContext, BattleTurnExtracterHelper turnExtracterHelper) {
        Matcher switchMatcher = switchPattern.matcher(turnContext);
        if (switchMatcher.find()) {
            int playerIndex = Integer.parseInt(switchMatcher.group(1)) - 1;
            String moveName = switchMatcher.group(2);
            String pokemonName = switchMatcher.group(3);
            turnExtracterHelper.setPresentPokemon(playerIndex, pokemonName);
            turnExtracterHelper.setMovePokemonName(playerIndex, moveName, pokemonName);
            turnExtracterHelper.addSwitchCount(playerIndex, pokemonName);
        }
    }

    public static void extractStatus(int turnIndex, String event, BattleTurnExtracterHelper turnExtracterHelper) {
        Matcher statusMatcher = statusPattern.matcher(event);
        String statusFrom, statusOf;
        Integer ofPlayerIndex;
        if (statusMatcher.find()) {
            boolean cure = statusMatcher.group(1).contains("cure");
            int playerIndex = Integer.parseInt(statusMatcher.group(2)) - 1;
            String moveName = statusMatcher.group(3);
            String status = cure ? Status.HEALTH.getName() : statusMatcher.group(4);
            String extraSourceInfo = statusMatcher.group(5);
            Matcher fromMatcher = fromPattern.matcher(extraSourceInfo);
            Matcher ofMatcher = ofPattern.matcher(extraSourceInfo);
            if (fromMatcher.find()) {
                statusFrom = fromMatcher.group(1);
            }
            if (ofMatcher.find()) {
                ofPlayerIndex = Integer.parseInt(ofMatcher.group(1));
                statusOf = ofMatcher.group(2);
            }
            turnExtracterHelper.setStatusTrend(turnIndex, playerIndex, moveName, status);
        }
    }
}