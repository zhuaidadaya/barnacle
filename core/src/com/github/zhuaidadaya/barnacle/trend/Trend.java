package com.github.zhuaidadaya.barnacle.trend;

import com.github.zhuaidadaya.MCH.log.Logger;
import com.github.zhuaidadaya.barnacle.gui.BarnacleFrame;
import com.github.zhuaidadaya.barnacle.option.SimpleOption;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashSet;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class Trend {
    public static Logger logger_plot = new Logger("Barnacle Plot");
    public static Logger logger_trend = new Logger("Barnacle Trend");

    private LinkedHashSet<SimpleOption> options = new LinkedHashSet<>();
    private String trend;
    private String name;
    private String invoke;
    private boolean canTrend = true;
    private boolean trendWait = false;
    private boolean optionWait = false;
    private boolean trendSkip = false;

    public Trend(String name, JSONObject json) {
        initTrend(name, json);
    }

    private Trend(String name, SimpleOption... options) {

    }

    public void initTrend(String name, JSONObject json) {
        this.name = name;
        logger_trend.info("trend to: " + name);
        boolean next = false;
        try {
            trend = json.getString("trend");
            if(json.getBoolean("directly")) {
                initTrend(trend, getTrend(trend));
                next = true;
            }
        } catch (Exception e) {

        }

        if(! next) {
            this.invoke = json.getString("invoke");
            this.options = new LinkedHashSet<>();
            try {
                JSONObject options = json.getJSONObject("options");
                for(Object o : options.keySet())
                    this.options.add(new SimpleOption(o.toString(), options.getJSONObject(o.toString())));
            } catch (Exception e) {

            }

            trendWait = false;
            optionWait = false;

            optionsButton = new LinkedHashSet<>();
            BarnacleFrame.apply();
        }
    }

    public void trendContinue() {
        trendWait = false;
    }

    public void trendSkip() {
        trendSkip = true;
    }

    public void trend() {
        JSONArray plots = getPlotDetails(getPlot(invoke));
        if(! trendWait) {
            for(Object o : plots) {
                if(! trendSkip) {
                    logger_plot.info(o.toString());
                    BarnacleFrame.setText(o.toString());
                    trendWait = true;

                    long waitTime = System.currentTimeMillis();

                    while(trendWait) {
                        if(trendSkip) {
                            break;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {

                        }
                    }
                } else {
                    logger_plot.info(o.toString());
                    BarnacleFrame.setText(o.toString());
                }
            }

            trendSkip = false;

            if(options.size() > 0) {
                boolean lowerCanOption = false;
                for(SimpleOption option : options) {
                    if(option.canOption()) {
                        logger_plot.info(option.trendName());
                        lowerCanOption = true;
                        optionsButton.add(option);
                    }
                }

                BarnacleFrame.apply();

                if(! lowerCanOption) {
                    canTrend = false;
                } else {
                    optionWait = true;
                }

                while(optionWait) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }
            } else {
                initTrend(trend, getTrend(trend));
            }
        }
    }

    public void print() {
        System.out.println(name);
        System.out.println("-----");
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        JSONArray options = new JSONArray();
        json.put("name", name);
        for(SimpleOption option : this.options) {
            options.put(option.toJSONObject());
        }
        json.put("options", options);
        return json;
    }

    public boolean canTrend() {
        return canTrend;
    }
}