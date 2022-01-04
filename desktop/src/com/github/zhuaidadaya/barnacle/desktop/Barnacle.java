package com.github.zhuaidadaya.barnacle.desktop;

import com.github.zhuaidadaya.MCH.Resources;
import com.github.zhuaidadaya.barnacle.gui.BarnacleFrame;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class Barnacle {
    public static void main(String[] args) {
//        try {
//            System.loadLibrary("jogl");
//        } catch(Exception e) {
//            logger.info("failed to load library");
//        }

        initVariables();

        DesktopLauncher.launcher();

        try {
//            BarnacleFrame.init();

            new Thread(() -> {
                while(trend.canTrend()) {
                    trend.trend();
                }
            }).start();
        } catch (Exception e) {

        }
    }
}
