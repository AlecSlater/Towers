package com.wurmcraft.towers.game.api;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Block extends Entity {

    public Block(Animation texture, Body body, int hp) {
        super(texture, body, hp);
    }

    @Override
    public void render(Stage stage, float time) {
       stage.getBatch().draw((TextureRegion) texture.getKeyFrame(4 / hp, true), body.getPosition().x, body.getPosition().y);
    }
}
