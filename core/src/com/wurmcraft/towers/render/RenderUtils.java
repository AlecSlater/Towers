package com.wurmcraft.towers.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RenderUtils {

    public static void clearScreen() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static Animation getAnimationForEntity(int data, TextureRegion[][] textureRegion, int speed) {
        int firstRow = data / 6;
        int startingIndex = data % 3;
        TextureRegion[] textures = new TextureRegion[3];
        textures[0] = textureRegion[firstRow][startingIndex];
        textures[1] = textureRegion[firstRow][++startingIndex];
        textures[2] = textureRegion[firstRow][++startingIndex];
        return new Animation(1f / (3f * speed), textures);
    }
}
