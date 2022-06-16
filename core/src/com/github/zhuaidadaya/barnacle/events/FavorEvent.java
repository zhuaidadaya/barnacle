package com.github.zhuaidadaya.barnacle.events;

import com.github.zhuaidadaya.barnacle.entity.PlayerEntity;
import org.json.JSONObject;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class FavorEvent implements Event{
    private final String target;
    private final String targetFor;
    private final int value;

    public FavorEvent(JSONObject json) {
        this.target = json.getString("target");
        this.targetFor = json.getString("for");
        this.value = json.getInt("value");
    }

    public String getTarget() {
        return target;
    }

    public String getTargetFor() {
        return targetFor;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void apply() {
        PlayerEntity playerTarget = roles.get(formatConstant(getTarget() + ".identifier"));
        PlayerEntity playerFor = roles.get(formatConstant(getTargetFor() + ".identifier"));
        playerTarget.changeFavor(playerFor.getIdentifier(), getValue());

        roles.put(formatConstant(getTarget() + ".identifier"), playerTarget);

        config.set("players", roles.toJSONObject());
    }
}
