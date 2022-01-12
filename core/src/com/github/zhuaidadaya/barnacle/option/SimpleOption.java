package com.github.zhuaidadaya.barnacle.option;

import com.github.zhuaidadaya.barnacle.events.ExpectEvent;
import com.github.zhuaidadaya.barnacle.events.FavorEvent;
import org.json.JSONObject;

import java.util.LinkedHashSet;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class SimpleOption {
    private final LinkedHashSet<String> requireSelectedOption = new LinkedHashSet<>();
    private String jumpTrend;
    private String invoke;
    private String name;
    private String tip;
    private boolean canOption = true;
    private FavorEvent favor;
    private ExpectEvent expect;

    public SimpleOption(String name, JSONObject json) {
        try {
            this.name = name;
            jumpTrend = json.getString("trend");
            invoke = json.getString("invoke");
            try {
                for(Object o : json.getJSONArray("require")) {
                    requireSelectedOption.add(o.toString());
                }
            } catch (Exception ex) {

            }

            try {
                JSONObject event = json.getJSONObject("event");

                try {
                    JSONObject favor = event.getJSONObject("favor");

                    this.favor = new FavorEvent(favor);
                } catch (Exception ex) {

                }

                try {
                    JSONObject expect = event.getJSONObject("expect");
                    this.expect = new ExpectEvent(expect);
                } catch (Exception ex) {

                }
            } catch (Exception ex) {

            }

            try {
                tip = json.getString("tip");
            } catch (Exception ex) {

            }

            try {
                int loopLeastRequirement = -1;
                try {
                    JSONObject requirement = json.getJSONObject("requirement");
                    loopLeastRequirement= requirement.getJSONObject("loop").getInt("least");
                }catch (Exception e) {

                }

                if(!(loopLeastRequirement == -1)) {
                    if(Integer.parseInt(config.getConfigValue("loop")) < loopLeastRequirement) {
                        canOption = false;
                    }
                }
            } catch(Exception ex) {
                canOption = false;
            }
        } catch (Exception e) {
            canOption = false;
        }
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("trend", jumpTrend);
        json.put("invoke", invoke);
        return json;
    }

    public boolean canOption() {
        if(requireSelectedOption.size() > 0) {
            for(String s : requireSelectedOption) {
                if(! selectedOptions.contains(s))
                    return false;
            }
        }
        return canOption;
    }

    public JSONObject trend() {
        selectedOptions.add(name);

        try {
            expect.apply();
        } catch (Exception e) {

        }

        try {
            favor.apply();
        }catch (Exception e) {

        }

        try {
            drawPlotTip = getFormat(tip).toString();
        } catch (Exception e) {

        }

        return getTrend(jumpTrend);
    }

    public String trendName() {
        return jumpTrend;
    }

    public String formatInvoke() {
        return getFormat(invoke).toString();
    }
}
