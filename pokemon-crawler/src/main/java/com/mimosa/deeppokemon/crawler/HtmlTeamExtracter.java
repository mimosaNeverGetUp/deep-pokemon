package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Team;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlTeamExtracter {

    public static Battle extract(String html)throws Exception{
        try{
            System.out.println("extract Team start");
            String[] playName = extractPlayerName(html);
            String tier = extractTier(html);
            Team[] teams = extractTeam(html);
            teams[0].setPlayerName(playName[0]);
            teams[1].setPlayerName(playName[1]);
            teams[0].setTier(tier);
            teams[1].setTier(tier);
            System.out.println("extract end");
            LocalDate date = extractDate(html);
            String winner = extractWinner(html);
            Float avageRating = extractAvageRating(html);
            Battle battle = new Battle(teams, date, winner, avageRating);
            battle.setInfo(String.format("%s vs %s",playName[0],playName[1]));
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
                System.out.println("match playerName1:"+matcher.group(2));
                playerNames[0] = matcher.group(2).trim();
            }
            else{
                System.out.println("match playerName2:"+matcher.group(2));
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
                System.out.println("match p1 Pokemon:"+pokemonName);
                Pokemon pokemon =extractPokemon(html,pokemonName,1);
                pokemons1.add(pokemon);
            }else{
                String pokemonName = matcher.group(2).trim();
                System.out.println("match p2 Pokemon:"+pokemonName);
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
                System.out.println(String.format("match %s move:%s", pokemonName, matcher.group(1)));
            }
        }
        pokemon.setMoves(moves);

        String item = extractPokemonItem(html, pokemonMoveName, playerNumber);
        System.out.println("match item:" + item);
        pokemon.setItem(item);
        return pokemon;
    }

    private static String extractTier(String html) {
        Pattern pattern = Pattern.compile("\\|tier\\|(.*)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String tier = matcher.group(1).trim();
            System.out.println(String.format("match tier:%s", tier));
            return tier;
        }
        return "unknown";
    }

    private static String extractWinner (String html) throws Exception{
        Pattern pattern = Pattern.compile("win\\|(.*)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String winPlayerName = matcher.group(1).trim();
            System.out.println("match winner:" + winPlayerName);
            return winPlayerName;
        }
        throw new Exception("match battle win relations failed");
    }

    private static LocalDate extractDate(String html) {
        Pattern pattern = Pattern.compile("Uploaded:</em>([^\\|<]*)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            System.out.println("match Date" + matcher.group(1));
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.ENGLISH);
            LocalDate date=LocalDate.parse(matcher.group(1).trim(), formatter);
            System.out.println("after format:"+formatter.format(date));
            return date;
        }
        return null;
    }

    private static float extractAvageRating(String html) {
        Pattern pattern = Pattern.compile("Rating:</em> ([0-9]+)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            System.out.println("match avgRating:" + matcher.group(1));
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
            System.out.println("match Move Name:" + pokemonMoveName);
            return pokemonMoveName;
        }
        return pokemonName;
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
                System.out.println("match trick target:" + target);
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
}
