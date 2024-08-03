/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.util;

import org.springframework.data.mongodb.core.query.Query;

public class MongodbUtils {

    private MongodbUtils() {}

    public static Query withPageOperation(Query query, int page, int row) {
        return query.skip((long) (page) * row).limit(row);
    }
}