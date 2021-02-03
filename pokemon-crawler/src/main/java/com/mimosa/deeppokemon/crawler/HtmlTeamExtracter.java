package com.mimosa.deeppokemon.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mimosa.deeppokemon.entity.*;
import com.mimosa.deeppokemon.tagger.TeamTagger;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class HtmlTeamExtracter {
    private static final Logger logger = LoggerFactory.getLogger(HtmlTeamExtracter.class);
    @Autowired
    private TeamTagger teamTagger;


    public Battle extract(String html)throws Exception{
        try{
            logger.debug("extract Team start");
            String[] playName = extractPlayerName(html);
            String tier = extractTier(html);
            Team[] teams = extractTeam(html);
            //贴标签
            for (Team team : teams) {
                teamTagger.tagTeam(team);
            }
            ArrayList<ArrayList<HashMap<String, Float>>> lists = extractHealthLineData(html);
            ArrayList<ArrayList<String>> list = extractHighLight(html);
            for (int j = 0; j < lists.size(); ++j) {
                ArrayList<HashMap<String, Float>> arrayList = lists.get(j);
                for (int i = 1; i < arrayList.size(); ++i) {
                    HashMap<String, Float> hashMap = arrayList.get(i);
                    for (String s : hashMap.keySet()) {
                        Float f = hashMap.get(s);
                        if (f != null && f < 50.0f &&  f!=0.0f) {
                            HashMap<String, Float> hashMapPrevious = arrayList.get(i-1);
                            Float f1 = hashMapPrevious.get(s);
                            logger.debug(f + " and" + f1);
                            if (f1 == null || f1 >= 50.0f) {
                                int  pos;
                                if (j == 0) {
                                    pos = 1;
                                } else {
                                    pos = 0;
                                }
                                String str = list.get(pos).get(i);
                                list.get(pos).set(i, str + "(opp's " + s + " hp " + f.toString()+")");
                                logger.debug(list.get(pos).get(i));
                            }
                        }
                    }
                }
            }
            String healthLinePairJsonString = JSONObject.toJSONString(lists);
            String highLightJsonString = JSONObject.toJSONString(list);
            teams[0].setPlayerName(playName[0]);
            teams[1].setPlayerName(playName[1]);
            teams[0].setTier(tier);
            teams[1].setTier(tier);
            logger.debug("extract end");
            LocalDate date = extractDate(html);
            String winner = extractWinner(html);
            Float avageRating = extractAvageRating(html);
            Battle battle = new Battle(teams, date, winner, avageRating, healthLinePairJsonString);
            battle.setHighLightJsonString(highLightJsonString);
            battle.setInfo(String.format("%s vs %s",playName[0],playName[1]));
            logger.debug("extract battle: {}",battle);
            return battle;
        }
        catch (Exception e){
            throw  e;
        }
    }

    private static String[] extractPlayerName(String html) throws Exception{
        Pattern pattern=Pattern.compile("\\|player\\|p([12])\\|([^//|]*)\\|");
        Matcher matcher=pattern.matcher(html);
        String[] playerNames = new String[2];
        while(matcher.find()){
            if(matcher.group(1).equals("1")){
                logger.debug("match playerName1:"+matcher.group(2));
                playerNames[0] = matcher.group(2).trim();
            }
            else{
                logger.debug("match playerName2:"+matcher.group(2));
                playerNames[1] = matcher.group(2).trim();
            }
        }
        if(playerNames[0] ==null && playerNames[1]==null){
            throw new Exception("extract playerName Failed");
        }
        return  playerNames;
    }

    private  static Team[] extractTeam(String html)throws  Exception{
        Pattern pattern = Pattern.compile("\\|poke\\|p([12])\\|([^//|,]*)[\\|,]");
        Matcher matcher = pattern.matcher(html);
        ArrayList<Pokemon> pokemons1 = new ArrayList<Pokemon>(6);
        ArrayList<Pokemon> pokemons2 = new ArrayList<Pokemon>(6);
        while (matcher.find()){
            if(matcher.group(1).equals("1")){
                String pokemonName = matcher.group(2).trim();
                logger.debug("match p1 Pokemon:"+pokemonName);
                Pokemon pokemon =extractPokemon(html,pokemonName,1);
                pokemons1.add(pokemon);
            }else{
                String pokemonName = matcher.group(2).trim();
                logger.debug("match p2 Pokemon:"+pokemonName);
                Pokemon pokemon =extractPokemon(html,pokemonName,2);
                pokemons2.add(pokemon);
            }
        }
        if(pokemons1.size() == 0 && pokemons2.size() == 0){throw new Exception("A Team match failed");}
        Team team1 =new Team(pokemons1);
        Team team2 =new Team(pokemons2);
        Team[] teams =new Team[2];
        teams[0] = team1;
        teams[1] = team2;
        return  teams;
    }

    private  static Pokemon extractPokemon(String html,String pokemonName,int playerNumber){
        Pokemon pokemon = new Pokemon(pokemonName);
        String pokemonMoveName = extractMoveName(html, pokemonName, playerNumber);
        String regex = String.format(new String("move\\|p%da: %s\\|([^\\|]*)\\|"), playerNumber, pokemonMoveName);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        HashSet<String> moves = new HashSet<>(4);
        while (matcher.find()) {
            if (moves.add(matcher.group(1))) {
                logger.debug(String.format("match %s move:%s", pokemonName, matcher.group(1)));
            }
        }
        pokemon.setMoves(moves);

        String item = extractPokemonItem(html, pokemonMoveName, playerNumber);
        logger.debug("match item:" + item);
        pokemon.setItem(item);
        return pokemon;
    }

    private static String extractTier(String html) {
        Pattern pattern = Pattern.compile("\\|tier\\|(.*)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String tier = matcher.group(1).trim();
            logger.debug(String.format("match tier:%s", tier));
            return tier;
        }
        return "unknown";
    }

    private static String extractWinner (String html) throws Exception{
        Pattern pattern = Pattern.compile("win\\|(.*)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String winPlayerName = matcher.group(1).trim();
            logger.debug("match winner:" + winPlayerName);
            return winPlayerName;
        }
        throw new Exception("match battle win relations failed");
    }

    private static LocalDate extractDate(String html) {
        Pattern pattern = Pattern.compile("Uploaded:</em>([^\\|<]*)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            logger.debug("match Date" + matcher.group(1));
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.ENGLISH);
            LocalDate date=LocalDate.parse(matcher.group(1).trim(), formatter);
            logger.debug("after format:"+formatter.format(date));
            return date;
        }
        return null;
    }

    private static float extractAvageRating(String html) {
        Pattern pattern = Pattern.compile("Rating:</em> ([0-9]+)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            logger.debug("match avgRating:" + matcher.group(1));
            return Float.parseFloat(matcher.group(1));
        }
        return 0;//zero mean unkown
    }

    private static String extractMoveName (String html,String pokemonName,int playerNumber){
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

    private static String extractPokemonName (String html,String moveName,int playerNumber){
        String regex = String.format("switch\\|p%da: %s\\|([^,\\|]*)", playerNumber, moveName);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String pokemonName = matcher.group(1).trim();
            return pokemonName;
        }
        return moveName;
    }

    private  static String extractPokemonItem(String html,String pokemonMoveName,int playerNumber){
        //trick will disturb other check ,so should check first
        String item = extractTrickItem(html, pokemonMoveName, playerNumber);
        if (item != null){ return item; }

        String regex = String.format(new String("p%da: %s\\|.*item: (.*)"), playerNumber, pokemonMoveName);
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
        if (item != null){ return item; }

        item = extractRockyHelmetItem(html, pokemonMoveName, playerNumber);
        if (item != null){ return item; }

        item = extractMegaItem(html, pokemonMoveName, playerNumber);
        if (item != null){ return item; }

        item = extractZMovetItem(html, pokemonMoveName, playerNumber);
        if (item != null){ return item; }

        return  null;
    }

    private static boolean containRockyHelmet(String item){
        return item.contains("Rocky Helmet");
    }

    private static String extractRockyHelmetItem(String html,String pokemonMoveName,int playerNumber){
        String regex = String.format(new String("item: Rocky Helmet\\|\\[of\\] p%da: %s"), playerNumber, pokemonMoveName);
        Pattern itemPattern = Pattern.compile(regex);
        Matcher itemMatcher = itemPattern.matcher(html);
        if (itemMatcher.find()) {
            return "RockyHelmet";
        }
        return null;
    }

    private static String extractZMovetItem(String html,String pokemonMoveName,int playerNumber){
        String regex = String.format(new String("\\-zpower\\|p%da: %s"), playerNumber, pokemonMoveName);
        Pattern itemPattern = Pattern.compile(regex);
        Matcher itemMatcher = itemPattern.matcher(html);
        if (itemMatcher.find()) {
            return "Z-Crystal";
        }
        return null;
    }

    private static String extractMegaItem(String html, String pokemonMoveName, int playerNumber) {
        String regex = String.format(new String("\\-mega\\|p%da: %s\\|[^\\|]*\\|([^\\|]*)"), playerNumber, pokemonMoveName);
        Pattern itemPattern = Pattern.compile(regex);
        Matcher itemMatcher = itemPattern.matcher(html);
        if (itemMatcher.find()) {
            return itemMatcher.group(1).trim();
        }
        return null;
    }

    private static String extractEndItem(String html, String pokemonMoveName, int playerNumber) {
        String regex = String.format(new String("\\-enditem\\|p%da: %s\\|([^\\|]*)\\|"), playerNumber, pokemonMoveName);
        Pattern itemPattern = Pattern.compile(regex);
        Matcher itemMatcher = itemPattern.matcher(html);
        if (itemMatcher.find()) {
            return itemMatcher.group(1).trim();
        }
        return null;
    }

    private static String extractTrickItem(String html, String pokemonMoveName, int playerNumber) {
        String target = String.format("p%da: %s", playerNumber, pokemonMoveName);
        String regex = String.format(new String("\\-activate\\|([^\\|]*|%s)\\|move: Trick\\|\\[of\\] (.*|%s)"), target, target);
        Pattern itemPattern = Pattern.compile(regex);
        Matcher itemMatcher = itemPattern.matcher(html);
        if (itemMatcher.find()) {
            String pre = itemMatcher.group(1).trim();
            String next = itemMatcher.group(2).trim();
            if (pre.equals(target) || next.equals(target)) {
                if (!pre.equals(target)) {
                    target = pre;
                } else {
                    target = next;
                }
                logger.debug("match trick target:" + target);
                regex = String.format(new String("%s\\|([^\\|]*)\\|\\[from\\] move: Trick"), target);
                itemPattern = Pattern.compile(regex);
                itemMatcher = itemPattern.matcher(html);
                if (itemMatcher.find()) {
                    return itemMatcher.group(1).trim();
                }
            }
        }
        return null;
    }

    public  static ArrayList<ArrayList<HashMap<String, Float>>> extractHealthLineData(String html) {
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
            logger.debug("match turnContext: {}",turnContest);
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
        int i =1;
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
        int turn =1;
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
                    base1 += pokeName + " use "  + moveMatcher.group(3)+" ";
                } else {
                    base2 += pokeName + " use " + moveMatcher.group(3)+" ";
                }
            }

            Matcher switchMatcher = switchPattern.matcher(turnContest);
            while (switchMatcher.find()) {
                String moveName = switchMatcher.group(2);
                String pokeName = extractPokemonName(html, moveName, Integer.valueOf(switchMatcher.group(1)));
                if ("1".equals(switchMatcher.group(1))) {
                    base1 +=  "switch "  + pokeName+" ";
                } else {
                    base2 += "switch " + pokeName+"";
                }
            }

            Matcher faintMatcher = faintPattern.matcher(turnContest);
            while (faintMatcher.find()) {
                String moveName = faintMatcher.group(2).trim();
                String pokeName = extractPokemonName(html, moveName, Integer.valueOf(faintMatcher.group(1)));
                if ("1".equals(faintMatcher.group(1))) {
                    base2 +=  "(opp's "  + pokeName + " faint) ";
                } else {
                    base1 +=  "(opp's "  + pokeName + " faint) ";
                }
            }

            Matcher endMatcher = endItemPattern.matcher(turnContest);
            while (endMatcher.find()) {
                String moveName = endMatcher.group(2);
                String pokeName = extractPokemonName(html, moveName, Integer.valueOf(endMatcher.group(1)));
                if ("1".equals(endMatcher.group(1))) {
                    base2 +=  "(opp's "  + pokeName + " "+endMatcher.group(3).trim()+ " drop) ";
                } else {
                    base1 +=  "(opp's "  + pokeName + " "+endMatcher.group(3).trim()+ " drop) ";
                }
            }

            Matcher statMatcher = statPattern.matcher(turnContest);
            while (statMatcher.find()) {
                String moveName = statMatcher.group(2);
                String pokeName = extractPokemonName(html, moveName, Integer.valueOf(statMatcher.group(1)));
                if ("1".equals(statMatcher.group(1))) {
                    base2 +=  "(opp's "  + pokeName + " "+statMatcher.group(3)+ ") ";
                } else {
                    base1 +=  "(opp's "  + pokeName + " "+statMatcher.group(3)+ ") ";
                }
            }


            Matcher boostMatcher = boostPattern.matcher(turnContest);
            while (boostMatcher.find()) {
                String moveName = boostMatcher.group(2);
                String pokeName = extractPokemonName(html, moveName, Integer.valueOf(boostMatcher.group(1)));
                if ("1".equals(boostMatcher.group(1))) {
                    if (!base1.contains("boost")) {
                        base1 +=  "(boost)";

                    }
                } else {
                    if (!base2.contains("boost")) {
                        base2 +=  "(boost)";
                    }
                }
            }
            logger.debug("extract highlight: {}",base1);
            logger.debug("extract highlight: {}",base2);
            highlightList1.add(base1);
            highlightList2.add(base2);
        }
        highlightLists.add(highlightList1);
        highlightLists.add(highlightList2);
        return highlightLists;
    }

    private List<TeamBattleAnalysis> extractTeamBattleAnalysis(Team[] teams) {
        //初始化
        List<PokemonBattleAnalysis> firstPokemonBattleAnalysisList;
        List<PokemonBattleAnalysis> secondBattleAnalysisList;
        List<Map<String, PokemonBattleAnalysis>> mapList = new ArrayList<>(2);
        for (Team team : teams) {
            Map<String, PokemonBattleAnalysis> analysisMap = new HashMap<>(6);
            for (Pokemon pokemon : team.getPokemons()) {
                PokemonBattleAnalysis pokemonBattleAnalysis = new PokemonBattleAnalysis();
                pokemonBattleAnalysis.setPokemonName(pokemon.getName());
                analysisMap.put(pokemon.getName(), pokemonBattleAnalysis);
            }
            mapList.add(analysisMap);
        }
        return null;
    }
}
