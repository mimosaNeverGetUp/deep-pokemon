/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.controller;

import com.mimosa.deeppokemon.service.TourService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tour")
public class TourApiController {
    private final TourService tourService;

    public TourApiController(TourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping("/wcop2024/battle")
    public boolean crawlTour() {
        tourService.crawWcop2024();
        return true;
    }

    @PostMapping("/wcop2024/record/update")
    public boolean updateWcop2024PlayerRecord(@RequestParam("format") String format) {
        tourService.updatePlayerRecord("The World Cup of Pokémon 2024", format);
        return true;
    }

    @PostMapping("/wcop2024/player/update")
    public boolean updateWcop2024TierPlayer(@RequestParam("format") String format) {
        tourService.updateTour("The World Cup of Pokémon 2024", format);
        return true;
    }
}