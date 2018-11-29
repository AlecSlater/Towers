package com.wurmcraft.towers.game.api;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Entity extends Actor {

    public Body body;
    public Texture texture;
    public int hp;

    public Entity(Body body, Texture texture) {
        this.body = body;
        this.texture = texture;
        hp = 2;
    }

    public Entity(Body body, Texture texture, int hp) {
        this.body = body;
        this.texture = texture;
        this.hp = hp;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, body.getPosition().x, body.getPosition().y);
    }
}
