package com.github.zhuaidadaya.barnacle.entity;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class Players {
    private LinkedHashMap<String, PlayerEntity> players = new LinkedHashMap<>();

    public void put(String name, PlayerEntity player) {
        players.put(name, player);
    }

    public PlayerEntity get(String name) {
        return players.get(name);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        for(PlayerEntity player : players.values())
            json.put(player.getIdentifier(), player.toJSONObject());

        return json;

    }
}
