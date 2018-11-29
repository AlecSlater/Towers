package com.wurmcraft.towers.game.api;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

public class Enemy extends Entity {

    public int type;

    public Enemy(Body body, Texture texture, int type, int hp) {
        super(body, texture);
        this.type = type;
        this.hp = hp;
    }
}
