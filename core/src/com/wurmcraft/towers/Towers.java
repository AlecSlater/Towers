package com.wurmcraft.towers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.towers.gui.MenuScreen;
import com.wurmcraft.towers.json.Local;
import com.wurmcraft.towers.json.Settings;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Towers extends Game {

    // Config
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public static Settings settings;
    public static Local local;

    public SpriteBatch batch;
    public BitmapFont font;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        settings = GSON.fromJson(Gdx.files.internal("settings.json").reader(), Settings.class);
        font.getData().setScale(settings.fontScale);
        local = GSON.fromJson(Gdx.files.internal("lang/en_us.json").reader(), Local.class);
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void render() {
        super.render();
    }
}

