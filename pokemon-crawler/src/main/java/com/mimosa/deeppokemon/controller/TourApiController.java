/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.entity.TourTeamGroupDetail;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.TourService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/tour")
public class TourApiController {
    protected static final String THE_WORLD_CUP_OF_POKEMON_2024 = "The World Cup of Pokémon 2024";
    protected static final String WCOP_2024 = "wcop_2024";
    protected static final String OLT_XI = "olt_xi";
    protected static final String OLT_XI_FULL_TOUR_NAME = "Smogon's Official Ladder Tournament XI";
    protected static final String SCL_IV = "SCL IV";
    protected static final String SMOGON_CHAMPIONS_LEAGUE_IV = "Smogon Champions League IV";

    protected static final String SMOGON_PREMIER_LEAGUE_XVI = "Smogon Premier League XVI";
    protected static final String SPL_XVI = "SPL XVI";

    protected static final String WCOP_2025_FULL_TOUR_NAME = "The World Cup of Pokémon 2025";
    protected static final String WCOP_2025 = "wcop_2025";

    protected static final String OUPL_VIII = "OUPL VIII";
    protected static final String GEN_9_OU = "gen9ou";

    private final TourService tourService;
    private final BattleService battleService;

    @Value("classpath:tourReplay/oupl.csv")
    Resource ouplReplayCsvResource;

    @Value("classpath:tourReplay/splxvi.csv")
    Resource splXVIReplayCsvResource;

    @Value("classpath:tourReplay/wcop2025.csv")
    Resource wcop2025ReplayCsvResource;

    public TourApiController(TourService tourService, BattleService battleService) {
        this.tourService = tourService;
        this.battleService = battleService;
    }

    @PostMapping("/wcop2024/battle")
    public boolean crawWcopTour(@RequestParam("format") String format) {
        tourService.crawWcop2024(format);
        return true;
    }

    @PostMapping("/wcop2025/battle")
    public boolean crawWcop2025Tour(@RequestParam("format") String format) {
        tourService.crawWcop2025(format);
        return true;
    }

    @PostMapping("/wcop2025/battleByCsv")
    public boolean crawWcop2025ByCsv(@RequestParam("format") String format) throws IOException {
        tourService.crawTourByCsv(WCOP_2025_FULL_TOUR_NAME, WCOP_2025, format,
                wcop2025ReplayCsvResource.getContentAsString(StandardCharsets.UTF_8));
        return true;
    }

    @PostMapping("/wcopOltXi/battle")
    public boolean crawOltXiTour() {
        tourService.crawOltXI();
        return true;
    }

    @PostMapping("/wcopOltXii/battle")
    public boolean crawOltXiiTour() {
        tourService.crawOltXII();
        return true;
    }

    @PostMapping("/sclIv/battle")
    public boolean crawSclIv() {
        tourService.crawSclIv();
        return true;
    }

    @PostMapping("/sclV/battle")
    public boolean crawSclV() {
        tourService.crawSclV();
        return true;
    }

    @PostMapping("/splXv/battle")
    public boolean crawSplIv(@RequestParam("format") String format) {
        tourService.crawSplXv(format);
        return true;
    }

    @PostMapping("/splXvi/battle")
    public boolean crawSplIvi(@RequestParam("format") String format) {
        tourService.crawSplXvi(format);
        return true;
    }

    @PostMapping("/splXvi/battleByCsv")
    public boolean crawSplIviByCsv(@RequestParam("format") String format) throws IOException {
        tourService.crawTourByCsv(SMOGON_PREMIER_LEAGUE_XVI, SPL_XVI, format,
                splXVIReplayCsvResource.getContentAsString(StandardCharsets.UTF_8));
        return true;
    }

    @PostMapping("/ouplViii/battle")
    public boolean crawOuplViii(@RequestParam("format") String format) {
        tourService.crawOuplViii(format);
        return true;
    }

    @PostMapping("/ouplViii/battleByCsv")
    public boolean crawOuplViiiByCsv(@RequestParam("format") String format) throws IOException {
        tourService.crawTourByCsv(OUPL_VIII, OUPL_VIII, format, ouplReplayCsvResource.getContentAsString(StandardCharsets.UTF_8));
        return true;
    }

    @PostMapping("/wcop2024/record/update")
    public boolean updateWcop2024PlayerRecord(@RequestParam("format") String format) {
        tourService.updatePlayerRecord(THE_WORLD_CUP_OF_POKEMON_2024, format);
        return true;
    }

    @PostMapping("/wcop2024/info/update")
    public boolean updateWcop2024Info(@RequestParam("format") String format) {
        tourService.updateTour(THE_WORLD_CUP_OF_POKEMON_2024, WCOP_2024, format);
        return true;
    }

    @PostMapping("/oltxi/info/update")
    public boolean updateOltXiTourInfo() {
        tourService.updateTour(OLT_XI_FULL_TOUR_NAME, OLT_XI, GEN_9_OU);
        return true;
    }

    @PostMapping("/scliv/info/update")
    public boolean updateSclIvTourInfo() {
        tourService.updateTour(SMOGON_CHAMPIONS_LEAGUE_IV, SCL_IV, GEN_9_OU);
        return true;
    }

    @PostMapping("/scliv/team/update")
    public boolean updateSclTeam(@RequestParam("format") String format) {
        battleService.updateTeam(new TourTeamGroupDetail("team_group_tour_SCL IV",
                "team_set_tour_SCL IV", SMOGON_CHAMPIONS_LEAGUE_IV, format));
        return true;
    }

    @PostMapping("/oltxi/team/update")
    public boolean updateOltxieam() {
        battleService.updateTeam(new TourTeamGroupDetail("team_group_tour_olt_xi",
                "team_set_tour_olt_xi", OLT_XI_FULL_TOUR_NAME, GEN_9_OU));
        return true;
    }

    @PostMapping("/wcop2024/team/update")
    public boolean updateWcop2024Team(@RequestParam("format") String format) {
        if (format.equals(GEN_9_OU)) {
            battleService.updateTeam(new TourTeamGroupDetail("team_group_tour_wcop_2024",
                    "team_set_tour_wcop_2024", THE_WORLD_CUP_OF_POKEMON_2024, format));
        } else {
            battleService.updateTeam(new TourTeamGroupDetail("team_group_tour_wcop_2024_" + format,
                    "team_set_tour_wcop_" + format, OUPL_VIII, format));
        }

        return true;
    }

    @PostMapping("/ouplViii/update")
    public boolean updateOuplGroupAndRecord(@RequestParam("format") String format) {
        tourService.updatePlayerRecord(OUPL_VIII, format);
        if (format.equals(GEN_9_OU)) {
            battleService.updateTeam(new TourTeamGroupDetail("team_group_tour_OUPL VIII",
                    "team_set_tour_OUPL VIII", OUPL_VIII, format));
        } else {
            battleService.updateTeam(new TourTeamGroupDetail("team_group_tour_OUPL VIII_" + format,
                    "team_set_tour_OUPL VIII_" + format, OUPL_VIII, format));
        }

        return true;
    }
}