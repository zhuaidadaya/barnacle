package com.github.zhuaidadaya.barnacle.plot;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class Plot {
    private String tip = "";
    private String message = "";
    private String title = "";
    private String subtitle= "";
    private String background = "";
    private String leftNpc = "";
    private String rightNpc = "";
    private boolean drawDialogBox = true;
    private boolean monotonous = false;
    private int time = 14000;

    public Plot(String message) {
        this.message = message;
        monotonous = true;
    }

    public Plot(JSONObject json) {
        try {
            this.message = json.getString("message");
        } catch (Exception e) {

        }

        try {
            this.title = json.getString("title");
        } catch (Exception e) {

        }

        try {
            this.subtitle = json.getString("subtitle");
        } catch (Exception e) {

        }

        try {
            this.tip = json.getString("tip");
        } catch (Exception e) {

        }

        try {
            this.background = json.getString("background");
        } catch (Exception e) {

        }

        try {
            this.leftNpc = json.getString("npc_left");
        } catch (Exception e) {

        }

        try {
            this.rightNpc = json.getString("npc_right");
        } catch (Exception e) {

        }

        try {
            this.drawDialogBox = json.getBoolean("dialog");
        } catch (Exception e) {

        }

        try {
            this.time = json.getInt("time");
        } catch(Exception e) {

        }
    }

    public String getRightNpc() {
        return rightNpc;
    }

    public String getLeftNpc() {
        return leftNpc;
    }

    public String getTip() {
        return tip;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getBackground() {
        return background;
    }

    public String getMessage() {
        return message;
    }

    public boolean isMonotonous() {
        return monotonous;
    }

    public boolean hasBackground() {
        return !background.equals("");
    }

    public boolean hasLeftNpc() {
        return !leftNpc.equals("");
    }

    public boolean hasRightNpc() {
        return !rightNpc.equals("");
    }

    public boolean drawDialogBox() {
        return drawDialogBox;
    }

    public int getTime() {
        return time;
    }
}
