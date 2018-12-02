package com.wurmcraft.towers.game.api;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class Block extends Entity {

    public Block(Animation<TextureRegion> texture, Body body, int hp) {
        super(texture, body, hp);
    }

    @Override
    public void render(Stage stage, float time) {
        stage.getBatch().draw((texture.getKeyFrame((hp / 4) * 4, true)), body.getPosition().x, body.getPosition().y);
    }

    @Override
    public void update(Array<Body> entities) {

    }

    @Override
    public void applyDamage(double amount) {

    }

    @Override
    public void kill() {

    }
}
