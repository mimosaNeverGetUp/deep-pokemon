/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.pokemon.portal.entity.PageResponse;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MongodbUtils {

    public static final String DATA = "data";
    public static final String TOTAL = "total";

    public static Query buildPageFacetAggregationOperation(Query query, int page, int row) {
        return query.skip((long) (page) * row).limit(row);
    }

    public static AggregationOperation[] buildPageFacet(int page, int row) {
        return new AggregationOperation[]{Aggregation.skip((long) (page) * row),
                Aggregation.limit(row)};
    }

    public static AggregationOperation buildCountFacet(String resultKey) {
        return Aggregation.count().as(resultKey);
    }

    public static Aggregation addPageFacetOperation(Aggregation aggregation, int page, int row) {
        aggregation.getPipeline().add(Aggregation.facet()
                .and(MongodbUtils.buildPageFacet(page, row)).as(DATA)
                .and(MongodbUtils.buildCountFacet(TOTAL)).as(TOTAL));

        aggregation.getPipeline().add(Aggregation.unwind(TOTAL));
        aggregation.getPipeline().add(Aggregation.project(Fields.from(
                Fields.field(DATA),
                Fields.field(TOTAL, "total.total"))));
        return aggregation;
    }

    public static <T> PageResponse<T> parsePageAggregationResult(AggregationResults<Document> results, int page, int row, Class<T> targetClass) {
        Optional<Document> result = results.getMappedResults().stream().findFirst();
        if (result.isEmpty()) {
            return new PageResponse<>(0, page, row, new ArrayList<>());
        } else {
            List<T> data = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

            List<Document> documents = result.get().getList(DATA, Document.class);
            documents.forEach(document -> {
                try {
                    data.add(objectMapper.readValue(document.toJson(), targetClass));
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("convert aggregation result fail", e);
                }
            });
            int total = result.get().getInteger(TOTAL);
            return new PageResponse<>(total, page, row, data);
        }
    }
}