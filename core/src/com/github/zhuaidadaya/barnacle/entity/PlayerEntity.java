package com.github.zhuaidadaya.barnacle.entity;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public class PlayerEntity extends Entity {
    protected String alias;
    private final LinkedHashMap<String, Integer> favorMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> expectMap = new LinkedHashMap<>();

    public PlayerEntity(JSONObject json) {
        name = json.getString("name");
        alias = json.getString("alias");
        identifier = json.getString("id");

        try {
            JSONObject favor = json.getJSONObject("favor");

            for(String s : favor.keySet()) {
                favorMap.put(s, favor.getInt(s));
            }
        } catch (Exception ex) {

        }

        try {
            JSONObject expect = json.getJSONObject("expect");

            for(String s : expect.keySet()) {
                expectMap.put(s, expect.getInt(s));
            }
        } catch (Exception ex) {

        }
    }

    public PlayerEntity() {

    }

    public void changeFavor(String identifier, int favor) {
        try {
            favorMap.put(identifier, favor + favorMap.get(identifier));
        } catch (Exception e) {
            favorMap.put(identifier, favor);
        }
    }

    public void changeExpect(String identifier, int expect) {
        try {
            expectMap.put(identifier, expect + expectMap.get(identifier));
        } catch (Exception e) {
            expectMap.put(identifier, expect);
        }
    }

    public int getFavor(String player) {
        return favorMap.get(player) == null ? 0 : favorMap.get(player);
    }

    public int getExpect(String player) {
        return expectMap.get(player) == null ? 0 : expectMap.get(player);
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        json.put("name", name);
        json.put("alias", alias);
        json.put("id", identifier);

        JSONObject favor = new JSONObject();

        for(String name : favorMap.keySet()) {
            favor.put(name, favorMap.get(name));
        }

        JSONObject expect = new JSONObject();

        for(String name : expectMap.keySet()) {
            expect.put(name, expectMap.get(name));
        }

        json.put("favor", favor);
        json.put("expect", expect);

        return json;
    }

    public String getIdentifier() {
        return identifier;
    }
}
