package com.mimosa.pokemon.portal.service;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Player;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Team;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class BattleService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Battle> listBattleByName(String playerName, int page) {
        int num_perPage = 15;
        Query query = new BasicQuery(String.format("{ 'teams.playerName' : \"%s\" }", playerName))
                .with(Sort.by(Sort.Order.desc("date"))).limit(num_perPage).skip((page - 1) * num_perPage);
        List<Battle> battles = mongoTemplate.find(query, Battle.class, "battle");
        return battles;
    }

    public List<Team> listTeamByPlayerList(List<Player> list) {
        ArrayList<Pokemon> emptyPokemons = new ArrayList<>(6);
        Pokemon emptyPokemon = new Pokemon("null");

        List<Team> teamList = new ArrayList<>();
        for (int i = 0; i < 6; ++i) {
            emptyPokemons.add(emptyPokemon);
        }
        for (Player player : list) {
            String queryString = String.format("{ 'teams.playerName' : \"%s\",'teams.tier' : \"[Gen 8] OU\" }", player.getName());
            System.out.println(queryString);
            Query query = new BasicQuery(queryString)
                    .with(Sort.by(Sort.Order.desc("date")))
                    .limit(2);
            List<Battle> battles = mongoTemplate.find(query, Battle.class, "battle");
            for (Battle battle : battles) {
                Team[] teams = battle.getTeams();
                for (Team team : teams) {
                    if (player.getName().equals(team.getPlayerName())) {
                        teamList.add(team);
                        break;
                    }
                }
            }
            Team emptyTeam = new Team();
            emptyTeam.setPokemons(emptyPokemons);
            emptyTeam.setPlayerName(player.getName());
            for (int j = 0; j < 2 - battles.size(); ++j) {
                teamList.add(emptyTeam);
            }
        }
        return teamList;
    }

    public List<Pair<Team, String>> Team(int page) {
        int num_perPage = 20;
        ArrayList<Team> teamList = new ArrayList<>();
        List<Pair<Team, String>> teams = new ArrayList<>();
        Query query = new BasicQuery("{}")
                .with(Sort.by(Sort.Order.desc("date")))
                .limit(num_perPage).skip((page - 1) * num_perPage);
        List<Battle> battles = mongoTemplate.find(query, Battle.class, "battle");
        for (Battle battle : battles) {
            for (Team team : battle.getTeams()) {
                boolean b = true;
                for (int i = 0; i < teamList.size(); ++i) {
                    if (team.equals(teamList.get(i))) {
                        b = false;
                        break;
                    }
                }
                if (b) {
                    teamList.add(team);
                    Pair<Team, String> pair = new Pair<>(team, battle.getBattleID());
                    teams.add(pair);
                }
            }
        }
        return teams;

    }

    public Pair<Pair<Float,Float>, List<Team>> statistic(String name, LocalDate dayAfter, LocalDate dayBefore) {
        Query query = new BasicQuery("{}").with(Sort.by(Sort.Order.desc("date")));
        Criteria criteria = Criteria.where("date").gte(dayAfter).lte(dayBefore);
        query.addCriteria(criteria);
        List<Battle> battleList = mongoTemplate.find(query, Battle.class, "battle");
        int pokeUse = 0;
        int pokeWin = 0;
        int pokeTotal = 2 * battleList.size();
        List<Team> teamList = new ArrayList<>();
        for (Battle battle : battleList) {
            for (Team team : battle.getTeams()) {
                for (Pokemon pokemon : team.getPokemons()) {
                    if (name.equals(pokemon.getName())) {
                        ++pokeUse;
                        boolean b = true;
                        for (Team t:teamList ) {
                            if (team.equals(t)) {
                                b = false;
                                break;
                            }
                        }
                        if (b && teamList.size() <= 100) {
                            teamList.add(team);
                        }

                        String playerName = team.getPlayerName();
                        String winnerName = battle.getWinner();
                        if (playerName !=null && playerName.equals(winnerName)) {
                            ++pokeWin;
                        }
                    }
                }
            }
        }
        Float f1 = (float) pokeUse / (float) pokeTotal;
        Float f2 = (float) pokeWin / (float) pokeUse;
        Pair<Float, Float> pk = new Pair(f1,f2);
        Pair<Pair<Float, Float>, List<Team>> pair = new Pair<>(pk, teamList);
        return pair;
    }
}
