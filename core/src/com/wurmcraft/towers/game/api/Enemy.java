package com.wurmcraft.towers.game.api;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class Enemy extends Entity {

    public int damage;
    public int movementSpeed;

    public Enemy(Animation<TextureRegion> texture, Body body, int hp, int damage, int movmentSpeed) {
        super(texture, body, hp);
        this.damage = damage;
        this.movementSpeed = movmentSpeed;
    }
}
