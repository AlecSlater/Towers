package com.wurmcraft.towers.game.api;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class Tower extends Entity {

    public int nextShoot = 2;

    public Tower(Animation texture, Body body, int hp) {
        super(texture, body, hp);
    }

    @Override
    public void render(Stage stage, float time) {
        if (nextShoot <= 0) {
            stage.getBatch().draw(((TextureRegion) texture.getKeyFrame(4, true)), body.getPosition().x, body.getPosition().y);
        } else
            stage.getBatch().draw(((TextureRegion) texture.getKeyFrame(hp / 4, true)), body.getPosition().x, body.getPosition().y);
    }

    @Override
    public void update(Array<Body> entities) {
        for (Body body : entities) {
            if (body.getUserData() instanceof com.wurmcraft.towers.game.api.Enemy) {
                int distanceX = (int) (getX() - body.getPosition().x);
                if (Math.abs(distanceX) <= 600) {
                    if (nextShoot <= 0) {
                        com.wurmcraft.towers.game.api.Enemy enemy = (Enemy) body.getUserData();
                        enemy.hp--;
                        nextShoot = 2;
                    } else
                        nextShoot--;
                }
            }
        }
    }


}
