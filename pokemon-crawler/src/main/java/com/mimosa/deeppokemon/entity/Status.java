package com.mimosa.deeppokemon.entity;

/**
 * @program: deep-pokemon
 * @description: 宝可梦状态
 * @author: mimosa
 * @create: 2021//08//24
 */
public enum Status {
    HEALTH("health", (short) 0),
    SLEEP("sleep", (short) 1),
    BURN("burn", (short) 2),
    FREEZE("freeze", (short) 3),
    PARA("para", (short) 4),
    TOX("tox", (short) 5)
    ;

    private String name;
    private short code;

    Status(String name, Short code) {
        this.name = name;
        this.code = code;
    }

    public static Short getcode(String name) {
        for (Status status : Status.values()) {
            if (status.name == name) {
                return status.code;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public short getCode() {
        return code;
    }
}
