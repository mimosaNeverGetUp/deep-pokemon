/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

package com.mimosa.deeppokemon.info;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MoveInfoProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveInfoProvider.class);

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    protected static final String PREFIX_Z_MOVE = "Z-";
    protected static final Map<String, String> TYPE_Z_ITEM_MAP = new HashMap<>();

    static {
        TYPE_Z_ITEM_MAP.put("Bug", "Buginium Z");
        TYPE_Z_ITEM_MAP.put("Dark", "Darkinium Z");
        TYPE_Z_ITEM_MAP.put("Dragon", "Dragonium Z");
        TYPE_Z_ITEM_MAP.put("Electric", "Electrium Z");
        TYPE_Z_ITEM_MAP.put("Fairy", "Fairium Z");
        TYPE_Z_ITEM_MAP.put("Fighting", "Fightinium Z");
        TYPE_Z_ITEM_MAP.put("Fire", "Firium Z");
        TYPE_Z_ITEM_MAP.put("Flying", "Flyinium Z");
        TYPE_Z_ITEM_MAP.put("Ghost", "Ghostium Z");
        TYPE_Z_ITEM_MAP.put("Grass", "Grassium Z");
        TYPE_Z_ITEM_MAP.put("Ground", "Groundium Z");
        TYPE_Z_ITEM_MAP.put("Ice", "Icium Z");
        TYPE_Z_ITEM_MAP.put("Normal", "Normalium Z");
        TYPE_Z_ITEM_MAP.put("Poison", "Poisonium Z");
        TYPE_Z_ITEM_MAP.put("Psychic", "Psychium Z");
        TYPE_Z_ITEM_MAP.put("Rock", "Rockium Z");
        TYPE_Z_ITEM_MAP.put("Steel", "Steelium Z");
        TYPE_Z_ITEM_MAP.put("Water", "Waterium Z");
    }

    @Value("classpath:pokemon/moveInfo.json")
    private Resource moveInfoData;

    private final ItemInfoProvider itemInfoProvider;

    private final Map<String, MoveInfo> moveInfos = new HashMap<>();
    private final Map<String, MoveInfo> shortNameMap = new HashMap<>();

    public MoveInfoProvider(ItemInfoProvider itemInfoProvider) {
        this.itemInfoProvider = itemInfoProvider;
    }

    @RegisterReflectionForBinding(value = MoveInfo.class)
    public MoveInfo getMoveInfo(String move) {
        if (moveInfos.isEmpty()) {
            load();
        }
        MoveInfo moveInfo = moveInfos.get(move);
        if (moveInfo == null) {
            moveInfo = shortNameMap.get(move);
        }
        return moveInfo;
    }

    private void load() {
        if (moveInfos.isEmpty()) {
            try {
                Map<String, MoveInfo> tmp = OBJECT_MAPPER.readValue(moveInfoData.getInputStream(),
                        new TypeReference<>() {
                        });
                for (var entry : tmp.entrySet()) {
                    moveInfos.put(entry.getValue().name(), entry.getValue());
                    shortNameMap.put(entry.getKey(), entry.getValue());
                }
            } catch (IOException e) {
                LOGGER.error("Failed to load moveInfos", e);
            }
        }
    }

    public boolean isZMove(String move) {
        if (move == null || move.isEmpty()) return false;
        if (move.startsWith(PREFIX_Z_MOVE)) return true;

        MoveInfo moveInfo = getMoveInfo(move);
        return moveInfo != null && moveInfo.isZ() != null;
    }

    public String getZMoveItem(String move) {
        if (!move.startsWith(PREFIX_Z_MOVE)) {
            MoveInfo moveInfo = getMoveInfo(move);
            if (moveInfo == null || moveInfo.isZ() == null) {
                return null;
            }
            ItemInfo itemInfo = itemInfoProvider.getItemInfo(moveInfo.isZ());
            return itemInfo == null ? null : itemInfo.name();
        } else {
            String baseMove = move.substring(PREFIX_Z_MOVE.length());
            MoveInfo moveInfo = getMoveInfo(baseMove);
            if (moveInfo == null) {
                return null;
            }
            return TYPE_Z_ITEM_MAP.get(moveInfo.type());
        }
    }

    public String getName(String shortName) {
        MoveInfo moveInfo = getMoveInfo(shortName);
        return moveInfo == null ? null : moveInfo.name();
    }
}