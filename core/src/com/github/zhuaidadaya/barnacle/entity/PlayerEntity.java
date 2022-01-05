package com.github.zhuaidadaya.barnacle.entity;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public class PlayerEntity extends Entity {
    private final LinkedHashMap<String, Integer> favorMap = new LinkedHashMap<>();
    protected String alias;
    private int expect = 100;

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
            expect = json.getInt("expect");
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

    public void changeExpect(int expect) {
        this.expect = this.expect + expect;
    }

    public void setExpect(int expect) {
        this.expect = expect;
    }

    public int getFavor(String player) {
        return favorMap.get(player) == null ? 0 : favorMap.get(player);
    }

    public int getExpect() {
        return expect;
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

        json.put("favor", favor);

        json.put("expect", expect);

        return json;
    }

    public String getIdentifier() {
        return identifier;
    }
}
