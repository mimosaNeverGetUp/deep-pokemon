/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.*;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.DamageEventStat;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatusBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
class DamageEventAnalyzerTest {
    public static final String HIPPOWDONW = "Hippowdonw";
    public static final String ZAPDOS = "Zapdos";
    public static final String RAGING_BOLT = "Raging Bolt";
    public static final String SKELEDIRGE = "Skeledirge";

    @Autowired
    private DamageEventAnalyzer damageEventAnalyzer;

    public static Stream<Arguments> provideAnalyzeParams() {
        return Stream.of(buildMoveDamageEvent(), buildHelmetDamageEvent(), buildMoveDamageFaintEvent());
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
        Assertions.assertInstanceOf(DamageEventStat.class, event.getBattleEventStat());
        DamageEventStat damageEventStat = (DamageEventStat) event.getBattleEventStat();
        Assertions.assertNotNull(damageEventStat.damageOf());
        Assertions.assertNotNull(damageEventStat.eventTarget());
        if (event.getParentEvent() == null || !"move".equals(event.getParentEvent().getType())) {
            Assertions.assertNotNull(damageEventStat.damageFrom());
        }
        Assertions.assertNotEquals(0, damageEventStat.healthDiff());
    }


    @ParameterizedTest
    @MethodSource("provideSwitchDamageEvent")
    void analyzeSwitchDamageEvent(BattleEvent event, BattleStat stat, BattleStatus status, PlayerStat playerStat,
                                  int exceptSwitchDamage) {
        damageEventAnalyzer.analyze(event, stat, status);
        Assertions.assertEquals(exceptSwitchDamage, playerStat.getSwitchDamage());
    }

    @Test
    void analyzeSideDamage() {
        BattleEvent switchEvent = new BattleEvent("switch", null, null, null);
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p1a: Gholdengo", "94/100", "[from] Stealth Rock"), switchEvent, null);
        int sideFromPlayerNumber = 2;
        String skarmory = "Skarmory";
        String gholdengo = "Gholdengo";
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(sideFromPlayerNumber, skarmory, skarmory)
                .addPokemon(1, gholdengo, gholdengo)
                .addSide(1, new Side("Stealth Rock", new EventTarget(sideFromPlayerNumber, skarmory, skarmory)))
                .setTurnStartPokemon(1, gholdengo)
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, gholdengo)
                .addPokemonStat(sideFromPlayerNumber, skarmory)
                .build();
        damageEventAnalyzer.analyze(battleEvent, battleStat, battleStatus);
        PokemonBattleStat skarmoryStat = battleStat.playerStatList().get(sideFromPlayerNumber - 1)
                .getPokemonBattleStat(skarmory);
        Assertions.assertEquals(6, skarmoryStat.getAttackValue());
        Assertions.assertEquals(6, skarmoryStat.getHealthValue());
    }

    @Test
    void analyzeWeatherDamage() {
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p1a: Zapdos", "94/100", "[from] Sandstorm"), null, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(2, HIPPOWDONW, HIPPOWDONW)
                .addPokemon(1, ZAPDOS, ZAPDOS)
                .setWeather(new Weather("Sandstorm", new EventTarget(2, HIPPOWDONW, HIPPOWDONW)))
                .setTurnStartPokemon(1, "Zapdos")
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, ZAPDOS)
                .addPokemonStat(2, HIPPOWDONW)
                .build();

        damageEventAnalyzer.analyze(battleEvent, battleStat, battleStatus);
        PokemonBattleStat hippowdonwStat = battleStat.playerStatList().get(1)
                .getPokemonBattleStat(HIPPOWDONW);
        Assertions.assertEquals(6, hippowdonwStat.getAttackValue());
        Assertions.assertEquals(6, hippowdonwStat.getHealthValue());

        PokemonBattleStat zapdosStat = battleStat.playerStatList().get(0)
                .getPokemonBattleStat(ZAPDOS);
        Assertions.assertEquals(0, zapdosStat.getAttackValue());
        Assertions.assertEquals(-6, zapdosStat.getHealthValue());
    }

    @Test
    void analyzeStatusDamage() {
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p2a: Raging Bolt", "80/100 brn", "[from] " +
                "brn"), null, null);
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(2, RAGING_BOLT, RAGING_BOLT)
                .addPokemon(1, SKELEDIRGE, SKELEDIRGE)
                .setStatus(2, RAGING_BOLT, new Status("brn", new EventTarget(1, SKELEDIRGE, SKELEDIRGE)))
                .setTurnStartPokemon(2, RAGING_BOLT)
                .setHealth(2, RAGING_BOLT, 86)
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, SKELEDIRGE)
                .addPokemonStat(2, RAGING_BOLT)
                .build();
        
        damageEventAnalyzer.analyze(battleEvent, battleStat, battleStatus);
        PokemonBattleStat ragingboltStat = battleStat.playerStatList().get(1)
                .getPokemonBattleStat(RAGING_BOLT);
        Assertions.assertEquals(0, ragingboltStat.getAttackValue());
        Assertions.assertEquals(-6, ragingboltStat.getHealthValue());

        PokemonBattleStat skeledirgeStat = battleStat.playerStatList().get(0)
                .getPokemonBattleStat(SKELEDIRGE);
        Assertions.assertEquals(6, skeledirgeStat.getAttackValue());
        Assertions.assertEquals(6, skeledirgeStat.getHealthValue());
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
        p1Status.setTurnStartPokemonName(1, "Skarmory");

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
        int damageTargetPlayerNumber = 1;
        int movePlayerNumber = 2;
        String skarmory = "Skarmory";
        String gliscor = "Gliscor";
        moveEvent.setBattleEventStat(new MoveEventStat(new EventTarget(movePlayerNumber, gliscor, gliscor)
                , "Knock Off"));
        BattleEvent damageEvent = new BattleEvent("damage", List.of("p1a: OLD DOG DIFFERENCE", "73/100"),
                moveEvent, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(damageTargetPlayerNumber, skarmory)
                .addPokemonStat(movePlayerNumber, gliscor)
                .build();
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(1, skarmory, "OLD DOG DIFFERENCE")
                .addPokemon(2, gliscor, gliscor)
                .setTurnStartPokemon(1, skarmory)
                .build();


        PokemonBattleStat exceptSkarmory = new PokemonBattleStat(skarmory);
        exceptSkarmory.setHealthValue(-27);
        exceptSkarmory.setAttackValue(0);
        PokemonStatus exceptSkarmoryStatus = new PokemonStatus(skarmory);
        exceptSkarmoryStatus.setHealth(73);
        PokemonBattleStat exceptGliscor = new PokemonBattleStat(gliscor);
        exceptGliscor.setAttackValue(27);
        exceptGliscor.setHealthValue(27);


        PokemonBattleStat skyStat = battleStat.playerStatList().get(damageTargetPlayerNumber - 1).getPokemonBattleStat(
                skarmory);
        PokemonBattleStat gliscorStat = battleStat.playerStatList().get(movePlayerNumber - 1).getPokemonBattleStat(
                gliscor);
        PokemonStatus skyStatus = battleStatus.getPlayerStatusList().get(damageTargetPlayerNumber-1).getPokemonStatus(skarmory);

        return Arguments.of(damageEvent, battleStat, battleStatus, skyStat,
                exceptSkarmory, gliscorStat, exceptGliscor, skyStatus, exceptSkarmoryStatus);
    }

    private static Arguments buildMoveDamageFaintEvent() {
        BattleEvent moveEvent = new BattleEvent("move", null, null, null);
        int damageTargetPlayerNumber = 1;
        int movePlayerNumber = 2;
        String skarmory = "Skarmory";
        String gliscor = "Gliscor";
        moveEvent.setBattleEventStat(new MoveEventStat(new EventTarget(movePlayerNumber, gliscor, gliscor)
                , "Knock Off"));
        BattleEvent damageEvent = new BattleEvent("damage", List.of("p1a: OLD DOG DIFFERENCE", "0 fnt"),
                moveEvent, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(damageTargetPlayerNumber, skarmory)
                .addPokemonStat(movePlayerNumber, gliscor)
                .build();
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(1, skarmory, "OLD DOG DIFFERENCE")
                .addPokemon(2, gliscor, gliscor)
                .setTurnStartPokemon(1, skarmory)
                .build();


        PokemonBattleStat exceptSkarmory = new PokemonBattleStat(skarmory);
        exceptSkarmory.setHealthValue(-100);
        exceptSkarmory.setAttackValue(0);
        PokemonStatus exceptSkarmoryStatus = new PokemonStatus(skarmory);
        exceptSkarmoryStatus.setHealth(0);
        PokemonBattleStat exceptGliscor = new PokemonBattleStat(gliscor);
        exceptGliscor.setAttackValue(100);
        exceptGliscor.setHealthValue(100);


        PokemonBattleStat skyStat = battleStat.playerStatList().get(damageTargetPlayerNumber - 1).getPokemonBattleStat(
                skarmory);
        PokemonBattleStat gliscorStat = battleStat.playerStatList().get(movePlayerNumber - 1).getPokemonBattleStat(
                gliscor);
        PokemonStatus skyStatus = battleStatus.getPlayerStatusList().get(damageTargetPlayerNumber-1).getPokemonStatus(skarmory);

        return Arguments.of(damageEvent, battleStat, battleStatus, skyStat,
                exceptSkarmory, gliscorStat, exceptGliscor, skyStatus, exceptSkarmoryStatus);
    }

    private static Arguments buildHelmetDamageEvent() {
        int damageTargetPlayerNumber = 2;
        int helmetPlayerNumber = 1;
        String skarmory = "Skarmory";
        String gliscor = "Gliscor";

        BattleEvent moveEvent = new BattleEvent("move", null, null, null);
        moveEvent.setBattleEventStat(new MoveEventStat(new EventTarget(2, "Gliscor", "Gliscor")
                , "Knock Off"));
        BattleEvent damageEvent = new BattleEvent("damage", List.of("p2a: Gliscor", "84/100"
                , "[from] item: Rocky Helmet", "[of] p1a: OLD DOG DIFFERENCE"), moveEvent, null);
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(helmetPlayerNumber, skarmory)
                .addPokemonStat(damageTargetPlayerNumber, gliscor)
                .build();
        BattleStatus battleStatus = new BattleStatusBuilder()
                .addPokemon(helmetPlayerNumber, skarmory, "OLD DOG DIFFERENCE")
                .addPokemon(damageTargetPlayerNumber, gliscor, gliscor)
                .setTurnStartPokemon(damageTargetPlayerNumber, gliscor)
                .build();

        PokemonBattleStat exceptSkarmory = new PokemonBattleStat("Skarmory");
        exceptSkarmory.setHealthValue(16);
        exceptSkarmory.setAttackValue(16);

        PokemonBattleStat exceptGliscor = new PokemonBattleStat("Gliscor");
        exceptGliscor.setAttackValue(0);
        exceptGliscor.setHealthValue(-16);
        PokemonStatus exceptGliscorStatus = new PokemonStatus("Gliscor");
        exceptGliscorStatus.setHealth(84);

        PokemonBattleStat gliscorStat =
                battleStat.playerStatList().get(damageTargetPlayerNumber - 1).getPokemonBattleStat(gliscor);
        PokemonBattleStat skarmoryStat =
                battleStat.playerStatList().get(helmetPlayerNumber - 1).getPokemonBattleStat(skarmory);
        PokemonStatus gliscorStatus =
                battleStatus.getPlayerStatusList().get(damageTargetPlayerNumber - 1).getPokemonStatus(gliscor);

        return Arguments.of(damageEvent, battleStat, battleStatus, gliscorStat, exceptGliscor, skarmoryStat,
                exceptSkarmory, gliscorStatus, exceptGliscorStatus);
    }
}