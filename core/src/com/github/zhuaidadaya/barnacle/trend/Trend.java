package com.github.zhuaidadaya.barnacle.trend;

import com.github.zhuaidadaya.MCH.log.Logger;
import com.github.zhuaidadaya.barnacle.option.SimpleOption;
import com.github.zhuaidadaya.barnacle.plot.Plot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;
import static com.github.zhuaidadaya.barnacle.storage.Variables.trend;

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

    private long lastContinue = - 1;
    private long lastSkip = - 1;
    private int latestPlotStep = 0;

    public Trend(String name, JSONObject json) {
        initTrend(name, json);
    }

    private Trend(String name, SimpleOption... options) {

    }

    /**
     * 初始化进展, 在正常跳转时使用
     *
     * @param name
     * @param json
     *
     * @author 草awa
     * @author 草二号机
     */
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
            latestPlotStep = 0;

            lastSkip = System.currentTimeMillis();

            optionsButton = new LinkedHashSet<>();
        }
    }

    /**
     * 同步进展的位置, 直接跳转时使用
     *
     * @param steps
     *         同步进展位置
     *
     * @author 草awa
     */
    public void setLatestPlotStep(int steps) {
        latestPlotStep = steps;
    }

    /**
     * 继续处理进展的位置
     *
     * @author 草awa
     */
    public void trendContinue() {
        if(System.currentTimeMillis() - lastContinue > 500) {
            trendWait = false;
            lastContinue = System.currentTimeMillis();
        }
    }

    /**
     * 跳过这一个进展
     *
     * @author 草awa
     */
    public void trendSkip() {
        if(System.currentTimeMillis() - lastSkip > 500) {
            trendSkip = true;
            lastSkip = System.currentTimeMillis();
        }
    }

    /**
     * 处理下一个进展
     *
     * @author 草awa
     * @author 草二号机
     */
    public void trend() {
        int steps = 0;
        LinkedHashSet<Plot> plots = getPlotDetails(getPlot(invoke));
        boolean first = false;
        trendSkip = false;
        if(! trendWait) {
            for(Plot plot : plots) {
                if(! trendSkip | ! first) {
                    if(! (latestPlotStep > steps)) {
                        first = true;

                        if(! plot.isMonotonous()) {
                            if(plot.hasTitle())
                                drawPlotTitle = plot.getTitle();
                            if(plot.hasSubtitle())
                                drawPlotSubtitle = plot.getSubtitle();
                            if(plot.hasBackground())
                                drawBackground = plot.getBackground();
                            if(plot.hasTip())
                                drawPlotTip = plot.getTip();
                            if(plot.hasRightNpc())
                                drawPlotRightNpc = plot.getRightNpc();
                            if(plot.hasLeftNpc())
                                drawPlotLeftNpc = plot.getLeftNpc();
                            drawDialogBox = plot.drawDialogBox();
                        }

                        String message = plot.getMessage();
                        logger_plot.info(message);
                        drawPlotMessage = message;
                        trendWait = true;

                        long waitTime = System.currentTimeMillis();

                        while(trendWait) {
                            if(trendSkip || (System.currentTimeMillis() - waitTime) > 10000) {
                                break;
                            }
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {

                            }
                        }
                        drawPlotTip = "saving";

                        config.set("latest_plot_step", steps);
                        config.set("latest_plot", name);
                        config.set("players", players.toJSONObject());
                        latestPlotStep = steps;

                        drawPlotTip = "";
                    }
                } else {
                    String message = plot.getMessage();
                    logger_plot.info(message);
                    drawPlotMessage = message;
                    drawPlotTip = "skip";
                }
                steps++;
            }

            drawPlotTip = "saving";

            config.set("latest_plot_step", steps);
            config.set("latest_plot", name);

            trendSkip = false;
            drawPlotTip = "";

            if(options.size() > 0) {
                optionsButton = new LinkedHashSet<>();
                boolean lowerCanOption = false;
                for(SimpleOption option : options) {
                    if(option.canOption()) {
                        lowerCanOption = true;
                        optionsButton.add(option);
                    }
                }

                willOptionIndex = new SecureRandom().nextInt(optionsButton.size());
                randomDefaultOption = (SimpleOption) optionsButton.toArray()[willOptionIndex];
                willOption = randomDefaultOption;

                if(! lowerCanOption) {
                    canTrend = false;
                } else {
                    optionWait = true;
                }

                long waitTime = System.currentTimeMillis();

                while(optionWait) {
                    if(System.currentTimeMillis() - waitTime > 10000) {
                        trendWillOption();
                    }
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

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        JSONArray options = new JSONArray();
        json.put("name", name);
        for(SimpleOption option : this.options) {
            options.put(option.toJSONObject());
        }
        json.put("options", options);
        json.put("invoke", invoke);
        json.put("latestPlotStep", latestPlotStep);
        return json;
    }

    /**
     * 检查是否可以进行下一次进展
     *
     * @return
     *
     * @author 草二号机
     */
    public boolean canTrend() {
        return canTrend;
    }

    /**
     * 检查是否正在等待选项
     *
     * @return
     *
     * @author 草二号机
     */
    public boolean optionWaiting() {
        return optionWait;
    }
}