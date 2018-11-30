package com.wurmcraft.towers.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.wurmcraft.towers.Towers;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Towers";
		config.width = Towers.WIDTH;
		config.height = Towers.HEIGHT;
		new LwjglApplication(new Towers(), config);
	}
}
