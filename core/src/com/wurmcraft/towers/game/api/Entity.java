package com.wurmcraft.towers.game.api;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public abstract class Entity extends Actor {

    // Rendering
    protected Animation<TextureRegion> texture;
    // Physics
    public Body body;
    // Game Data
    public int hp;

    public Entity(Animation<TextureRegion> texture, Body body, int hp) {
        this.texture = texture;
        this.body = body;
        this.hp = hp;
    }

    public abstract void update(Array<Body> entities);

    public void render(Stage stage, float time) {
        stage.getBatch().draw(texture.getKeyFrame(time, true), body.getPosition().x, body.getPosition().y);
    }

    public abstract void applyDamage(double amount);

    public abstract void kill();


    public enum Type {
        ENEMY, BLOCK, TOWER
    }
}
