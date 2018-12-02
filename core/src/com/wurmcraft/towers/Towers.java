package com.wurmcraft.towers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.towers.gui.MenuGui;
import com.wurmcraft.towers.json.Local;
import com.wurmcraft.towers.json.Settings;

public class Towers extends Game {

    // Config
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public static Settings settings;
    public static Local local;
    // Singleton's
    public static SpriteBatch batch;
    public static BitmapFont font;
    public static Gson gson;
    // Gui's
    public static MenuGui menuGui;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        gson = new GsonBuilder().setPrettyPrinting().create();
        settings = gson.fromJson(Gdx.files.internal("settings.json").reader(), Settings.class);
        font.getData().setScale(settings.fontScale);
        local = gson.fromJson(Gdx.files.internal("lang/en_us.json").reader(), Local.class);
        setScreen(menuGui = new MenuGui(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}

