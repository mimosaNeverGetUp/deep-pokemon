package com.mimosa.pokemon.portal.service;

import com.mimosa.deeppokemon.entity.*;
import com.mimosa.pokemon.portal.entity.MapResult;
import com.mimosa.pokemon.portal.entity.Statistic;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public Pair<Pair<Float, Float>, List<Team>> statistic(String name, LocalDate dayAfter, LocalDate dayBefore) throws Exception {
        Query query = new BasicQuery("{}").with(Sort.by(Sort.Order.desc("date")));
        Criteria criteria = Criteria.where("date").gte(dayAfter).lte(dayBefore);
        query.addCriteria(criteria);
        Statistic statistic = mapReduce(query, name);
        query = query.limit(100);
        List<Battle> battleList = mongoTemplate.find(query, Battle.class, "battle");
        Set<Team> teamSet = new HashSet<>(100);
        for (Battle battle : battleList) {
            Team[] teams = battle.getTeams();
            for (Team team : teams) {
                for (Pokemon pokemon : team.getPokemons()) {
                    if (name.equals(pokemon.getName())) {
                        teamSet.add(team);
                    }
                }
            }
        }
        List<Team> teamList = new ArrayList<>(teamSet);

        Pair<Float, Float> pk = new Pair(statistic.getUse() / statistic.getTotal(), statistic.getWin() / statistic.getUse());
        Pair<Pair<Float, Float>, List<Team>> pair = new Pair<>(pk, teamList);
        return pair;
    }

    public List<MapResult> statisticAll(String name, LocalDate dayAfter,
                                        LocalDate dayBefore) throws Exception {
        Query query = new BasicQuery("{}").with(Sort.by(Sort.Order.desc("date")));
        Criteria criteria = Criteria.where("date").gte(dayAfter).lte(dayBefore);
        query.addCriteria(criteria);
        List<MapResult> mapResultList = mapReduceAll(query);
        return mapResultList;
    }

    public List<MapResult> statisticAllDetails(String name, LocalDate dayAfter,
                                        LocalDate dayBefore) throws Exception {
        Query query = new BasicQuery("{}").with(Sort.by(Sort.Order.desc("date")));
        Criteria criteria = Criteria.where("date").gte(dayAfter).lte(dayBefore);
        query.addCriteria(criteria);
        List<MapResult> mapResultList = mapReduceAllDetails(query);
        return mapResultList;
    }

    //根据名字统计登场率胜率,待完善更多数据补充
    public Statistic mapReduce(Query query, String name) throws Exception {
        String mapFunction = "function(){" +
                "var use=0;var total=0; var win=0;" +
                "for(var i=0;i<this.teams.length;++i ){" +
                "   for(var j=0;j<this.teams[i].pokemons.length;++j){" +
                "       if(this.teams[i].pokemons[j].name==\"" + name + "\"){" +
                "           ++use;" +
                "           if(this.winner==this.teams[i].playerName){" +
                "               ++win;" +
                "           }" +
                "       }" +
                "   }" +
                "   ++total;" +
                "}" +
                "emit(\"" + name + "\",{\"use\":use,\"win\":win,\"total\":total});" +
                "}";
        String reduceFunction = "function(keys,values){" +
                "var use=0;var total=0; var win=0;" +
                "for(var i=0;i<values.length;++i){" +
                "use = use + values[i].use;" +
                "win = win + values[i].win;" +
                "total = total + values[i].total ;" +
                "}" +
                "return (\"+" + name + "\",{\"use\":use,\"win\":win,\"total\":total});" +
                "}";
        MapReduceResults<MapResult> results = mongoTemplate.mapReduce(query, "battle", mapFunction, reduceFunction, MapResult.class);
        Iterator<MapResult> iterator = results.iterator();
        if (results == null || !iterator.hasNext()) {
            throw new Exception("mapReduce null!");
        }
        return iterator.next().getValue();
    }
    //统计所有pm的登场率、胜率
    public List<MapResult> mapReduceAll(Query query) throws Exception {
        Long total = 2 * mongoTemplate.count(query, "battle");
        String mapFunction = "function(){" +
                "for(var i=0;i<this.teams.length;++i ){" +
                "   for(var j=0;j<this.teams[i].pokemons.length;++j){" +
                "       if(this.winner==this.teams[i].playerName){" +
                "           emit(this.teams[i].pokemons[j].name,{\"use\":1,\"win\":1,\"total\":" + total + "});}" +
                "       else{" +
                "           emit(this.teams[i].pokemons[j].name,{\"use\":1,\"win\":0,\"total\":" + total + "});" +
                "           }" +
                "   }" +
                "}" +
                "}";
        String reduceFunction = "function(keys,values){" +
                "var use=0; var win=0;" +
                "for(var i=0;i<values.length;++i){" +
                "use = use + values[i].use;" +
                "win = win + values[i].win;" +
                "}" +
                "return (values[0]._id,{\"use\":use,\"win\":win,\"total\":values[0].total});" +
                "}";
        MapReduceResults<MapResult> results =
                mongoTemplate.mapReduce(query, "battle", mapFunction, reduceFunction, MapResult.class);
        if (results == null) {
            throw new Exception("mapReduce null!");
        }
        Iterator<MapResult> iterator = results.iterator();
        List<MapResult> mapResultList = new ArrayList<>(400);
        while (iterator.hasNext()) {
            mapResultList.add(iterator.next());
        }
        Collections.sort(mapResultList, new Comparator<MapResult>() {
            @Override
            public int compare(MapResult o1, MapResult o2) {
                float use1 = o1.getValue().getUse();
                float use2 = o2.getValue().getUse();
                if (use1 == use2) {
                    return 0;
                } else {
                    return use1 > use2 ? -1 : 1;//降序排序
                }
            }
        });
        return mapResultList;
    }

    //统计所有pm的胜率、登场率、招式使用率
    public List<MapResult> mapReduceAllDetails(Query query) throws Exception {
        Long total = 2 * mongoTemplate.count(query, "battle");
        String mapFunction = "function(){" +
                "for(var i=0;i<this.teams.length;++i ){" +
                "   for(var j=0;j<this.teams[i].pokemons.length;++j){" +
                "       var moves={};var object={};" +
                "       for(var k=0;k<this.teams[i].pokemons[j].moves.length;++k){" +
                "           moves[this.teams[i].pokemons[j].moves[k]]=1;" +
                "       }" +
                "       object['moves']=moves;object['total']=" +total+";object['use']=1;"+
                "       if(this.winner==this.teams[i].playerName){" +
                "           object['win']=1;}" +
                "       else{" +
                "           object['win']=0;" +
                "           }" +
                "       emit(this.teams[i].pokemons[j].name,object);"+
                "   }" +
                "}" +
                "}";
        String reduceFunction = "function(keys,values){" +
                "var use=0; var win=0;var object={};var moves={};" +
                "for(var i=0;i<values.length;++i){" +
                "   use = use + values[i].use;" +
                "   win = win + values[i].win;" +
                "   for(var key in values[i].moves){"+
                "   if(moves.hasOwnProperty(key))"+
                "      {moves[key]= values[i].moves[key] +parseInt(moves[key]);}"+
                "   else{moves[key]= values[i].moves[key] ;}"+
                "   }"  +
                "}" +
                "object['use']=use;object['win']=win;object['total']=values[0].total;"+
                "object['moves']=moves;"+
                "return (values[0]._id,object);" +
                "}";

        MapReduceOptions mapReduceOptions = new MapReduceOptions();
        mapReduceOptions.outputDatabase("moveTest");
        mapReduceOptions.outputCollection("move");
        MapReduceResults<MapResult> results =
                mongoTemplate.mapReduce(query, "battle", mapFunction, reduceFunction,mapReduceOptions, MapResult.class);
        if (results == null) {
            throw new Exception("mapReduce null!");
        }
        Iterator<MapResult> iterator = results.iterator();
        List<MapResult> mapResultList = new ArrayList<>(400);
        while (iterator.hasNext()) {
            mapResultList.add(iterator.next());
        }
        Collections.sort(mapResultList, new Comparator<MapResult>() {
            @Override
            public int compare(MapResult o1, MapResult o2) {
                float use1 = o1.getValue().getUse();
                float use2 = o2.getValue().getUse();
                if (use1 == use2) {
                    return 0;
                } else {
                    return use1 > use2 ? -1 : 1;//降序排序
                }
            }
        });
        for (int i = 0; i < mapResultList.size(); i++) {
            MapResult result =  mapResultList.get(i);
            sortAndReduceMap(result.getValue());
        }
        return mapResultList;
    }

    //对统计里的moves（hashmap类型）根据值降序排序,保留使用次数高的并转化为使用率
    private void sortAndReduceMap(Statistic statistic) {
        HashMap<String, Float> map = statistic.getMoves();
        List<Map.Entry<String,Float>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return (int)(o2.getValue()-o1.getValue()); //重写排序规则，小于0表示升序，大于0表示降序
            }
        });
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<String, Float> stringIntegerEntry = list.get(i);
            if (i >= 6) {
                map.remove(stringIntegerEntry.getKey());
            }
        }
        Float use = statistic.getUse();
        try {
            for (String key : map.keySet()) {
                map.put(key, map.get(key) / use);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Pair<Team, String>> Team1(int page,String tag,String pokemonName,String dayAfter,String dayBefore) {
        int num_perPage = 20;
        ArrayList<Team> teamList = new ArrayList<>();
        List<Pair<Team, String>> teams = new ArrayList<>();

        List<AggregationOperation> operations = new ArrayList<>();




        //设置页数条件
        operations.add(Aggregation.sort(Sort.by(Sort.Order.desc("date"))));
//        operations.add(Aggregation.unwind("teams"));//将一个文档根据teams拆成多个文档，方便后面查找
       //动态设置条件
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (!StringUtils.isEmpty(dayAfter)) {
            LocalDate after = LocalDate.parse(dayAfter, format);
            operations.add(Aggregation.match(Criteria.where("date").gte(after)));
        }
        if (!StringUtils.isEmpty(dayBefore)) {
            LocalDate before = LocalDate.parse(dayBefore, format);
            operations.add(Aggregation.match(Criteria.where("date").lte(before)));
        }
        if (!StringUtils.isEmpty(tag)) {
            operations.add(Aggregation.match(Criteria.where("teams.tagSet").is(tag)));
        }
        if (!StringUtils.isEmpty(pokemonName)) {
            operations.add(Aggregation.match(Criteria.where("teams.pokemons.name").is(pokemonName)));
        }
        operations.add(Aggregation.skip((long) (page - 1) * num_perPage));
        operations.add(Aggregation.limit(num_perPage));
        Aggregation aggregation = Aggregation.newAggregation(operations);

        List<Battle> battles = mongoTemplate.aggregate(aggregation, "battle",Battle.class).getMappedResults();
        for (Battle battle : battles) {
            for (Team team : battle.getTeams()) {
                if (team == null) {
                    continue;
                }
                //需要进一步过滤battle里不符合条件或者重复的队伍
                boolean canAdd = true;
                for (int i = 0; i < teamList.size(); ++i) {
                    //检查重复
                    if (team.equals(teamList.get(i))) {
                        canAdd = false;
                        break;
                    }
                }
                if (!StringUtils.isEmpty(pokemonName)) {
                    boolean hasSpecifyPokemon = false;
                    for (Pokemon pokemon : team.getPokemons()) {
                        if (pokemonName.equals(pokemon.getName())) {
                            hasSpecifyPokemon = true;
                        }
                    }
                    if (!hasSpecifyPokemon) {
                        canAdd = false;
                    }
                }
                if (!StringUtils.isEmpty(tag)) {
                    if (!team.getTagSet().contains(Tag.valueOf(tag))) {
                        canAdd = false;
                    }
                }
                if (canAdd) {
                    teamList.add(team);
                    Pair<Team, String> pair = new Pair<>(team, battle.getBattleID());
                    teams.add(pair);
                }
            }
        }
        return teams;

    }
}
