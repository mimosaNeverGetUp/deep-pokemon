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
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BattleReplayExtractor {
    private static final Logger logger = LoggerFactory.getLogger(BattleReplayExtractor.class);
    private final TeamTagger teamTagger;

    public BattleReplayExtractor(TeamTagger teamTagger) {
        this.teamTagger = teamTagger;
    }

    public Battle extract(BattleReplayData battleReplayData) {
        logger.debug("extract Team start");

        String tier = battleReplayData.formatId();
        List<BattleTeam> teams = extractTeam(battleReplayData.log());
        teams.get(0).setPlayerName(battleReplayData.players().get(0));
        teams.get(1).setPlayerName(battleReplayData.players().get(1));
        teams.get(0).setTier(tier);
        teams.get(1).setTier(tier);
        teams.get(0).setRating(battleReplayData.rating());
        teams.get(1).setRating(battleReplayData.rating());
        //贴标签
        for (BattleTeam team : teams) {
            teamTagger.tagTeam(team, null);
        }
        logger.debug("extract end");
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(battleReplayData.uploadTime()),
                ZoneId.systemDefault());
        String winner = extractWinner(battleReplayData.log());
        int avageRating = battleReplayData.rating();
        Battle battle = new Battle(teams, date, winner, avageRating);
        battle.setBattleID(battleReplayData.id());
        battle.setFormat(tier);
        battle.setPlayers(battleReplayData.players());
        battle.setAvageRating(battleReplayData.rating());
        battle.setLog(battleReplayData.log());
        extractBattleTurn(battleReplayData.log(), battle);
        logger.debug("extract battle: {}", battle);
        return battle;
    }

    private static List<BattleTeam> extractTeam(String html) {
        Pattern pattern = Pattern.compile("\\|poke\\|p([12])\\|([^//|,]*)[\\|,]");
        Matcher matcher = pattern.matcher(html);
        List<Pokemon> pokemons1 = new ArrayList<>(6);
        List<Pokemon> pokemons2 = new ArrayList<>(6);
        while (matcher.find()) {
            if (matcher.group(1).equals("1")) {
                String pokemonName = matcher.group(2).trim();
                logger.debug("match p1 Pokemon: {}", pokemonName);
                Pokemon pokemon = extractPokemon(html, pokemonName, 1);
                pokemons1.add(pokemon);
            } else {
                String pokemonName = matcher.group(2).trim();
                logger.debug("match p2 Pokemon: {}", pokemonName);
                Pokemon pokemon = extractPokemon(html, pokemonName, 2);
                pokemons2.add(pokemon);
            }
        }
        if (pokemons1.isEmpty() && pokemons2.isEmpty()) {
            throw new ServerErrorException("A Team match failed", null);
        }

        List<BattleTeam> teams = new ArrayList<>(2);
        BattleTeam team1 = new BattleTeam();
        team1.setPokemons(pokemons1);
        BattleTeam team2 = new BattleTeam();
        team2.setPokemons(pokemons2);
        teams.add(team1);
        teams.add(team2);
        return teams;
    }

    private static Pokemon extractPokemon(String html, String pokemonName, int playerNumber) {
        Pokemon pokemon = new Pokemon(pokemonName);
        String pokemonMoveName = extractMoveName(html, pokemonName, playerNumber);
        if ("Ditto".equals(pokemonName)) {
            pokemon.setMoves(new HashSet<>(Collections.singletonList("Transform")));
        } else {
            String regex = String.format("move\\|p%da: %s\\|([^\\|]*)\\|(.*)", playerNumber,
                    Pattern.quote(pokemonMoveName));
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(html);
            HashSet<String> moves = new HashSet<>(4);
            while (matcher.find()) {
                String move = matcher.group(1);
                String other = matcher.group(2);
                if (other.contains("Magic Bounce")) {
                    logger.debug("match {} move {} but it is use via magic bounce", pokemonName, move);
                    continue;
                }
                if (moves.add(move)) {
                    logger.debug("match {} move:{}", pokemonName, move);
                }
            }
            pokemon.setMoves(moves);
        }
        return pokemon;
    }

    private static String extractWinner(String html) {
        Pattern pattern = Pattern.compile(Pattern.quote("|") + "win\\|(.*)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String winPlayerName = matcher.group(1).trim();
            logger.debug("match winner: {}", winPlayerName);
            return winPlayerName;
        }
        throw new ServerErrorException("match battle win relations failed", null);
    }

    private static String extractMoveName(String html, String pokemonName, int playerNumber) {
        String regex = String.format("(switch|drag)\\|p%da: ([^\\|]*)\\|%s", playerNumber, pokemonName);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String pokemonMoveName = matcher.group(2).trim();
            logger.debug("match Move Name:{}", pokemonMoveName);
            return pokemonMoveName;
        }
        return pokemonName;
    }

    private void extractBattleTurn(String html, Battle battle) {
        String lastTurn = html.substring(html.lastIndexOf("|turn|"));
        String turnCount;
        try {
            turnCount = new BufferedReader(new StringReader(lastTurn)).readLine().split("\\|")[2];
            battle.setTurnCount(Integer.parseInt(turnCount));
        } catch (IOException e) {
            logger.warn("extract battle turn fail");
        }

    }
}