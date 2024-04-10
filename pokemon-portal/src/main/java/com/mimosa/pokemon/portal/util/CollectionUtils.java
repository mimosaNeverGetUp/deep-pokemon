/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.util;

import java.util.Collections;
import java.util.List;

public class CollectionUtils {

    public static <T> List<T> singletonListIfPresent(T object) {
        if (object == null) {
            return null;
        }

        return Collections.singletonList(object);
    }

    public static boolean hasNotNullObject(List<String> list) {
        if (list == null) {
            return false;
        }

        for (String str : list) {
            if (str != null && !str.isEmpty() && !str.isBlank()) {
                return true;
            }
        }

        return false;
    }
}