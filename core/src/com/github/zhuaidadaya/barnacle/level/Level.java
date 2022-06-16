package com.github.zhuaidadaya.barnacle.level;

import com.github.cao.awa.modmdo.times.*;
import com.github.zhuaidadaya.barnacle.entity.Role;
import com.github.zhuaidadaya.barnacle.plot.Plot;
import com.github.zhuaidadaya.barnacle.trend.Trend;
import org.json.JSONObject;

public class Level {
    private final Trend trend;
    private Plot plot;
    private final Role roles;
    private final String time;
    private final int steps;

    public Level(Trend trend, Role roles, int steps) {
        this.trend = trend;
        this.roles = roles;
        this.steps = steps;
        time = Times.getTime(TimeType.AS_SECOND);
    }

    public Level(JSONObject json) {
        JSONObject trendJson = json.getJSONObject("trend");
        this.trend = new Trend(trendJson.getString("name"),trendJson);
        this.roles = new Role(json.getJSONObject("roles"));
        this.time = json.getString("time");
        this.steps = json.getInt("step");
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("trend", trend.toJSONObject());
        json.put("roles", roles.toJSONObject());
        json.put("time", time);
        json.put("step", steps);
        return json;
    }

    public Role getRoles() {
        return roles;
    }

    public Trend getTrend() {
        return trend;
    }

    public String getTime() {
        return time;
    }

    public int getSteps() {
        return steps;
    }
}
