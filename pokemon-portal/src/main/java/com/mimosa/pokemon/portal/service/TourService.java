/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.service;

import com.mimosa.deeppokemon.entity.tour.Tour;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourService {
    private final MongoTemplate mongoTemplate;

    public TourService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Cacheable("tours")
    public List<Tour> getAllTours() {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("startDate")));
        return mongoTemplate.find(query, Tour.class);
    }
}