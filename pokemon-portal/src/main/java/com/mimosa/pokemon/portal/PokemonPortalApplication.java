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

package com.mimosa.pokemon.portal;


import com.mimosa.deeppokemon.entity.*;
import com.mimosa.pokemon.portal.dto.BattleDto;
import com.mimosa.pokemon.portal.dto.PlayerRankDTO;
import com.mimosa.pokemon.portal.dto.TeamGroupDto;
import com.mimosa.pokemon.portal.entity.PageResponse;
import org.bson.types.Binary;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;

/**
 * deeppokemon应用入口
 * 使用scanBasePackages导入其他模块
 *
 * @author huangxiaocong(2070132549 @ qq.com)
 */
@EnableCaching
@EnableFeignClients
@ImportRuntimeHints(PokemonPortalApplication.ApplicationRuntimeHints.class)
@SpringBootApplication
public class PokemonPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(PokemonPortalApplication.class, args);
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
                                    TreeMap.class),
                            TypeHint.builtWith(INVOKE_PUBLIC_CONSTRUCTORS, INVOKE_PUBLIC_METHODS));
            hints.serialization()
                    .registerType(PageResponse.class)
                    .registerType(BattleDto.class)
                    .registerType(BattleTeam.class)
                    .registerType(Pokemon.class)
                    .registerType(TeamGroupDto.class)
                    .registerType(TeamSet.class)
                    .registerType(PokemonBuildSet.class)
                    .registerType(PlayerRankDTO.class)
                    .registerType(ArrayList.class)
                    .registerType(LinkedList.class)
                    .registerType(HashSet.class)
                    .registerType(TreeSet.class)
                    .registerType(ConcurrentHashMap.class)
                    .registerType(TypeReference.of("java.util.Collections$UnmodifiableCollection"))
                    .registerType(TypeReference.of("java.util.Collections$UnmodifiableList"))
                    .registerType(TypeReference.of("java.util.Collections$UnmodifiableSet"))
                    .registerType(TypeReference.of("java.util.Collections$UnmodifiableMap"))
                    .registerType(TypeReference.of("java.util.Collections$UnmodifiableSortedMap"))
                    .registerType(TypeReference.of("java.util.Collections$UnmodifiableSortedSet"))
                    .registerType(TypeReference.of("java.util.Collections$UnmodifiableRandomAccessList"))
                    .registerType(TypeReference.of("java.util.Collections$UnmodifiableEntrySet"))
                    .registerType(TypeReference.of("java.util.Collections$UnmodifiableEntry"))
                    .registerType(TypeReference.of("java.util.Collections$UnmodifiableNavigableMap"))
                    .registerType(TypeReference.of("java.util.Collections$EmptyList"))
                    .registerType(TypeReference.of("java.util.Collections$EmptySet"))
                    .registerType(TypeReference.of("java.util.Collections$EmptyMap"))
                    .registerType(LinkedHashMap.class)
                    .registerType(LinkedHashSet.class)
                    .registerType(Number.class)
                    .registerType(TreeMap.class)
                    .registerType(LocalDate.class)
                    .registerType(TypeReference.of("java.time.Ser"))
                    .registerType(Binary.class)
                    .registerType(Tag.class);
        }
    }
}