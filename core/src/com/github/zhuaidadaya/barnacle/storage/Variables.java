package com.github.zhuaidadaya.barnacle.storage;


import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.resourceLoader.*;
import com.github.zhuaidadaya.barnacle.entity.PlayerEntity;
import com.github.zhuaidadaya.barnacle.entity.Role;
import com.github.zhuaidadaya.barnacle.level.Level;
import com.github.zhuaidadaya.barnacle.level.LevelStorage;
import com.github.zhuaidadaya.barnacle.option.SimpleOption;
import com.github.zhuaidadaya.barnacle.plot.Plot;
import com.github.zhuaidadaya.barnacle.texture.*;
import com.github.zhuaidadaya.barnacle.trend.Trend;
import com.github.zhuaidadaya.rikaishinikui.handler.config.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class Variables {
    public static String BARNACLE = "Barnacle";
    public static Logger logger = LogManager.getLogger(BARNACLE);
    public static Language language = Language.ZH_CN;
    public static LinkedHashSet<String> selectedOptions = new LinkedHashSet<>();
    public static Role roles = new Role();
    public static Plot drawPlotMessage = new Plot("");
    public static String drawPlotTitle = "";
    public static String drawPlotSubtitle = "";
    public static String drawPlotTip = "";
    public static String drawPlotRightNpc = "";
    public static String drawPlotLeftNpc = "";
    public static boolean drawDialogBox = true;
    public static String drawBackground = "background_default";
    public static LevelStorage levels = new LevelStorage();
    public static boolean pause = false;
    public static boolean shuttingDown = false;
    public static boolean rendingSaveLevel = false;
    public static boolean launched = false;
    public static boolean viewingLog = false;
    public static boolean running = false;
    public static boolean autoTrend = false;
    public static int selectedSlot = 0;
    public static HashSet<Integer> savingSlot = new HashSet<>();

    public static int frameHeight = 920;
    public static int frameWidth = 1580;

    public static DiskObjectConfigUtil config;
    public static Trend trend;
    public static JSONObject resourceJson;
    public static JSONObject stages;
    public static JSONObject lang;
    public static JSONObject trends;
    public static JSONObject npcs;
    public static JSONObject texturesJson;
    public static SimpleOption randomDefaultOption;
    public static SimpleOption willOption;
    public static int willOptionIndex = - 1;
    public static LinkedHashSet<SimpleOption> optionsButton = new LinkedHashSet<>();
    public static LinkedHashMap<TextButton, SimpleOption> optionsButtonMap = new LinkedHashMap<>();
    public static Textures textures = new Textures();

    public static long waitingProgressBarMax = 0;
    public static long waitingProgressBarValue = 0;

    public static LinkedHashMap<Integer, Plot> recodingPlots = new LinkedHashMap<>();

    public static void jumpTrend(Level level) {
        String jumpTrendTarget = level.getTrend().getName();
        roles = new Role(level.getRoles().toJSONObject());
        config.set("roles", roles.toJSONObject());
        trend.jumpTrend(jumpTrendTarget, jumpTrendTarget, level.getTrend().toJSONObject(), level.getTime(), level.getSteps());
    }

    public static void trendWillOption() {
        trend.initTrend(willOption.trendName(), willOption.trend());
        optionsButtonMap = new LinkedHashMap<>();
    }

    public static Texture getTexture(String name) {
        return textures.get(name);
    }

    public static JSONObject getTrend(String name) {
        return trends.getJSONObject(name);
    }

    public static String formatLevels(String text) {
        String[] formats = new String[10];

        try {
            for (int i = 0; i < 10; i++) {
                Level level = levels.getLevel(String.valueOf(i));
                if (level == null) {
                    formats[i] = getFormat("rending.save.slot.null");
                } else {
                    if (savingSlot.contains(i)) {
                        formats[i] = getFormat("rending.save.saving");
                    } else {
                        formats[i] = "  " + level.getTime();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        text = String.format(text, (Object[]) formats);

        text = text.replace("[" + selectedSlot + "]", "> [" + selectedSlot + "]");

        return text;
    }

    public static String getFormat(String name) {
        return lang.getString(name);
    }

    public static JSONObject trendingPlot(String stage) {
        return new JSONObject(String.format("""
                {
                    "message": "[Log] Trending for %s",
                    "color": {
                        "r": 255,
                        "g": 0,
                        "b": 0,
                        "a": 100
                    }
                }
                """, stage));
    }

    public static JSONObject getPlot(String name) {
        if (! name.startsWith("plot."))
            return stages.getJSONObject("plot." + name);
        else
            return stages.getJSONObject(name);
    }

    public static LinkedHashSet<Plot> getPlotDetails(JSONObject plot) {
        LinkedHashSet<Plot> plots = new LinkedHashSet<>();
        JSONArray plotsSource = plot.getJSONArray("details");
        for (Object o : plotsSource) {
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
            config = new DiskObjectConfigUtil(BARNACLE, "config/", "barnacle", false);

            running = true;

            StringBuilder s = new StringBuilder();
            String cache;

            BufferedReader br = new BufferedReader(new InputStreamReader(Resources.getResource("/format/constants.json", Resources.class), StandardCharsets.UTF_8));

            while ((cache = br.readLine()) != null) {
                s.append(cache).append("\n");
            }

            resourceJson = new JSONObject(s.toString());

            lang = resourceJson.getJSONObject("lang").getJSONObject(language.getName());
            stages = resourceJson.getJSONObject("stage");
            trends = resourceJson.getJSONObject("trend");
            npcs = resourceJson.getJSONObject("roles");

            br = new BufferedReader(new InputStreamReader(Resources.getResource("/textures/atlas.json", Resources.class), StandardCharsets.UTF_8));
            s = new StringBuilder();

            while ((cache = br.readLine()) != null) {
                s.append(cache).append("\n");
            }

            texturesJson = new JSONObject(s.toString());

            roles.put(formatConstant("$n1.identifier"), new PlayerEntity(npcs.getJSONObject("n1")));
            roles.put(formatConstant("$n2.identifier"), new PlayerEntity(npcs.getJSONObject("n2")));
            //            try {
            //                players = new Players(new JSONObject(config.getConfigValue("players")));
            //            } catch(Exception ex) {
            //
            //            }

            trend = new Trend("stage.starter", trends.getJSONObject("stage.starter"));

            try {
                levels = new LevelStorage(new JSONObject(config.getConfigString("levels")));
            } catch (Exception ex) {

            }

            config.set("roles", roles.toJSONObject());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String formatConstant(String source) {
        return source.replace("$n1.name", getNpcName(1)).replace("$n2.name", getNpcName(2)).replace("$n1.alias", getNpcAlias(1)).replace("$n2.alias", getNpcAlias(2)).replace("$n1.identifier", getNpcIdentifier(1)).replace("$n2.identifier", getNpcIdentifier(2));
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

    public static Language getLanguage() {
        return language;
    }
}
