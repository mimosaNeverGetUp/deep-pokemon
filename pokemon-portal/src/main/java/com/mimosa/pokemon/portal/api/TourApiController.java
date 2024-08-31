/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.api;

import com.mimosa.deeppokemon.entity.tour.Tour;
import com.mimosa.pokemon.portal.service.TourService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tour")
public class TourApiController {
    private final TourService tourService;

    public TourApiController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping("/tours")
    public List<Tour> tours() {
        return tourService.getAllTours();
    }
}