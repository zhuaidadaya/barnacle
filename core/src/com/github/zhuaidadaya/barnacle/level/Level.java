package com.github.zhuaidadaya.barnacle.level;

import com.github.zhuaidadaya.MCH.time.TimeType;
import com.github.zhuaidadaya.MCH.time.Times;
import com.github.zhuaidadaya.barnacle.entity.Players;
import com.github.zhuaidadaya.barnacle.plot.Plot;
import com.github.zhuaidadaya.barnacle.trend.Trend;
import org.json.JSONObject;

public class Level {
    private Trend trend;
    private Plot plot;
    private Players players;
    private String time;
    private int steps;

    public Level(Trend trend, Players players,int steps) {
        this.trend = trend;
        this.players = players;
        this.steps = steps;
        time = Times.getTime(TimeType.AS_SECOND);
    }

    public Level(JSONObject json) {
        JSONObject trendJson = json.getJSONObject("trend");
        this.trend = new Trend(trendJson.getString("name"),trendJson);
        this.players = new Players(json.getJSONObject("players"));
        this.time = json.getString("time");
        this.steps = json.getInt("step");
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("trend", trend.toJSONObject());
        json.put("players", players.toJSONObject());
        json.put("time", time);
        json.put("step", steps);
        return json;
    }

    public Players getPlayers() {
        return players;
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
