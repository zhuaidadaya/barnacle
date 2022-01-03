package com.github.zhuaidadaya.barnacle.entity;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public class PlayerEntity extends Entity {
    public LinkedHashMap<PlayerEntity, Integer> favorMap = new LinkedHashMap<>();
    public LinkedHashMap<PlayerEntity, Integer> exceptMap = new LinkedHashMap<>();
    protected String alias;

    public int getFavor(PlayerEntity player) {
        return favorMap.get(player) == null ? 0 : favorMap.get(player);
    }

    public int getExcept(PlayerEntity player) {
        return exceptMap.get(player) == null ? 0 : exceptMap.get(player);
    }

    public PlayerEntity(JSONObject json) {
        name = json.getString("name");
        alias = json.getString("name");
    }

    public PlayerEntity() {

    }
}
