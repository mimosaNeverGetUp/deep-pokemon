/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Replay {
    protected String id;
    @JsonProperty("uploadtime")
    protected long uploadTime;
    protected String format;
    protected int rating;
    protected String[] players;
    protected boolean isPrivate;

    public Replay() {

    }

    @JsonCreator
    public Replay(@JsonProperty("id") String id, @JsonProperty("uploadtime") long uploadTime,
                  @JsonProperty("format") String format, @JsonProperty("rating") int rating,
                  @JsonProperty("players") String[] players, @JsonProperty("isPrivate") boolean isPrivate) {
        this.id = id;
        this.uploadTime = uploadTime;
        this.format = format;
        this.rating = rating;
        this.players = players;
        this.isPrivate = isPrivate;
    }

    public Replay(String id) {
        this(id, 0, null, 0, null, false);
    }

    public String getId() {
        return id;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String[] getPlayers() {
        return players;
    }

    public void setPlayers(String[] players) {
        this.players = players;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}