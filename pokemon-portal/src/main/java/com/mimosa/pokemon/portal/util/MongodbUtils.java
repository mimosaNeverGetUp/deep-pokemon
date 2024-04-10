/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.util;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;

public class MongodbUtils {

    public static Query addPageFilter(Query query, int page, int row) {
        return query.skip((long) (page) * row).limit(row);
    }

    public static Aggregation addPageFilter(Aggregation aggregation, int page, int row) {
        aggregation.getPipeline().add(Aggregation.skip((long) page * row));
        aggregation.getPipeline().add(Aggregation.limit(row));
        return aggregation;
    }
}