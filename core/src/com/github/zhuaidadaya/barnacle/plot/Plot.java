package com.github.zhuaidadaya.barnacle.plot;

import com.badlogic.gdx.graphics.*;
import com.github.zhuaidadaya.barnacle.math.*;
import com.github.zhuaidadaya.barnacle.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.json.JSONObject;

import static com.github.zhuaidadaya.barnacle.storage.Variables.formatConstant;
import static com.github.zhuaidadaya.barnacle.storage.Variables.getFormat;

public class Plot {
    private String tip = "";
    private String message = "";
    private String title = "";
    private String subtitle = "";
    private String background = "";
    private String leftNpc = "";
    private String rightNpc = "";
    private Color color;
    private boolean drawDialogBox = true;
    private boolean monotonous = false;
    private int time = 10000;

    public Plot(String message) {
        this.message = message;
        monotonous = true;
    }

    public Plot(JSONObject json) {
        EntrustExecution.tryTemporary(() -> {
            this.message = getFormat(json.getString("message"));
        });

        EntrustExecution.tryTemporary(() -> {
            this.title = json.getString("title");
        });

        EntrustExecution.tryTemporary(() -> {
            this.subtitle = json.getString("subtitle");
        });

        EntrustExecution.tryTemporary(() -> {
            this.tip = json.getString("tip");
        });

        EntrustExecution.tryTemporary(() -> {
            this.background = json.getString("background");
        });

        EntrustExecution.tryTemporary(() -> {
            this.leftNpc = json.getString("npc_left");
        });

        EntrustExecution.tryTemporary(() -> {
            this.rightNpc = json.getString("npc_right");
        });

        EntrustExecution.tryTemporary(() -> {
            this.drawDialogBox = json.getBoolean("dialog");
        });

        EntrustExecution.tryTemporary(() -> {
            this.time = json.getInt("time");
        });

        EntrustExecution.tryTemporary(() -> {
            JSONObject c = json.getJSONObject("color");
            this.color = new Color(Mathematics.Percentage.rgb(c.getInt("r")) , Mathematics.Percentage.rgb(c.getInt("g")),Mathematics.Percentage.rgb( c.getInt("b")), Mathematics.Percentage.alpha(EntrustParser.tryCreate(() -> c.getInt("a"), 100)));
        });
    }

    public String rightNpc() {
        return rightNpc;
    }

    public String leftNpc() {
        return leftNpc;
    }

    public String tip() {
        return tip;
    }

    public String title() {
        return title;
    }

    public String subtitle() {
        return subtitle;
    }

    public String background() {
        return background;
    }

    public String message() {
        return formatConstant(message);
    }

    public boolean monotonous() {
        return monotonous;
    }

    public boolean hasBackground() {
        return ! background.equals("");
    }

    public boolean hasLeftNpc() {
        return ! leftNpc.equals("");
    }

    public boolean hasRightNpc() {
        return ! rightNpc.equals("");
    }

    public boolean dialogBox() {
        return drawDialogBox;
    }

    public int time() {
        return time;
    }

    public Color color() {
        return color;
    }
}
