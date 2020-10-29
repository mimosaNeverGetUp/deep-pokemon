package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.deeppokemon.service.BattleService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@RunWith(SpringRunner.class)
class TeamPopularTagProviderTest {

    @Autowired
    private BattleService battleService;

    @Autowired
    private TeamPopularTagProvider teamPopularTagProvider;
    @Test
    void tag() {
        List<Battle> battleList = battleService.find100BattleSortByDate();
        for (Battle battle : battleList) {
            for (Team team : battle.getTeams()) {
                teamPopularTagProvider.tag(team);
                if (team.getTagSet() != null) {
                    for (Pokemon pokemon : team.getPokemons()) {
                        System.out.println(pokemon.getName());
                    }
                    System.out.println(team.getTagSet());
                }
            }
        }
    }
}