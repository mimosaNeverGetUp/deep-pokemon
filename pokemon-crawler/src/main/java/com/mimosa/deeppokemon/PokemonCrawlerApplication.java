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

package com.mimosa.deeppokemon;

import com.mimosa.deeppokemon.crawler.LadderCrawler;


import com.mimosa.deeppokemon.entity.Replay;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;

@SpringBootApplication
@EnableCaching
@ImportRuntimeHints(PokemonCrawlerApplication.ApplicationRuntimeHints.class)
public class PokemonCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PokemonCrawlerApplication.class, args);
    }

    @Bean
    @Primary
    LadderCrawler crawler() {
        return new LadderCrawler("gen9ou", 1,
                200, 1600, LocalDate.now().minusMonths(1), 60.0f);
    }


    /**
     * graalvm runtime hint
     */
    static class ApplicationRuntimeHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection()
                    .registerTypes(TypeReference.listOf(
                                    ArrayList.class,
                                    LinkedList.class,
                                    HashSet.class,
                                    TreeSet.class,
                                    ConcurrentHashMap.class,
                                    LinkedHashMap.class,
                                    TreeMap.class,
                                    Replay.class),
                            TypeHint.builtWith(INVOKE_PUBLIC_CONSTRUCTORS, INVOKE_PUBLIC_METHODS));
        }
    }
}