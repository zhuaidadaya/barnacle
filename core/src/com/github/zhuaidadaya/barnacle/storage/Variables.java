package com.github.zhuaidadaya.barnacle.storage;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.zhuaidadaya.MCH.Resources;
import com.github.zhuaidadaya.MCH.language.Language;
import com.github.zhuaidadaya.MCH.log.Logger;
import com.github.zhuaidadaya.MCH.utils.config.ConfigUtil;
import com.github.zhuaidadaya.barnacle.BarnacleGraphics;
import com.github.zhuaidadaya.barnacle.entity.PlayerEntity;
import com.github.zhuaidadaya.barnacle.entity.Players;
import com.github.zhuaidadaya.barnacle.level.Level;
import com.github.zhuaidadaya.barnacle.level.LevelStorage;
import com.github.zhuaidadaya.barnacle.option.SimpleOption;
import com.github.zhuaidadaya.barnacle.plot.Plot;
import com.github.zhuaidadaya.barnacle.trend.Trend;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class Variables {
    public static Logger logger = new Logger("Barnacle");
    public static Language language = Language.CHINESE;
    public static LinkedHashSet<String> selectedOptions = new LinkedHashSet<>();
    public static Players players = new Players();
    public static String drawPlotMessage = "";
    public static String drawPlotTitle = "";
    public static String drawPlotSubtitle = "";
    public static String drawPlotTip = "";
    public static String drawPlotRightNpc = "";
    public static String drawPlotLeftNpc = "";
    public static boolean drawDialogBox = true;
    public static String drawBackground = "background_default";
    public static LevelStorage levels = new LevelStorage();
    public static boolean pause = false;
    public static boolean rendingSaveLevel = false;
    public static int selectedSlot = 0;
    public static boolean launched = false;

    public static int frameHeight = 920;
    public static int frameWidth = 1580;

    public static ConfigUtil config = new ConfigUtil(System.getProperty("user.dir"), "BarnacleConfig.mhf", "1.1", "Barnacle").setEncryption(false).setNote("""
            config of Barnacle, this is Barnacle game database
                
            encryption and database provided by MCH
            """).setSplitRange(500).setEncryption(false);
    //            .setSplitRange(Integer.MAX_VALUE);
    public static Trend trend;
    public static JSONObject resourceJson;
    public static JSONObject libraries;
    public static JSONObject trends;
    public static JSONObject npcs;
    public static JSONObject texturesJson;
    public static SimpleOption randomDefaultOption;
    public static SimpleOption willOption;
    public static int willOptionIndex = - 1;
    public static LinkedHashSet<SimpleOption> optionsButton = new LinkedHashSet<>();
    public static LinkedHashMap<TextButton, SimpleOption> optionsButtonMap = new LinkedHashMap<>();
    public static LinkedHashMap<String, Texture> textureAtlas = new LinkedHashMap<>();

    public static long waitingProgressBarMax = 0;
    public static long waitingProgressBarValue = 0;

    public static void jumpTrend(Level level) {
        String jumpTrendTarget = level.getTrend().getName();
        players = new Players(level.getPlayers().toJSONObject());
        config.set("players", players.toJSONObject());
        trend.jumpTrend(jumpTrendTarget, jumpTrendTarget, level.getTrend().toJSONObject());
    }

    public static void trendWillOption() {
        trend.initTrend(willOption.trendName(), willOption.trend());
        optionsButtonMap = new LinkedHashMap<>();
    }

    public static Texture getTexture(String name) {
        return textureAtlas.get(name);
    }

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

    public static String formatLevels(String text) {
        String[] formats = new String[10];

        try {
            for(int i = 0; i < 10; i++) {
                Level level = levels.getLevel(String.valueOf(i));
                if(level == null)
                    formats[i] = getFormat("rending.save.slot.null").toString();
                else
                    formats[i] = "  " + level.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        text = String.format(text, (Object[]) formats);

        text = text.replace("[" + selectedSlot +"]","> [" + selectedSlot + "]");

        return text;
    }

    public static String formatConstant(String source) {
        return source.replace("$n1.name", getNpcName(1)) // npc1 name
                .replace("$n2.name", getNpcName(2)) // npc2 name
                .replace("$n1.alias", getNpcAlias(1)) // npc1 alias
                .replace("$n2.alias", getNpcAlias(2)) // npc2 alias
                .replace("$n1.identifier", getNpcIdentifier(1)) // npc1 id
                .replace("$n2.identifier", getNpcIdentifier(2)); // npc2 id
    }

    public static String getNpcIdentifier(int index) {
        return npcs.getJSONObject("n" + index).getString("id");
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

    public static LinkedHashSet<Plot> getPlotDetails(JSONObject plot) {
        LinkedHashSet<Plot> plots = new LinkedHashSet<>();
        JSONArray plotsSource = plot.getJSONArray("details");
        for(Object o : plotsSource) {
            try {
                JSONObject json = new JSONObject(o.toString());
                plots.add(new Plot(json));
            } catch (Exception ex) {
                plots.add(new Plot(o.toString()));
            }
        }
        return plots;
    }

    public static void initVariables() {
        try {
            InputStream input = Resources.getResource("/format/format.json", Resources.class);
            StringBuilder s = new StringBuilder();
            String cache;

            BufferedReader br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

            while((cache = br.readLine()) != null) {
                s.append(cache).append("\n");
            }

            resourceJson = new JSONObject(s.toString());

            libraries = resourceJson.getJSONObject("library").getJSONObject(language.getName());
            trends = resourceJson.getJSONObject("trend");
            npcs = resourceJson.getJSONObject("npc");

            input = Resources.getResource("/textures/atlas.json", Resources.class);
            br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            s = new StringBuilder();

            while((cache = br.readLine()) != null) {
                s.append(cache).append("\n");
            }

            texturesJson = new JSONObject(s.toString());

            players.put(formatConstant("$n1.identifier"), new PlayerEntity(npcs.getJSONObject("n1")));
            players.put(formatConstant("$n2.identifier"), new PlayerEntity(npcs.getJSONObject("n2")));
//            try {
//                players = new Players(new JSONObject(config.getConfigValue("players")));
//            } catch(Exception ex) {
//
//            }

            trend = new Trend("stage.starter", trends.getJSONObject("stage.starter"));

            //            try {
            //                String plot = config.getConfigValue("latest_plot");
            //
            //                trend = new Trend(plot, trends.getJSONObject(plot));
            //            } catch (Exception e) {
            //
            //            }
            //
            //            try {
            //                int plotStep = Integer.parseInt(config.getConfigValue("latest_plot_step"));
            //
            //                trend.setLatestPlotStep(plotStep);
            //            } catch (Exception e) {
            //
            //            }
            try {
                levels = new LevelStorage(new JSONObject(config.getConfigValue("levels")));
            } catch(Exception ex) {

            }

            config.set("players", players.toJSONObject());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
