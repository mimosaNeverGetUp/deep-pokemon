/*
 * The MIT License
 *
 * Copyright (c) [2023]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mimosa.pokemon.portal.entity.stat;

import java.util.List;

public class PokemonMoveStat {
    private String name;
    private long use;
    private List<PokemonMoveUsageStat> moveUsage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUse() {
        return use;
    }

    public void setUse(long use) {
        this.use = use;
    }

    public List<PokemonMoveUsageStat> getMoveUsage() {
        return moveUsage;
    }

    public void setMoveUsage(List<PokemonMoveUsageStat> moveUsage) {
        this.moveUsage = moveUsage;
    }

    public static class PokemonMoveUsageStat {
        private String name;
        private double usePercent;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getUsePercent() {
            return usePercent;
        }

        public void setUsePercent(double usePercent) {
            this.usePercent = usePercent;
        }
    }
}
