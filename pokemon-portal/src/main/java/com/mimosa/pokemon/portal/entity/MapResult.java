package com.mimosa.pokemon.portal.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.HashMap;

/**
 * @program: deep-pokemon
 * @description: mongodb mapReduce result pojo
 * @author: mimosa
 * @create: 2020//10//17
 */

@Document
public class MapResult  {


    @MongoId
    private String _id;
    private Statistic value;




    @Override
    public String toString() {
        return "MapResult{" +
                "_id='" + _id + '\'' +
                ", value=" + value +
                '}';
    }

    public MapResult() {
    }



    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
    public Statistic getValue() {
        return value;
    }

    public void setValue(Statistic value) {
        this.value = value;
    }

    public MapResult(Statistic value) {
        this.value = value;
    }

}
