package com.github.zhuaidadaya.barnacle.trend;

import com.github.zhuaidadaya.MCH.log.Logger;
import com.github.zhuaidadaya.barnacle.events.ExpectEvent;
import com.github.zhuaidadaya.barnacle.events.FavorEvent;
import com.github.zhuaidadaya.barnacle.level.Level;
import com.github.zhuaidadaya.barnacle.option.SimpleOption;
import com.github.zhuaidadaya.barnacle.plot.Plot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.LinkedHashSet;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class Trend {
    public static Logger logger_plot = new Logger("Barnacle Plot");
    public static Logger logger_trend = new Logger("Barnacle Trend");

    private LinkedHashSet<SimpleOption> options = new LinkedHashSet<>();
    private String trend;
    private String name;
    private String invoke;
    private String jumpTrendTarget;
    private boolean canTrend = true;
    private boolean trendWait = false;
    private boolean optionWait = false;
    private boolean trendSkip = false;

    private long lastContinue = - 1;
    private long lastSkip = - 1;
    private int latestPlotStep = 0;

    private boolean jumpTrend = false;

    private ExpectEvent expect;
    private FavorEvent favor;

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
        if(expect != null) {
            expect.apply();
            expect = null;
        }

        if(favor != null) {
            favor.apply();
            favor = null;
        }

        this.name = name;
        boolean next = false;
        try {
            trend = json.getString("trend");
            if(json.getBoolean("directly")) {
                initTrend(trend, getTrend(trend));
                next = true;
            }
        } catch (Exception e) {

        }

        try {
            JSONObject event = json.getJSONObject("event");

            try {
                expect = new ExpectEvent(event.getJSONObject("expect"));
            } catch (Exception e) {

            }

            try {
                favor = new FavorEvent(event.getJSONObject("favor"));
            } catch (Exception ex) {

            }
        } catch (Exception ex) {

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

    public void jumpTrend(String jumpTrendTarget, String trendName, JSONObject trendJson) {
        this.jumpTrendTarget = jumpTrendTarget;
        initTrend(trendName, trendJson);
        jumpTrend = true;
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
        if(System.currentTimeMillis() - lastContinue > 1000) {
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
        if(System.currentTimeMillis() - lastSkip > 1000) {
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
        jumpTrend = false;
        int steps = 0;
        LinkedHashSet<Plot> plots = getPlotDetails(getPlot(invoke));
        trendSkip = false;
        if(! trendWait) {
            for(Plot plot : plots) {
                if(jumpTrend)
                    break;
                if(! trendSkip) {
                    if(! (latestPlotStep > steps)) {
                        if(! plot.isMonotonous()) {
                            drawPlotTitle = plot.getTitle();
                            drawPlotSubtitle = plot.getSubtitle();
                            drawPlotTip = plot.getTip();
                            if(plot.hasBackground())
                                drawBackground = plot.getBackground();
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

                        long waitTime = 0;

                        waitingProgressBarMax = 14000;

                        while(trendWait) {
                            if(jumpTrend || trendSkip || waitTime > 14000) {
                                if(launched) {
                                    break;
                                }
                            }

                            waitingProgressBarValue = waitTime;

                            try {
                                Thread.sleep(25);
                                if(! pause)
                                    waitTime += 25;
                            } catch (InterruptedException e) {

                            }
                        }
                        lastContinue = System.currentTimeMillis();
                        //                        drawPlotTip = "saving";

                        //                        config.set("latest_plot_step", steps);
                        //                        config.set("latest_plot", name);
                        //                        config.set("players", players.toJSONObject());
                        latestPlotStep = steps;

                        //                        drawPlotTip = "";
                    }
                } else {
                    String message = plot.getMessage();
                    logger_plot.info(message);
                    drawPlotMessage = message;
                    drawPlotTip = "skip";
                }
                steps++;
            }

            //            drawPlotTip = "saving";
            //
            //            config.set("latest_plot_step", steps);
            //            config.set("latest_plot", name);

            trendSkip = false;
            drawPlotTip = "";

            if(! jumpTrend && options.size() > 0) {
                optionsButton = new LinkedHashSet<>();
                boolean lowerCanOption = false;
                for(SimpleOption option : options) {
                    if(option.canOption()) {
                        lowerCanOption = true;
                        optionsButton.add(option);
                    }
                }

                if(optionsButton.size() > 0) {

                    willOptionIndex = new SecureRandom().nextInt(optionsButton.size());
                    randomDefaultOption = (SimpleOption) optionsButton.toArray()[willOptionIndex];
                    willOption = randomDefaultOption;

                    if(! lowerCanOption) {
                        canTrend = false;
                    } else {
                        optionWait = true;
                    }

                    long waitTime = 0;

                    waitingProgressBarMax = 10000;

                    while(optionWait) {
                        if(waitTime > 10000) {
                            if(launched) {
                                trendWillOption();
                            }
                        }

                        waitingProgressBarValue = waitTime;

                        try {
                            Thread.sleep(25);
                            if(! pause)
                                waitTime += 25;
                        } catch (InterruptedException e) {

                        }
                    }
                } else {
                    initTrend(trend, getTrend(trend));
                }
            } else {
                if(jumpTrend)
                    initTrend(jumpTrendTarget, getTrend(jumpTrendTarget));
                else
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
        json.put("trend", trend);
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

    public String getName() {
        return name;
    }
}