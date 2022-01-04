package com.github.zhuaidadaya.barnacle.option;

import com.github.zhuaidadaya.barnacle.entity.PlayerEntity;
import com.github.zhuaidadaya.barnacle.gui.BarnacleFrame;
import org.json.JSONObject;

import java.util.LinkedHashSet;
import java.util.Scanner;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class SimpleOption {
    private final LinkedHashSet<String> requireSelectedOption = new LinkedHashSet<>();
    private String jumpTrend;
    private String invoke;
    private String name;
    private String tip;
    private boolean canOption = true;
    private int favor = 0;
    private int except = 0;

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
                    favor = event.getInt("favor");
                } catch(Exception ex) {

                }

                try {
                    except = event.getInt("expect");
                } catch(Exception ex) {

                }
            } catch(Exception ex) {

            }

            try {
                tip = json.getString("tip");
            } catch(Exception ex) {

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

    public boolean request() {
        Scanner sc = new Scanner(System.in);
        logger.info(getFormat(invoke));
        return sc.nextBoolean();
    }

    public JSONObject trend() {
        selectedOptions.add(name);
        PlayerEntity playerN2 = players.get(formatConstant("$n2.name"));
        PlayerEntity playerN1 = players.get(formatConstant("$n1.name"));
        playerN2.changeExpect(playerN1.getIdentifier(), except);
        playerN2.changeFavor(playerN1.getIdentifier(), favor);

        players.put(formatConstant("$n2.name"),playerN2);

        config.set("players", players.toJSONObject());

        try {
            BarnacleFrame.setTip(getFormat(tip).toString());
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
