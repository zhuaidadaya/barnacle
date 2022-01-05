package com.github.zhuaidadaya.barnacle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.zhuaidadaya.barnacle.BarnacleGraphics;
import com.github.zhuaidadaya.barnacle.storage.Variables;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class DesktopLauncher {
	public static void launcher() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = frameWidth;
		config.height = frameHeight;
		config.foregroundFPS = 360;
		config.backgroundFPS = 10;
		new LwjglApplication(new BarnacleGraphics(), config);
	}
}
