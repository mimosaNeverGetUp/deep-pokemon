/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.analyzer;

import com.mimosa.deeppokemon.analyzer.entity.*;
import com.mimosa.deeppokemon.analyzer.entity.event.BattleEvent;
import com.mimosa.deeppokemon.analyzer.entity.event.DamageEventStat;
import com.mimosa.deeppokemon.analyzer.entity.event.EndEventStat;
import com.mimosa.deeppokemon.analyzer.entity.event.MoveEventStat;
import com.mimosa.deeppokemon.analyzer.entity.status.BattleContext;
import com.mimosa.deeppokemon.analyzer.entity.status.PlayerStatus;
import com.mimosa.deeppokemon.analyzer.entity.status.PokemonStatus;
import com.mimosa.deeppokemon.analyzer.util.BattleBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleStatBuilder;
import com.mimosa.deeppokemon.analyzer.util.BattleContextBuilder;
import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Pokemon;
import com.mimosa.deeppokemon.entity.stat.BattleStat;
import com.mimosa.deeppokemon.entity.stat.PlayerStat;
import com.mimosa.deeppokemon.entity.stat.PokemonBattleStat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
class DamageEventAnalyzerTest {
    public static final String HIPPOWDONW = "Hippowdonw";
    public static final String ZAPDOS = "Zapdos";
    public static final String RAGING_BOLT = "Raging Bolt";
    public static final String SKELEDIRGE = "Skeledirge";
    private static final String SALT_CURE = "Salt Cure";
    private static final String IRON_VALIANT = "Iron Valiant";
    private static final String GARGANACL = "Garganacl";
    private static final String CORVIKNIGHT = "Corviknight";
    private static final String OGERPON = "Ogerpon";
    private static final String ROARING_MOON = "Roaring Moon";
    private static final String TOXAPEX = "Toxapex";
    private static final String DRAGONITE = "Dragonite";
    private static final String PECHARUNT = "Pecharunt";
    private static final String IRON_TREADS = "Iron Treads";

    @Autowired
    private DamageEventAnalyzer damageEventAnalyzer;

    public static Stream<Arguments> provideAnalyzeParams() {
        return Stream.of(buildMoveDamageEvent(), buildMoveDamageFaintEvent());
    }

    public static Stream<Arguments> provideHelmetAnalyzeParams() {
        return Stream.of(buildHelmetDamageEvent());
    }

    public static Stream<Arguments> provideSwitchDamageEvent() {
        return Stream.of(buildSwitchDamageEvent());
    }

    @ParameterizedTest
    @MethodSource("provideAnalyzeParams")
    void analyze(BattleEvent event, BattleStat stat, BattleContext status,
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

        Assertions.assertNotEquals(BigDecimal.valueOf(0.0), damageEventStat.healthDiff());
    }

    @ParameterizedTest
    @MethodSource("provideHelmetAnalyzeParams")
    void analyzeHelmet(BattleEvent event, BattleStat stat, BattleContext status,
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
        Assertions.assertNotEquals(BigDecimal.valueOf(0.0), damageEventStat.healthDiff());
        Pokemon opponentPokemon = status.getBattle().getBattleTeams().get(0).findPokemon(opponentTargetStat.getName());
        Assertions.assertEquals("Rocky Helmet", opponentPokemon.getItem());
    }

    @ParameterizedTest
    @MethodSource("provideSwitchDamageEvent")
    void analyzeSwitchDamageEvent(BattleEvent event, BattleStat stat, BattleContext status, PlayerStat playerStat,
                                  BigDecimal exceptSwitchDamage) {
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
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(sideFromPlayerNumber, skarmory, skarmory)
                .addPokemon(1, gholdengo, gholdengo)
                .addSide(1, new Side("Stealth Rock", new EventTarget(sideFromPlayerNumber, skarmory, skarmory)))
                .setTurnStartPokemon(1, gholdengo)
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, gholdengo)
                .addPokemonStat(sideFromPlayerNumber, skarmory)
                .build();
        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PokemonBattleStat skarmoryStat = battleStat.playerStatList().get(sideFromPlayerNumber - 1)
                .getPokemonBattleStat(skarmory);
        Assertions.assertEquals(BigDecimal.valueOf(6.0), skarmoryStat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(6.0), skarmoryStat.getHealthValue());
    }

    @Test
    void analyzeWeatherDamage() {
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p1a: Zapdos", "94/100", "[from] Sandstorm"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, HIPPOWDONW, HIPPOWDONW)
                .addPokemon(1, ZAPDOS, ZAPDOS)
                .setWeather(new Weather("Sandstorm", new EventTarget(2, HIPPOWDONW, HIPPOWDONW)))
                .setTurnStartPokemon(1, "Zapdos")
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, ZAPDOS)
                .addPokemonStat(2, HIPPOWDONW)
                .build();

        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PokemonBattleStat hippowdonwStat = battleStat.playerStatList().get(1)
                .getPokemonBattleStat(HIPPOWDONW);
        Assertions.assertEquals(BigDecimal.valueOf(6.0), hippowdonwStat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(6.0), hippowdonwStat.getHealthValue());

        PokemonBattleStat zapdosStat = battleStat.playerStatList().get(0)
                .getPokemonBattleStat(ZAPDOS);
        Assertions.assertEquals(BigDecimal.valueOf(0.0), zapdosStat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(-6.0), zapdosStat.getHealthValue());
    }

    @Test
    void analyzeSelfWeatherDamage() {
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p1a: Zapdos", "94/100", "[from] Sandstorm"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, HIPPOWDONW, HIPPOWDONW)
                .addPokemon(1, ZAPDOS, ZAPDOS)
                .setWeather(new Weather("Sandstorm", new EventTarget(1, ZAPDOS, ZAPDOS)))
                .setTurnStartPokemon(1, "Zapdos")
                .setTurnStartPokemon(2, HIPPOWDONW)
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, ZAPDOS)
                .addPokemonStat(2, HIPPOWDONW)
                .build();

        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PokemonBattleStat hippowdonwStat = battleStat.playerStatList().get(1)
                .getPokemonBattleStat(HIPPOWDONW);
        Assertions.assertEquals(BigDecimal.valueOf(6.0), hippowdonwStat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(6.0), hippowdonwStat.getHealthValue());

        PokemonBattleStat zapdosStat = battleStat.playerStatList().get(0)
                .getPokemonBattleStat(ZAPDOS);
        Assertions.assertEquals(BigDecimal.valueOf(0.0), zapdosStat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(-6.0), zapdosStat.getHealthValue());
    }

    @Test
    void analyzeStatusDamage() {
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p2a: Raging Bolt", "80/100 brn", "[from] " +
                "brn"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, RAGING_BOLT, RAGING_BOLT)
                .addPokemon(1, SKELEDIRGE, SKELEDIRGE)
                .setStatus(2, RAGING_BOLT, new Status("brn", new EventTarget(1, SKELEDIRGE, SKELEDIRGE)))
                .setTurnStartPokemon(2, RAGING_BOLT)
                .setHealth(2, RAGING_BOLT, BigDecimal.valueOf(86))
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, SKELEDIRGE)
                .addPokemonStat(2, RAGING_BOLT)
                .build();

        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PokemonBattleStat ragingboltStat = battleStat.playerStatList().get(1)
                .getPokemonBattleStat(RAGING_BOLT);
        Assertions.assertEquals(BigDecimal.valueOf(0.0), ragingboltStat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(-6.0), ragingboltStat.getHealthValue());

        PokemonBattleStat skeledirgeStat = battleStat.playerStatList().get(0)
                .getPokemonBattleStat(SKELEDIRGE);
        Assertions.assertEquals(BigDecimal.valueOf(6.0), skeledirgeStat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(6.0), skeledirgeStat.getHealthValue());
    }

    @Test
    void analyzeToxicStatusDamage() {
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p2a: Roaring Moon", "0 fnt", "[from] " +
                "psn"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, ROARING_MOON, ROARING_MOON)
                .addPokemon(1, TOXAPEX, TOXAPEX)
                .setStatus(2, ROARING_MOON, new Status("tox", new EventTarget(1, TOXAPEX, TOXAPEX)))
                .setTurnStartPokemon(2, ROARING_MOON)
                .setHealth(2, ROARING_MOON, BigDecimal.valueOf(6))
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, TOXAPEX)
                .addPokemonStat(2, ROARING_MOON)
                .build();

        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PokemonBattleStat p2Stat = battleStat.playerStatList().get(1)
                .getPokemonBattleStat(ROARING_MOON);
        Assertions.assertEquals(BigDecimal.valueOf(0.0), p2Stat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(-6.0), p2Stat.getHealthValue());

        PokemonBattleStat p1Stat = battleStat.playerStatList().get(0)
                .getPokemonBattleStat(TOXAPEX);
        Assertions.assertEquals(BigDecimal.valueOf(6.0), p1Stat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(6.0), p1Stat.getHealthValue());
    }

    @Test
    void analyzeBuffDamage() {
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p2a: Iron Valiant", "88/100", "[from] Salt Cure"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, IRON_VALIANT, IRON_VALIANT)
                .addPokemon(1, GARGANACL, GARGANACL)
                .setTurnStartPokemon(2, IRON_VALIANT)
                .setBuffOf(2, IRON_VALIANT, SALT_CURE, new EventTarget(1, GARGANACL, GARGANACL))
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, IRON_VALIANT)
                .addPokemonStat(1, GARGANACL)
                .build();
        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PokemonBattleStat ironStat = battleStat.playerStatList().get(1).getPokemonBattleStat(IRON_VALIANT);
        Assertions.assertEquals(0, ironStat.getAttackValue().intValue());
        Assertions.assertEquals(-12, ironStat.getHealthValue().intValue());
        PokemonBattleStat garStat = battleStat.playerStatList().get(0).getPokemonBattleStat(GARGANACL);
        Assertions.assertEquals(12, garStat.getAttackValue().intValue());
        Assertions.assertEquals(12, garStat.getHealthValue().intValue());
    }

    @Test
    void analyzeRecoilDamage() {
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p1a: Corviknight", "0 fnt", "[from] Recoil"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, OGERPON, OGERPON)
                .addPokemon(1, CORVIKNIGHT, CORVIKNIGHT)
                .setActivePokemonName(2, OGERPON)
                .setTurnStartPokemon(1, CORVIKNIGHT)
                .setHealth(1, CORVIKNIGHT, BigDecimal.valueOf(22))
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, OGERPON)
                .addPokemonStat(1, CORVIKNIGHT)
                .build();
        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PokemonBattleStat ogerponStat = battleStat.playerStatList().get(1).getPokemonBattleStat(OGERPON);
        Assertions.assertEquals(22, ogerponStat.getAttackValue().intValue());
        Assertions.assertEquals(22, ogerponStat.getHealthValue().intValue());
        PokemonBattleStat corviknightStat = battleStat.playerStatList().get(0).getPokemonBattleStat(CORVIKNIGHT);
        Assertions.assertEquals(0, corviknightStat.getAttackValue().intValue());
        Assertions.assertEquals(-22, corviknightStat.getHealthValue().intValue());
    }

    @Test
    void analyzeSpecialRecoilDamage() {
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p1a: Iron Treads", "20/100", "[from] steelbeam"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, OGERPON, OGERPON)
                .addPokemon(1, IRON_TREADS, IRON_TREADS)
                .setActivePokemonName(2, OGERPON)
                .setTurnStartPokemon(1, IRON_TREADS)
                .setHealth(1, IRON_TREADS, BigDecimal.valueOf(71))
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, OGERPON)
                .addPokemonStat(1, IRON_TREADS)
                .build();
        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PokemonBattleStat ogerponStat = battleStat.playerStatList().get(1).getPokemonBattleStat(OGERPON);
        Assertions.assertEquals(51, ogerponStat.getAttackValue().intValue());
        Assertions.assertEquals(51, ogerponStat.getHealthValue().intValue());
        PokemonBattleStat corviknightStat = battleStat.playerStatList().get(0).getPokemonBattleStat(IRON_TREADS);
        Assertions.assertEquals(0, corviknightStat.getAttackValue().intValue());
        Assertions.assertEquals(-51, corviknightStat.getHealthValue().intValue());
    }

    @Test
    void analyzeInfestDamage() {
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p1a: oops??", "18/100", "[from] move: Infestation"),
                null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, TOXAPEX, TOXAPEX)
                .addPokemon(1, IRON_VALIANT, "oops??")
                .addActivateStatus(1, IRON_VALIANT, new ActivateStatus("move: Infestation", "move", "Infestation",
                        new EventTarget(2, TOXAPEX, TOXAPEX)))
                .setTurnStartPokemon(2, TOXAPEX)
                .setTurnStartPokemon(1, IRON_VALIANT)
                .setHealth(1, IRON_VALIANT, BigDecimal.valueOf(30))
                .build();
        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, IRON_VALIANT)
                .addPokemonStat(2, TOXAPEX)
                .build();

        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PokemonBattleStat p2Stat = battleStat.playerStatList().get(1)
                .getPokemonBattleStat(TOXAPEX);
        Assertions.assertEquals(BigDecimal.valueOf(12.0), p2Stat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(12.0), p2Stat.getHealthValue());

        PokemonBattleStat p1Stat = battleStat.playerStatList().get(0)
                .getPokemonBattleStat(IRON_VALIANT);
        Assertions.assertEquals(BigDecimal.valueOf(0.0), p1Stat.getAttackValue());
        Assertions.assertEquals(BigDecimal.valueOf(-12.0), p1Stat.getHealthValue());
    }

    @Test
    void analyzeConfusionDamage() {
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p1a: Dragonite", "0 fnt", "[from] confusion"), null, null);
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(2, PECHARUNT, PECHARUNT)
                .addPokemon(1, DRAGONITE, DRAGONITE)
                .setTurnStartPokemon(1, DRAGONITE)
                .setHealth(1, DRAGONITE, BigDecimal.valueOf(14))
                .setBuffOf(1, DRAGONITE, "confusion", new EventTarget(2, PECHARUNT, PECHARUNT))
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(2, PECHARUNT)
                .addPokemonStat(1, DRAGONITE)
                .build();
        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PokemonBattleStat p2Stat = battleStat.playerStatList().get(1).getPokemonBattleStat(PECHARUNT);
        Assertions.assertEquals(14, p2Stat.getAttackValue().intValue());
        Assertions.assertEquals(14, p2Stat.getHealthValue().intValue());
        PokemonBattleStat p1Stat = battleStat.playerStatList().get(0).getPokemonBattleStat(DRAGONITE);
        Assertions.assertEquals(0, p1Stat.getAttackValue().intValue());
        Assertions.assertEquals(-14, p1Stat.getHealthValue().intValue());
    }

    @Test
    void analyzeFutureSightDamage() {
        String hatterene = "Hatterene";
        String ironValiant = "Iron Valiant";
        String futureSight = "Future Sight";
        String corviknight = "Corviknight";
        String glimmora = "Glimmora";

        BattleEvent endEvent = new BattleEvent("end", null, null, null);
        endEvent.setBattleEventStat(new EndEventStat(new EventTarget(1, hatterene, "Nothin' Under"), futureSight));
        BattleEvent battleEvent = new BattleEvent("damage", List.of("p2a: Iron Valiant", "0 fnt"), endEvent, null);

        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, hatterene, "Nothin' Under")
                .addPokemon(1, glimmora, glimmora)
                .addPokemon(2, ironValiant, ironValiant)
                .addPokemon(2, corviknight, corviknight)
                .setTurnStartPokemon(2, corviknight)
                .setTurnStartPokemon(1, glimmora)
                .setHealth(2, ironValiant, BigDecimal.valueOf(100))
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, hatterene)
                .addPokemonStat(1, glimmora)
                .addPokemonStat(2, ironValiant)
                .addPokemonStat(2, corviknight)
                .build();
        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        PokemonBattleStat p2Stat = battleStat.playerStatList().get(1).getPokemonBattleStat(corviknight);
        Assertions.assertEquals(0, p2Stat.getAttackValue().intValue());
        Assertions.assertEquals(-100, p2Stat.getHealthValue().intValue());
        p2Stat = battleStat.playerStatList().get(1).getPokemonBattleStat(ironValiant);
        Assertions.assertEquals(0, p2Stat.getAttackValue().intValue());
        Assertions.assertEquals(0, p2Stat.getHealthValue().intValue());

        PokemonBattleStat p1Stat = battleStat.playerStatList().get(0).getPokemonBattleStat(hatterene);
        Assertions.assertEquals(100, p1Stat.getAttackValue().intValue());
        Assertions.assertEquals(100, p1Stat.getHealthValue().intValue());
        p1Stat = battleStat.playerStatList().get(0).getPokemonBattleStat(glimmora);
        Assertions.assertEquals(0, p1Stat.getAttackValue().intValue());
        Assertions.assertEquals(0, p1Stat.getHealthValue().intValue());
    }

    @Test
    void analyzeStickyBarbDamage() {
        String hatterene = "Hatterene";
        String clefable = "Clefable";
        String tinglu = "Ting-Lu";

        BattleEvent battleEvent = new BattleEvent("damage", List.of("p1a: Hatterene", "77/100", "[from] item: Sticky " +
                "Barb"), null, null);

        Battle battle = new BattleBuilder()
                .addPokemon(1, hatterene)
                .addPokemon(2, clefable)
                .addPokemon(2, tinglu)
                .build();

        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, hatterene, hatterene)
                .addPokemon(2, clefable, clefable)
                .setActivePokemonName(1, hatterene)
                .setActivePokemonName(2, clefable)
                .setTurnStartPokemon(2, tinglu)
                .setTurnStartPokemon(1, hatterene)
                .setHealth(1, hatterene, BigDecimal.valueOf(68))
                .setBattle(battle)
                .build();

        BattleStat battleStat = new BattleStatBuilder()
                .addPokemonStat(1, hatterene)
                .addPokemonStat(2, clefable)
                .addPokemonStat(2, tinglu)
                .build();
        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        Assertions.assertEquals("Sticky Barb", battle.getBattleTeams().get(1).findPokemon(clefable).getItem());
        Assertions.assertNull(battle.getBattleTeams().get(1).findPokemon(tinglu).getItem());
        Assertions.assertNull(battle.getBattleTeams().get(0).findPokemon(hatterene).getItem());


        battleEvent = new BattleEvent("damage", List.of("p1a: Hatterene", "34/100", "[from] item: Sticky " +
                "Barb"), null, null);
        battleContext.getPlayerStatusList().get(0).getPokemonStatus(hatterene).setHealth(BigDecimal.valueOf(46));
        battleContext.getPlayerStatusList().get(1).setActivePokemonName(tinglu);

        damageEventAnalyzer.analyze(battleEvent, battleStat, battleContext);
        Assertions.assertEquals("Sticky Barb", battle.getBattleTeams().get(1).findPokemon(clefable).getItem());
        Assertions.assertNull(battle.getBattleTeams().get(1).findPokemon(tinglu).getItem());
        Assertions.assertNull(battle.getBattleTeams().get(0).findPokemon(hatterene).getItem());
    }

    private static Arguments buildSwitchDamageEvent() {
        BattleEvent switchEvent = new BattleEvent("switch", null, null, null);
        switchEvent.setBattleEventStat(new MoveEventStat(new EventTarget(2, "Gliscor", "Gliscor")
                , "Knock Off"));
        BattleEvent damageEvent = new BattleEvent("damage", List.of("p1a: OLD DOG DIFFERENCE", "73/100"),
                switchEvent, null);
        PlayerStat p1 = new PlayerStat(1, "");
        PlayerStatus p1Status = new PlayerStatus();
        p1Status.setPokemonNickName("OLD DOG DIFFERENCE", "Skarmory");
        p1Status.setTurnStartPokemonName(1, "Skarmory");

        PokemonBattleStat skarmory = new PokemonBattleStat("Skarmory");
        p1.addPokemonBattleStat(skarmory);

        PokemonStatus skarmoryStatus = new PokemonStatus("Skarmory");
        p1Status.setPokemonStatus("Skarmory", skarmoryStatus);

        PlayerStat p2 = new PlayerStat(2, "");
        PlayerStatus p2Status = new PlayerStatus();
        p2Status.setPokemonNickName("Gliscor", "Gliscor");

        PokemonBattleStat gliscor = new PokemonBattleStat("Gliscor");
        p2.addPokemonBattleStat(gliscor);

        BattleContext battleContext = new BattleContext(List.of(p1Status, p2Status), null);
        BattleStat battleStat = new BattleStat(null, List.of(p1, p2), new ArrayList<>());

        return Arguments.of(damageEvent, battleStat, battleContext, p1, BigDecimal.valueOf(27.0));
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
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, skarmory, "OLD DOG DIFFERENCE")
                .addPokemon(2, gliscor, gliscor)
                .setTurnStartPokemon(1, skarmory)
                .build();


        PokemonBattleStat exceptSkarmory = new PokemonBattleStat(skarmory);
        exceptSkarmory.setHealthValue(BigDecimal.valueOf(-27.0));
        exceptSkarmory.setAttackValue(BigDecimal.valueOf(0.0));
        PokemonStatus exceptSkarmoryStatus = new PokemonStatus(skarmory);
        exceptSkarmoryStatus.setHealth(BigDecimal.valueOf(73.0));
        PokemonBattleStat exceptGliscor = new PokemonBattleStat(gliscor);
        exceptGliscor.setAttackValue(BigDecimal.valueOf(27.0));
        exceptGliscor.setHealthValue(BigDecimal.valueOf(27.0));

        PokemonBattleStat skyStat = battleStat.playerStatList().get(damageTargetPlayerNumber - 1).getPokemonBattleStat(
                skarmory);
        PokemonBattleStat gliscorStat = battleStat.playerStatList().get(movePlayerNumber - 1).getPokemonBattleStat(
                gliscor);
        PokemonStatus skyStatus = battleContext.getPlayerStatusList().get(damageTargetPlayerNumber - 1).getPokemonStatus(skarmory);

        return Arguments.of(damageEvent, battleStat, battleContext, skyStat,
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
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(1, skarmory, "OLD DOG DIFFERENCE")
                .addPokemon(2, gliscor, gliscor)
                .setTurnStartPokemon(1, skarmory)
                .build();


        PokemonBattleStat exceptSkarmory = new PokemonBattleStat(skarmory);
        exceptSkarmory.setHealthValue(BigDecimal.valueOf(-100.0));
        exceptSkarmory.setAttackValue(BigDecimal.valueOf(0.0));
        PokemonStatus exceptSkarmoryStatus = new PokemonStatus(skarmory);
        exceptSkarmoryStatus.setHealth(BigDecimal.valueOf(0.0));
        PokemonBattleStat exceptGliscor = new PokemonBattleStat(gliscor);
        exceptGliscor.setAttackValue(BigDecimal.valueOf(100.0));
        exceptGliscor.setHealthValue(BigDecimal.valueOf(100.0));


        PokemonBattleStat skyStat = battleStat.playerStatList().get(damageTargetPlayerNumber - 1).getPokemonBattleStat(
                skarmory);
        PokemonBattleStat gliscorStat = battleStat.playerStatList().get(movePlayerNumber - 1).getPokemonBattleStat(
                gliscor);
        PokemonStatus skyStatus = battleContext.getPlayerStatusList().get(damageTargetPlayerNumber - 1).getPokemonStatus(skarmory);

        return Arguments.of(damageEvent, battleStat, battleContext, skyStat,
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
        Battle battle = new BattleBuilder()
                .addPokemon(1, skarmory)
                .build();
        BattleContext battleContext = new BattleContextBuilder()
                .addPokemon(helmetPlayerNumber, skarmory, "OLD DOG DIFFERENCE")
                .addPokemon(damageTargetPlayerNumber, gliscor, gliscor)
                .setTurnStartPokemon(damageTargetPlayerNumber, gliscor)
                .setBattle(battle)
                .build();

        PokemonBattleStat exceptSkarmory = new PokemonBattleStat("Skarmory");
        exceptSkarmory.setHealthValue(BigDecimal.valueOf(16.0));
        exceptSkarmory.setAttackValue(BigDecimal.valueOf(16.0));

        PokemonBattleStat exceptGliscor = new PokemonBattleStat("Gliscor");
        exceptGliscor.setAttackValue(BigDecimal.valueOf(0.0));
        exceptGliscor.setHealthValue(BigDecimal.valueOf(-16.0));
        PokemonStatus exceptGliscorStatus = new PokemonStatus("Gliscor");
        exceptGliscorStatus.setHealth(BigDecimal.valueOf(84.0));

        PokemonBattleStat gliscorStat =
                battleStat.playerStatList().get(damageTargetPlayerNumber - 1).getPokemonBattleStat(gliscor);
        PokemonBattleStat skarmoryStat =
                battleStat.playerStatList().get(helmetPlayerNumber - 1).getPokemonBattleStat(skarmory);
        PokemonStatus gliscorStatus =
                battleContext.getPlayerStatusList().get(damageTargetPlayerNumber - 1).getPokemonStatus(gliscor);

        return Arguments.of(damageEvent, battleStat, battleContext, gliscorStat, exceptGliscor, skarmoryStat,
                exceptSkarmory, gliscorStatus, exceptGliscorStatus);
    }
}