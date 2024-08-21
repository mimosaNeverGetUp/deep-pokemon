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

import com.mimosa.deeppokemon.entity.BaseStats;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import org.springframework.stereotype.Component;

@Component
public class PokemonStatsLevelCrawler {
    private static final float DEFENSETHRESOLD_BAD = (80 * 2 + 204) * 1.1F * (80 * 2 + 99);
    private static final float DEFENSETHRESOLD_STANDARD = (85 * 2 + 204) * 1.1F * (85 * 2 + 99);
    private static final float DEFENSETHRESOLD_NROMAL = (90 * 2 + 204) * 1.1F * (90 * 2 + 99);
    private static final float DEFENSETHRESOLD_GOOD = (100 * 2 + 204) * 1.1F * (100 * 2 + 99);
    private static final float DEFENSETHRESOLD_EXCELLENT = (105 * 2 + 204) * 1.1F * (105 * 2 + 99);
    private static final float DEFENSETHRESOLD_OUTSTANDING = (120 * 2 + 204) * 1.1F * (120 * 2 + 99);
    private static final float DEFENSETHRESOLD_PRETTY = (125 * 2 + 204) * 1.1F * (125 * 2 + 99);

    private float getEndurance(int hp, int def) {
        return (hp * 2 + 204) * 1.1F * (def * 2 + 99);
    }

    public float getDefLevel(PokemonInfo pokemonInfo) {
        BaseStats baseStats = pokemonInfo.getBaseStats();
        int hp = baseStats.getHp();
        int def = baseStats.getDef();
        float enduranceDef = getEndurance(hp, def);
        if (enduranceDef < DEFENSETHRESOLD_BAD) {
            return 1;
        } else if (enduranceDef < DEFENSETHRESOLD_STANDARD) {
            return 2;
        } else if (enduranceDef < DEFENSETHRESOLD_NROMAL) {
            return 2.5F;
        } else if (enduranceDef < DEFENSETHRESOLD_GOOD) {
            return 3;
        } else if (enduranceDef < DEFENSETHRESOLD_EXCELLENT) {
            return 3.5F;
        } else if (enduranceDef < DEFENSETHRESOLD_OUTSTANDING) {
            return 4F;
        }  else if (enduranceDef < DEFENSETHRESOLD_PRETTY) {
            return 4.5F;
        } else {
            return 5F;
        }
    }

    public float getSpdLevel(PokemonInfo pokemonInfo) {
        BaseStats baseStats = pokemonInfo.getBaseStats();
        int hp = baseStats.getHp();
        int spd = baseStats.getSpd();
        float enduranceDef = getEndurance(hp, spd);
        if (enduranceDef < DEFENSETHRESOLD_BAD) {
            return 1;
        } else if (enduranceDef < DEFENSETHRESOLD_STANDARD) {
            return 2;
        } else if (enduranceDef < DEFENSETHRESOLD_NROMAL) {
            return 2.5F;
        } else if (enduranceDef < DEFENSETHRESOLD_GOOD) {
            return 3;
        } else if (enduranceDef < DEFENSETHRESOLD_EXCELLENT) {
            return 3.5F;
        } else if (enduranceDef < DEFENSETHRESOLD_OUTSTANDING) {
            return 4F;
        }  else if (enduranceDef < DEFENSETHRESOLD_PRETTY) {
            return 4.5F;
        } else {
            return 5F;
        }
    }

    public float getAtkLevel(PokemonInfo pokemonInfo) {
        BaseStats baseStats = pokemonInfo.getBaseStats();
        int atk = baseStats.getAtk();
        if (atk < 85) {
            return 1;
        } else if (atk < 100) {
            return 2;
        } else if (atk < 115) {
            return 3;
        } else if (atk < 120) {
            return 3.5F;
        } else if (atk < 135) {
            return 4;
        } else if (atk < 140) {
            return 4.5F;
        } else {
            return 5;
        }
    }

    public float getSatkLevel(PokemonInfo pokemonInfo) {
        BaseStats baseStats = pokemonInfo.getBaseStats();
        int def = baseStats.getSpa();
        if (def < 85) {
            return 1;
        } else if (def < 100) {
            return 2;
        } else if (def < 115) {
            return 3;
        } else if (def < 120) {
            return 3.5F;
        } else if (def < 135) {
            return 4;
        } else if (def < 140) {
            return 4.5F;
        } else {
            return 5;
        }
    }
}