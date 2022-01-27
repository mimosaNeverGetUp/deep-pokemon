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

package com.mimosa.deeppokemon.tagger;

import com.mimosa.deeppokemon.entity.PokemonInfo;
import com.mimosa.deeppokemon.entity.Tag;
import com.mimosa.deeppokemon.entity.Type;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @program: deep-pokemon
 * @description: 宝可梦特性分类标签类
 * @author: mimosa
 * @create: 2020//10//23
 */
@Component
public class PokemonAbilityTagProvider implements  PokemonTagProvider {


    private final static HashSet<String> ABILITIES_ATTACK_BAD = new HashSet<>();
    private final static HashSet<String> ABILITIES_ATTACK_GOOD = new HashSet<>();
    private final static HashSet<String> ABILITIES_ATTACK_PRETTY = new HashSet<>();

    private final static HashSet<String> ABILITIES_DEFENSE_BAD = new HashSet<>();
    private final static HashSet<String> ABILITIES_DEFENSE_GOOD = new HashSet<>();
    private final static HashSet<String> ABILITIES_DEFENSE_PRETTY = new HashSet<>();

    private final static HashSet<String> ABILITIES_WEATHER = new HashSet<>();

    //硬编码，需要维护特性名单
    static {
        String[] attack_bad={"Iron Fist","Skill Link","Tinted Lens","Reckless","Defiant","Infiltrator","Moxie"
        ,"Normalize","Tough Claws","Pixilate","Aerilate","Psychic Surge","Electric Surge","Grassy Surge","Punk Rock"};
        String[] attack_good={"Guts","Adaptability","Mold Breaker","Contrary"
        ,"Protean","Battle Bond","Transistor","Dragon's Maw","Libero","Soul-Heart","Beast Boost"};
        String[] attack_pretty={"Huge Power","Magnet Pull","Stance Change"};

        String[] defence_bad={"Volt Absorb","Sturdy","Static","Water Absorb","Flash Fire","Levitate","Rough Skin"
        ,"Natural Cure","Thick Fat","Flame Body","Marvel Scale","Storm Drain","Sap Sipper","Stamina","Triage"};
        String[] defence_good={"Unaware","Regenerator","Fluffy","Poison Heal","Disguise","Intimidate"};
        String[] defence_pretty={"Magic Guard","Imposter","Magic Bounce"};


        String[] weathers={"Sand Stream","Drizzle","Drought","Grassy Surge","Misty Surge","Psychic Surge"
        ,"Electric Surge"};

        ABILITIES_ATTACK_BAD.addAll(Arrays.asList(attack_bad));
        ABILITIES_ATTACK_GOOD.addAll(Arrays.asList(attack_good));
        ABILITIES_ATTACK_PRETTY.addAll(Arrays.asList(attack_pretty));

        ABILITIES_DEFENSE_BAD.addAll(Arrays.asList(defence_bad));
        ABILITIES_DEFENSE_GOOD.addAll(Arrays.asList(defence_good));
        ABILITIES_DEFENSE_PRETTY.addAll(Arrays.asList(defence_pretty));
    }

    @Override
    public void tag(PokemonInfo pokemonInfo) {
        int maxAttackLevel =0; //特性之中最好的进攻等级
        int maxDefenceLevel =0;//特性之中最好的防守等级

        for (String ability : pokemonInfo.getAbilities()) {

            if (ABILITIES_ATTACK_PRETTY.contains(ability)) {
                maxAttackLevel = 3;
            } else if (ABILITIES_ATTACK_GOOD.contains(ability)) {
                maxAttackLevel = Math.max(maxAttackLevel, 2);
            } else if (ABILITIES_ATTACK_BAD.contains(ability)) {
                maxAttackLevel = Math.max(maxAttackLevel, 1);
            }


            if (ABILITIES_DEFENSE_PRETTY.contains(ability)) {
                maxDefenceLevel = 3;
            } else if (ABILITIES_DEFENSE_GOOD.contains(ability)) {
                maxDefenceLevel = Math.max(maxDefenceLevel, 2);
            } else if (ABILITIES_DEFENSE_BAD.contains(ability)) {
                maxDefenceLevel = Math.max(maxDefenceLevel, 1);
            }
        }

        switch (maxAttackLevel) {
            case 1:
                pokemonInfo.addTag(Tag.ABILITY_ATTACK_BAD);
                break;
            case 2:
                pokemonInfo.addTag(Tag.ABILITY_ATTACK_GOOD);
                break;
            case 3:
                pokemonInfo.addTag(Tag.ABILITY_ATTACK_PRETTY);
        }
        switch (maxDefenceLevel) {
            case 1:
                pokemonInfo.addTag(Tag.ABILITY_DEFENCE_BAD);
                break;
            case 2:
                pokemonInfo.addTag(Tag.ABILITY_DEFENCE_GOOD);
                break;
            case 3:
                pokemonInfo.addTag(Tag.ABILITY_DEFENCE_PRETTY);
        }
    }
}
