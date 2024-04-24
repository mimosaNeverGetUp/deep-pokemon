/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class BattleEventParserTest {
    public static final int EXCEPT_CHILDREN_EVENT = 107;
    public static final int EXCEPT_ALL_EVENT = 368;
    @Autowired
    private BattleEventParser battleEventParser;

    @Value("classpath:battlereplay/smogtours-gen9ou-746547")
    private Resource battereplayResource;

    private final Set<String> noContentEvent = Set.of("clearpoke",
            "teampreview", "start", "upkeep");

    public static Stream<Arguments> provideParseParam() {
        return Stream.of(
                Arguments.of("start", "", true, "|start"),
                Arguments.of("switch", "p1a: Serperior|Serperior, F|100/100", true, "|switch|p1a: Serperior|Serperior, F|100/100"),
                Arguments.of("activate", "p2a: Roaring Moon|ability: Protosynthesis|[fromitem]", false, "|-activate|p2a: Roaring Moon|ability: Protosynthesis|[fromitem]"),
                Arguments.of("turn", "1", true, "|turn|1"),
                Arguments.of("move", "p2a: Roaring Moon|Knock Off|p1a: Raging Bolt", true, "|move|p2a: Roaring Moon|Knock Off|p1a: Raging Bolt")
        );
    }

    @Test
    void parse() throws IOException {
        List<BattleEvent> battleEvents =
                battleEventParser.parse(battereplayResource.getContentAsString(StandardCharsets.UTF_8));
        Assertions.assertNotNull(battleEvents);
        Assertions.assertEquals(EXCEPT_ALL_EVENT - EXCEPT_CHILDREN_EVENT, battleEvents.size());
        battleEvents.forEach(battleEvent -> {
            Assertions.assertFalse(battleEvent.getType() == null || battleEvent.getType().isEmpty());
            Assertions.assertFalse(battleEvent.getContents() == null
                    && !noContentEvent.contains(battleEvent.getType()));
            Assertions.assertNull(battleEvent.getParentEvent());
        });
        Assertions.assertTrue(battleEvents.stream()
                .flatMap(battleEvent -> Stream.concat(Stream.of(battleEvent), battleEvent.getChildrenEvents().stream()))
                .map(BattleEvent::getType)
                .collect(Collectors.toSet()).containsAll(
                        List.of("turn", "move", "damage", "start", "end", "sidestart", "boost")
                ));
        Assertions.assertEquals(EXCEPT_CHILDREN_EVENT, battleEvents.stream()
                .filter(battleEvent -> !battleEvent.getChildrenEvents().isEmpty())
                .mapToLong(battleEvent -> battleEvent.getChildrenEvents().size())
                .sum());

    }
}