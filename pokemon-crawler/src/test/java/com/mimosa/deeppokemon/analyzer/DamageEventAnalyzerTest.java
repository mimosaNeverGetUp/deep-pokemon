/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.BattleStat;
import com.mimosa.deeppokemon.analyzer.entity.EventTarget;
import com.mimosa.deeppokemon.analyzer.entity.PlayerStat;
import com.mimosa.deeppokemon.analyzer.entity.PokemonBattleStat;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
class DamageEventAnalyzerTest {
    @Autowired
    private DamageEventAnalyzer damageEventAnalyzer;

    public static Stream<Arguments> provideAnalyzeParams() {
        return Stream.of(buildMoveDamageEvent(), buildHelmetDamageEvent());
    }

    public static Stream<Arguments> provideSwitchDamageEvent() {
        return Stream.of(buildSwitchDamageEvent());
    }

    @ParameterizedTest
    @MethodSource("provideAnalyzeParams")
    void analyze(BattleEvent event, BattleStat stat, BattleStatus status,
                 PokemonBattleStat targetStat, PokemonBattleStat exceptTargetStat,
                 PokemonBattleStat opponentTargetStat, PokemonBattleStat exceptOpponentTargetStat,
                 PokemonStatus targetStatus, PokemonStatus exceptTargetStatus) {
        Assertions.assertTrue(damageEventAnalyzer.supportAnalyze(event));
        damageEventAnalyzer.analyze(event, stat, status);
        Assertions.assertEquals(exceptTargetStat.getHealthValue(), targetStat.getHealthValue());
        Assertions.assertEquals(exceptTargetStat.getAttackValue(), targetStat.getAttackValue());
        Assertions.assertEquals(exceptOpponentTargetStat.getHealthValue(), opponentTargetStat.getHealthValue());
        Assertions.assertEquals(exceptOpponentTargetStat.getAttackValue(), opponentTargetStat.getAttackValue());
        Assertions.assertEquals(targetStatus.getHealth(), exceptTargetStatus.getHealth());
    }


    @ParameterizedTest
    @MethodSource("provideSwitchDamageEvent")
    void analyzeSwitchDamageEvent(BattleEvent event, BattleStat stat, BattleStatus status, PlayerStat playerStat,
                                  int exceptSwitchDamage) {
        damageEventAnalyzer.analyze(event, stat, status);
        Assertions.assertEquals(exceptSwitchDamage, playerStat.getSwitchDamage());
    }

    private static Arguments buildSwitchDamageEvent() {
        BattleEvent switchEvent = new BattleEvent("switch", null, null, null);
        switchEvent.setBattleEventStat(new MoveEventStat(new EventTarget(2, "Gliscor", "Gliscor")
                , "Knock Off"));
        BattleEvent damageEvent = new BattleEvent("damage", List.of("p1a: OLD DOG DIFFERENCE", "73/100"),
                switchEvent, null);
        PlayerStat p1 = new PlayerStat(1, "");
        PlayerStatus p1Status = new PlayerStatus();
        p1Status.setPokemonNickNameMap("OLD DOG DIFFERENCE", "Skarmory");
        p1Status.setTurnStartPokemonName("Skarmory");

        PokemonBattleStat skarmory = new PokemonBattleStat("Skarmory");
        p1.addPokemonBattleStat(skarmory);

        PokemonStatus skarmoryStatus = new PokemonStatus("Skarmory");
        p1Status.setPokemonStatus("Skarmory", skarmoryStatus);

        PlayerStat p2 = new PlayerStat(2, "");
        PlayerStatus p2Status = new PlayerStatus();
        p2Status.setPokemonNickNameMap("Gliscor", "Gliscor");

        PokemonBattleStat gliscor = new PokemonBattleStat("Gliscor");
        p2.addPokemonBattleStat(gliscor);

        BattleStatus battleStatus = new BattleStatus(List.of(p1Status, p2Status));
        BattleStat battleStat = new BattleStat(List.of(p1, p2));

        return Arguments.of(damageEvent, battleStat, battleStatus, p1, 27);
    }

    private static Arguments buildMoveDamageEvent() {
        BattleEvent moveEvent = new BattleEvent("move", null, null, null);
        moveEvent.setBattleEventStat(new MoveEventStat(new EventTarget(2, "Gliscor", "Gliscor")
                , "Knock Off"));
        BattleEvent damageEvent = new BattleEvent("damage", List.of("p1a: OLD DOG DIFFERENCE", "73/100"),
                moveEvent, null);
        PlayerStat p1 = new PlayerStat(1, "");
        PlayerStatus p1Status = new PlayerStatus();
        p1Status.setPokemonNickNameMap("OLD DOG DIFFERENCE", "Skarmory");
        p1Status.setTurnStartPokemonName("Skarmory");

        PokemonBattleStat skarmory = new PokemonBattleStat("Skarmory");
        p1.addPokemonBattleStat(skarmory);
        PokemonBattleStat exceptSkarmory = new PokemonBattleStat("Skarmory");
        exceptSkarmory.setHealthValue(-27);
        exceptSkarmory.setAttackValue(0);

        PokemonStatus skarmoryStatus = new PokemonStatus("Skarmory");
        PokemonStatus exceptSkarmoryStatus = new PokemonStatus("Skarmory");
        exceptSkarmoryStatus.setHealth(73);
        p1Status.setPokemonStatus("Skarmory", skarmoryStatus);


        PlayerStat p2 = new PlayerStat(2, "");
        PlayerStatus p2Status = new PlayerStatus();
        p2Status.setPokemonNickNameMap("Gliscor", "Gliscor");

        PokemonBattleStat gliscor = new PokemonBattleStat("Gliscor");
        PokemonBattleStat exceptGliscor = new PokemonBattleStat("Gliscor");
        p2.addPokemonBattleStat(gliscor);
        exceptGliscor.setAttackValue(27);
        exceptGliscor.setHealthValue(27);

        BattleStatus battleStatus = new BattleStatus(List.of(p1Status, p2Status));
        BattleStat battleStat = new BattleStat(List.of(p1, p2));

        return Arguments.of(damageEvent, battleStat, battleStatus, skarmory, exceptSkarmory, gliscor, exceptGliscor,
                skarmoryStatus, exceptSkarmoryStatus);
    }

    private static Arguments buildHelmetDamageEvent() {
        BattleEvent moveEvent = new BattleEvent("move", null, null, null);
        moveEvent.setBattleEventStat(new MoveEventStat(new EventTarget(2, "Gliscor", "Gliscor")
                , "Knock Off"));
        BattleEvent damageEvent = new BattleEvent("damage", List.of("p2a: Gliscor", "84/100"
                , "[from] item: Rocky Helmet", "[of] p1a: OLD DOG DIFFERENCE"), moveEvent, null);
        PlayerStat p1 = new PlayerStat(1, "");
        PlayerStatus p1Status = new PlayerStatus();
        p1Status.setPokemonNickNameMap("OLD DOG DIFFERENCE", "Skarmory");

        PokemonBattleStat skarmory = new PokemonBattleStat("Skarmory");
        p1.addPokemonBattleStat(skarmory);
        PokemonBattleStat exceptSkarmory = new PokemonBattleStat("Skarmory");
        exceptSkarmory.setHealthValue(16);
        exceptSkarmory.setAttackValue(16);

        PlayerStat p2 = new PlayerStat(2, "");
        PlayerStatus p2Status = new PlayerStatus();
        p2Status.setTurnStartPokemonName("Gliscor");
        p2Status.setPokemonNickNameMap("Gliscor", "Gliscor");

        PokemonBattleStat gliscor = new PokemonBattleStat("Gliscor");
        PokemonBattleStat exceptGliscor = new PokemonBattleStat("Gliscor");
        p2.addPokemonBattleStat(gliscor);
        exceptGliscor.setAttackValue(0);
        exceptGliscor.setHealthValue(-16);
        PokemonStatus gliscorStatus = new PokemonStatus("Gliscor");
        PokemonStatus exceptGliscorStatus = new PokemonStatus("Gliscor");
        exceptGliscorStatus.setHealth(84);
        p2Status.setPokemonStatus("Gliscor", gliscorStatus);

        BattleStatus battleStatus = new BattleStatus(List.of(p1Status, p2Status));
        BattleStat battleStat = new BattleStat(List.of(p1, p2));

        return Arguments.of(damageEvent, battleStat, battleStatus, gliscor, exceptGliscor, skarmory, exceptSkarmory,
                gliscorStatus, exceptGliscorStatus);
    }
}