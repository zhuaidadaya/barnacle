package com.github.zhuaidadaya.barnacle.level;

import com.github.zhuaidadaya.MCH.time.TimeType;
import com.github.zhuaidadaya.MCH.time.Times;
import com.github.zhuaidadaya.barnacle.entity.Players;
import com.github.zhuaidadaya.barnacle.trend.Trend;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class LevelStorage {
    LinkedHashMap<String, Level> levels = new LinkedHashMap<>();

    public LevelStorage() {

    }

    public LevelStorage(JSONObject json) {
        for(Object o : json.keySet()) {
            Level level = new Level(json.getJSONObject(o.toString()));

            levels.put(o.toString(), level);
        }
    }

    public void createLevel() {
        createLevel("");
    }

    public void createLevel(String name) {
        Trend t = new Trend(trend.getName(), trend.toJSONObject());
        Players p = new Players(players.toJSONObject());
        Level level = new Level(t, p, trend.getLatestPlotStep());
        if(name != null) {
            levels.put(name.equals("") ? level.getTime() : name, level);
        }
    }

    public Level getLevel(String name) {
        return levels.get(name);
    }

    public LinkedHashMap<String, Level> getLevels() {
        return levels;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        for(String levelName : levels.keySet()) {
            json.put(levelName, levels.get(levelName).toJSONObject());
        }
        return json;
    }
}
