package com.github.zhuaidadaya.barnacle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.zhuaidadaya.Test;
import com.github.zhuaidadaya.barnacle.gui.BarnacleGraphics;
import com.github.zhuaidadaya.barnacle.storage.Variables;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class DesktopLauncher {
	public static void launcher() {
		logger.info("launching " + entrust);
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = frameWidth;
		config.height = frameHeight;
		config.foregroundFPS = 360;
		config.backgroundFPS = 30;
		LwjglApplication app = new LwjglApplication(new BarnacleGraphics(), config);
		new Thread(() -> {
			while(running) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {

				}
			}
			app.exit();
		}).start();
	}

	public static void launch() {
		initVariables();

		launcher();

		try {
			//            BarnacleFrame.init();

			new Thread(() -> {
				logger.info("started trend tread");
				while(trend.canTrend()) {
					trend.trend();
				}
			}).start();

		} catch (Exception e) {

		}
	}

	public static void main(String[] args) {
		Test.test();
		launch();
	}
}
