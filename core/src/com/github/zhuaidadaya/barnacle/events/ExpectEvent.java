package com.github.zhuaidadaya.barnacle.events;

import com.github.zhuaidadaya.barnacle.entity.PlayerEntity;
import org.json.JSONObject;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class ExpectEvent implements Event {
    private String target;
    private int value;
    private int change = - 1;

    public ExpectEvent(JSONObject json) {
        this.target = json.getString("target");

        try {
            this.change = json.getInt("change");
        } catch (Exception ex) {
            this.value = json.getInt("value");
        }
    }

    public String getTarget() {
        return target;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void apply() {
        PlayerEntity playerTarget = roles.get(formatConstant(getTarget() + ".identifier"));

        if(change == - 1) {
            playerTarget.changeExpect(getValue());
        }else {
            playerTarget.setExpect(change);
        }

        roles.put(formatConstant(getTarget() + ".identifier"), playerTarget);

        config.set("players", roles.toJSONObject());
    }
}
