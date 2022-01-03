package com.github.zhuaidadaya.barnacle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.zhuaidadaya.barnacle.BarnacleGraphics;

public class DesktopLauncher {
	public static void launcher() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new BarnacleGraphics(), config);
	}
}
