package com.wurmcraft.towers.game.api;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.wurmcraft.towers.Towers;

public abstract class Entity extends Actor {

    // Rendering
    protected Animation texture;
    // Physics
    public Body body;
    // Game Data
    protected int hp;

    public Entity(Animation texture, Body body, int hp) {
        this.texture = texture;
        this.body = body;
        this.hp = hp;
    }

    public void update() {

    }

    public void render(Stage stage, float time) {
        stage.getBatch().draw((TextureRegion) texture.getKeyFrame(time, true), body.getPosition().x, body.getPosition().y);
    }

    public void applyDamage(double amount) {

    }

    public void kill() {

    }


    public enum Type {
        ENEMY, BLOCK, TOWER
    }
}
