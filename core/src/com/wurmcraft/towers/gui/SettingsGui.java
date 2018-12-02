package com.wurmcraft.towers.gui;

import com.badlogic.gdx.Screen;
import com.wurmcraft.towers.Towers;
import com.wurmcraft.towers.render.RenderUtils;

public class SettingsGui implements Screen {

    private Towers towers;

    public SettingsGui(Towers towers) {
        this.towers = towers;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        RenderUtils.clearScreen();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
