/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.entity.TourTeamGroupDetail;
import com.mimosa.deeppokemon.service.BattleService;
import com.mimosa.deeppokemon.service.TourService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tour")
public class TourApiController {
    protected static final String THE_WORLD_CUP_OF_POKEMON_2024 = "The World Cup of Pokémon 2024";
    protected static final String WCOP_2024 = "wcop_2024";
    protected static final String OLT_XI = "olt_xi";
    protected static final String OLT_XI_FULL_TOUR_NAME = "Smogon's Official Ladder Tournament XI";
    private final TourService tourService;
    private final BattleService battleService;

    public TourApiController(TourService tourService, BattleService battleService) {
        this.tourService = tourService;
        this.battleService = battleService;
    }

    @PostMapping("/wcop2024/battle")
    public boolean crawWcopTour(@RequestParam("format") String format) {
        tourService.crawWcop2024(format);
        return true;
    }

    @PostMapping("/wcopOltXi/battle")
    public boolean crawOltXiTour() {
        tourService.crawOltXI();
        return true;
    }

    @PostMapping("/sclIv/battle")
    public boolean crawSclIv() {
        tourService.crawSclIv();
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
        tourService.updateTour(OLT_XI_FULL_TOUR_NAME, OLT_XI, "gen9ou");
        return true;
    }

    @PostMapping("/wcop2024/team/update")
    public boolean updateWcop2024Team(@RequestParam("format") String format) {
        battleService.updateTeam(new TourTeamGroupDetail("team_group_tour_wcop_2024",
                "team_set_tour_wcop_2024", THE_WORLD_CUP_OF_POKEMON_2024, format));
        return true;
    }
}