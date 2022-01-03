package com.github.zhuaidadaya.barnacle.storage;


import com.github.zhuaidadaya.MCH.Resources;
import com.github.zhuaidadaya.MCH.language.Language;
import com.github.zhuaidadaya.MCH.log.Logger;
import com.github.zhuaidadaya.barnacle.entity.PlayerEntity;
import com.github.zhuaidadaya.barnacle.option.SimpleOption;
import com.github.zhuaidadaya.barnacle.trend.Trend;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class Variables {
    public static Logger logger = new Logger("Barnacle");
    public static Language language = Language.CHINESE;
    public static LinkedHashSet<String> selectedOptions = new LinkedHashSet<>();
    public static PlayerEntity player = new PlayerEntity();
    public static LinkedHashMap<String, PlayerEntity> players = new LinkedHashMap<>();

    /**
     * main player
     */
    public static PlayerEntity MAIN_1 = new PlayerEntity();
    public static Trend trend;
    public static JSONObject libraries;
    public static JSONObject trends;
    public static JSONObject npcs;
    public static LinkedHashSet<SimpleOption> optionsButton = new LinkedHashSet<>();

    public static JSONObject getTrend(String name) {
        return trends.getJSONObject(name);
    }

    public static Object getFormat(String name) {
        try {
            return new JSONObject(libraries.getString(name));
        } catch (Exception ex) {
            return libraries.get(name);
        }
    }

    public static String formatConstant(String source) {
        return source.replace("$n1.name", getNpcName(1)) // npc1 name
                .replace("$n2.name", getNpcName(2)) // npc2 name
                .replace("$n1.alias", getNpcAlias(1)) // npc1 alias
                .replace("$n2.alias", getNpcAlias(2)); // npc2 alias
    }

    public static String getNpcName(int index) {
        return npcs.getJSONObject("n" + index).getString("name");
    }

    public static String getNpcAlias(int index) {
        return npcs.getJSONObject("n" + index).getString("alias");
    }

    public static JSONObject getPlot(String name) {
        if(! name.startsWith("plot."))
            return libraries.getJSONObject("plot." + name);
        else
            return libraries.getJSONObject(name);
    }

    public static JSONArray getPlotDetails(JSONObject plot) {
        return plot.getJSONArray("details");
    }

    public static void initVariables() {
        try {
            InputStream input = Resources.getResource("/format/format.json", Resources.class);
            JSONObject json;
            StringBuilder s = new StringBuilder();
            String cache;

            BufferedReader br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

            while((cache = br.readLine()) != null)
                s.append(cache).append("\n");

            json = new JSONObject(s.toString());

            libraries = json.getJSONObject("library").getJSONObject(language.getName());
            trends = json.getJSONObject("trend");
            npcs = json.getJSONObject("npc");

            trend = new Trend("stage.starter", trends.getJSONObject("stage.starter"));

            players.put(formatConstant("$n1.name"), new PlayerEntity(npcs.getJSONObject("n1")));
            players.put(formatConstant("$n2.name"), new PlayerEntity(npcs.getJSONObject("n2")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
